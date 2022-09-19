package com.useanvil.examples.runnable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.client.GraphqlClient;
import com.useanvil.examples.entity.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// Example: Create an e-sign packet via the Anvil API
//
// * E-sign API docs: https://www.useanvil.com/docs/api/e-signatures
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar create-etch-packet <your.real.email@ex.com>
//
// This script will create an e-sign packet with one signer and two documents.
// Then it will send a signature request to the email you specified.
// Use your real email address!
//
// This script will:
//
// * Use a template sample PDF document
// * Upload a new PDF document
// * Insert data (names, emails, addresses, etc) on both PDFs
// * Have the signer sign both documents
//
// After you run the script, you can find the new packet from the e-sign "Sent"
// area in your dashboard. The dashboard URL to the new packet will be output as
// well.

public class CreateEtchESignPacket implements IRunnable {
    ObjectMapper _objectMapper = new ObjectMapper();

    private byte[] getBase64File(String filePath) {
        byte[] encodedFile;
        try {
            encodedFile = Base64.getUrlEncoder().encode(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return encodedFile;
    }

    private List<EtchSigner> getSigners(String signerName, String signerEmail) {
        ArrayList<EtchSigner> signers = new ArrayList<>();
        // Signers will sign in the order they are specified in this array.
        // e.g. `employer` will sign after `employee` has finished signing
        EtchSigner signer = new EtchSigner();

        // `employee` is the first signer
        signer.id = "signer1";
        signer.name = signerName;
        signer.email = signerEmail;
        signer.signerType = "email";

        // These fields will be presented when this signer signs.
        // The signer will need to click through the signatures in
        // the order of this array.
        ArrayList<SignerField> signerFields = new ArrayList<>();
        // File IDs are specified in the `files` property above
        signerFields.add(new SignerField("sampleTemplate", "signature"));
        signerFields.add(new SignerField("sampleTemplate", "signatureInitial"));
        signerFields.add(new SignerField("sampleTemplate", "signatureDate"));
        signerFields.add(new SignerField("sampleTemplate", "signerName"));
        signerFields.add(new SignerField("sampleTemplate", "signerEmail"));
        // Two more for the other one
        signerFields.add(new SignerField("fileUploadNDA", "recipientSignature"));
        signerFields.add(new SignerField("fileUploadNDA", "recipientSignatureDate"));

        signer.fields = signerFields.toArray(new SignerField[0]);

        signers.add(signer);

        return signers;
    }

    private PayloadData getPayloadData(String signerName, String signerEmail) {
        var data = new PayloadData();
        var sampleTemplateData = new HashMap<String, Serializable>();
        sampleTemplateData.put("name", signerName);
        sampleTemplateData.put("email", signerEmail);

        // 'sampleTemplate' is the sample template ID specified above
        var sampleTemplate = new HashMap<String, Serializable>();
        sampleTemplate.put("data", sampleTemplateData);

        data.payloads.put("sampleTemplate", sampleTemplate);

        return data;
    }

    private CreateEtchPacket getPacketVariables(String pdfTemplateEid, String signerName, String signerEmail) {
        String res;
        CreateEtchPacket packetPayload = new CreateEtchPacket();

        // Subject & body of the emails to signers
        packetPayload.name = "Test Docs - " + signerName;
        packetPayload.signatureEmailSubject = "Custom email subject";
        packetPayload.signatureEmailBody = "Custom please sign these documents....";

        // Merge all PDFs into one PDF before signing.
        // Signing users will get one PDF instead of all PDFs as separate files.
        // packetPayload.mergePDFs = false;

        // Gather all file data. We'll use this in the final payload below.
        List<IAttachable> etchFiles = this.getFiles(pdfTemplateEid);
        List<EtchSigner> signers = this.getSigners(signerName, signerEmail);

        // This data will fill the PDF before it's sent to any signers.
        // IDs here were set up on each field while templatizing the PDF.
        PayloadData data = this.getPayloadData(signerName, signerEmail);

        packetPayload.files = etchFiles.toArray(new IAttachable[0]);
        packetPayload.signers = signers.toArray(new EtchSigner[0]);
        packetPayload.data = data;

        return packetPayload;
    }

    private List<IAttachable> getFiles(String pdfTemplateEid) {
        ArrayList<IAttachable> ret = new ArrayList<>();
        EtchCastRef ref = new EtchCastRef("sampleTemplate", pdfTemplateEid);

        ArrayList<CastField> fields = new ArrayList<>();

        // This is a file we will upload and specify the fields ourselves
        fields.add(new CastField("recipientName", "fullName", new Rect(223, 120, 12, 140), 0));
        fields.add(new CastField("recipientEmail", "email", new Rect(367, 120, 12, 166), 1));
        fields.add(new CastField("recipientSignatureName", "fullName", new Rect(107, 374, 22, 157), 1));
        fields.add(new CastField("recipientSignature", "signature", new Rect(270, 374, 22, 142), 1));
        fields.add(new CastField("recipientSignatureDate", "signatureDate", new Rect(419, 374, 22, 80), 1));

        // The file here is `null`, but will be replaced later at the server-level.
        // This example uses the multipart process.
        FileUpload file = new FileUpload("fileUploadNDA", "Demo NDA", null, fields.toArray(new CastField[0]));

        ret.add(ref);
        ret.add(file);

        return ret;
    }

    @Override
    public void run(String apiKey, String otherArg) {

        // The PDF template ID to fill. This PDF template ID is a sample template
        // available to anyone.
        //
        // See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
        // on setting up your own template
        String pdfTemplateId = "B5Loz3C7GVortDmn4p2P";

        // The second file is an NDA we'll upload and specify the field locations
        String ndaFilePath = "../../static/test-pdf-nda.pdf";

        // Signer information
        String signerName = "Testy Signer";
        // Signer email comes from a CLI argument
        String signerEmail = otherArg;

        CreateEtchPacket payload = this.getPacketVariables(pdfTemplateId, signerName, signerEmail);

        Path[] uploadFiles = new Path[]{ Paths.get(ndaFilePath) };

        GraphqlClient client;
        try {
            client = new GraphqlClient(apiKey);
            System.out.println("Creating Etch e-sign packet...");
            HttpResponse<String> response = client.doRequest(Paths.get("src/main/resources/mutations/create-etch-packet.graphql"), payload, uploadFiles);
            String headers = String.valueOf(response.request().headers());
            String res = response.body();
            int statusCode = response.statusCode();
            System.out.println(statusCode);
            System.out.println(headers);
            System.out.println(res);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String apiKey) throws Exception {
    }
}
