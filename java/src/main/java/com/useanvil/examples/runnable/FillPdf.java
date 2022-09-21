package com.useanvil.examples.runnable;

import com.useanvil.examples.client.RestClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.*;

// Example: Fill a PDF via the Anvil API
//
// * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
//
// This example is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar fill-pdf
//
// The filled PDF will be saved to `output/fill-output.pdf`. You can open the
// filled PDF immediately after saving the file on OSX machines with the
// `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar fill-pdf
// && open output/fill-output.pdf

public class FillPdf implements IRunnable {
    @Override
    public void run(String apiKey) {
        // The PDF template ID to fill. This PDF template ID is a sample template
        // available to anyone.
        //
        // See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
        // on setting up your own template
        String pdfTemplateEid = "B5Loz3C7GVortDmn4p2P";
        String payload;

        try {
            payload = new String(Files.readAllBytes(Paths.get("src/main/resources/payloads/pdf-fill.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RestClient client = new RestClient(apiKey);
        HttpResponse<byte[]> response;
        try {
            // response data will be the filled PDF binary data. It is important that the
            // data is saved with no encoding! Otherwise, the PDF file will be corrupt.
            response = client.fillPdf(pdfTemplateEid, payload);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(Paths.get("output/fill-output.pdf"), response.body());
            System.out.println("Fill PDF finished");
        } catch (IOException e) {
            System.out.println("Fill PDF did not finish successfully");
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String apiKey, String otherArg) throws Exception {

    }
}
