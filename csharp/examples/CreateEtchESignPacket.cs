// Example: Create an e-sign packet via the Anvil API
//
// * E-sign API docs: https://www.useanvil.com/docs/api/e-signatures
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run create-etch-packet <your.real.email@ex.com>
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

using Anvil.Client;
using Anvil.Payloads.Request.Types;
using AnvilExamples.examples;
using CreateEtchPacket = Anvil.Payloads.Request.CreateEtchPacket;

namespace AnvilExamples;

class CreateEtchESignPacket : RunnableBaseExample
{
    private string GetFileB64Bytes(string filePath)
    {
        // Convert the file bytes in into a base64 encoded string.
        // We will use this directly in the payload.
        var fileBytes = File.ReadAllBytes(filePath);
        return Convert.ToBase64String(fileBytes);
    }

    private List<IEtchPacketAttachable> GetFiles(string filePath)
    {
        return new List<IEtchPacketAttachable>()
        {
            // Use this type for existing Cast/PDF templates that you want to 
            // use for signing.
            new EtchCastRef()
            {
                // Our ID we will use to reference and fill it with data.
                // It can be any string you want!
                Id = "sampleTemplate",

                // The id to the ready-made sample template. Fields and their ids are
                // specified when building out the template in the UI.
                CastEid = "05xXsZko33JIO6aq5Pnr",
            },

            // Use this type for PDFs when you're uploading PDFs to be signed.
            // You must also provide field coordinates for where the user signs.
            new DocumentUpload()
            {
                // This is a file we will upload and specify the fields ourselves
                Id = "fileUploadNDA",
                Title = "Demo NDA",
                // The file to be uploaded.
                // This should be the file base64 encoded.
                File = new Base64FileUpload()
                {
                    Filename = "demo-nda.pdf",
                    Data = GetFileB64Bytes(filePath),
                },
                Fields = new List<SignatureField>
                {
                    new()
                    {
                        Id = "recipientName",
                        Type = "fullName",
                        Rect = new Rect
                        {
                            X = 223,
                            Y = 120,
                            Height = 12,
                            Width = 140
                        },
                        PageNum = 0,
                    },
                    new()
                    {
                        Id = "recipientEmail",
                        Type = "email",
                        Rect = new Rect
                        {
                            X = 367,
                            Y = 120,
                            Height = 12,
                            Width = 166,
                        },
                        PageNum = 0,
                    },
                    new()
                    {
                        Id = "recipientSignatureName",
                        Type = "fullName",
                        Rect = new Rect {X = 107, Y = 374, Height = 22, Width = 157},
                        PageNum = 1,
                    },
                    new()
                    {
                        Id = "recipientSignature",
                        Type = "signature",
                        Rect = new Rect {X = 270, Y = 374, Height = 22, Width = 142},
                        PageNum = 1,
                    },
                    new()
                    {
                        Id = "recipientSignatureDate",
                        Type = "signatureDate",
                        Rect = new Rect {X = 419, Y = 374, Height = 22, Width = 80},
                        PageNum = 1,
                    },
                }
            }
        };
    }

    private List<EtchSigner> GetSigners(string signerName, string signerEmail)
    {
        // Signers will sign in the order they are specified in this array.
        // e.g. `employer` will sign after `employee` has finished signing

        // `employee` is the first signer
        var signer = new EtchSigner
        {
            Id = "signer1",
            Name = signerName,
            Email = signerEmail,
            SignerType = "email",
            Fields = new SignerField[]
            {
                // File IDs are specified in the the `GetFiles` method
                new()
                {
                    FileId = "sampleTemplate",
                    FieldId = "signature",
                },
                new()
                {
                    FileId = "sampleTemplate",
                    FieldId = "signatureInitial",
                },
                new()
                {
                    FileId = "sampleTemplate",
                    FieldId = "signatureDate",
                },
                new()
                {
                    FileId = "sampleTemplate",
                    FieldId = "signerName",
                },
                new()
                {
                    FileId = "sampleTemplate",
                    FieldId = "signerEmail",
                },
                new()
                {
                    FileId = "fileUploadNDA",
                    // NDA field IDs are specified in the `files[].fields` property above
                    FieldId = "recipientSignature",
                },
                new()
                {
                    FileId = "fileUploadNDA",
                    FieldId = "recipientSignatureDate",
                }
            }
        };

        return new List<EtchSigner> {signer};
    }

    private CreateEtchPacket GetPacketVariables(string signerName, string signerEmail)
    {
        // The second file is an NDA we'll upload and specify the field locations
        var ndaFilePath = "../static/test-pdf-nda.pdf";

        // Gather all file data. We'll use this in the final payload below.
        var etchFiles = GetFiles(ndaFilePath);

        // Gather all signer data. This will use some data from the files payload above.
        var signers = GetSigners(signerName, signerEmail);

        var etchPacketPayload = new CreateEtchPacket
        {
            // Indicate the packet is all ready to send to the
            // signers. An email will be sent to the first signer.
            IsDraft = false,

            // Test packets will use development signatures and
            // not count toward your billed packets
            IsTest = true,

            // Subject & body of the emails to signers
            Name = "Test Docs - " + signerName,
            SignatureEmailSubject = "Custom email subject",
            SignatureEmailBody = "Custom please sign these documents....",

            // Merge all PDFs into one PDF before signing.
            // Signing users will get one PDF instead of all PDFs as separate files.
            // MergePdfs = false,

            // Files needs to be an array once it gets to this CreateEtchPacket type.
            Files = etchFiles.ToArray(),

            // Signers needs to be an array once it gets to this CreateEtchPacket type.
            Signers = signers.ToArray(),

            // Data to fill any fields
            Data = new
            {
                // This data will fill the PDF before it's sent to any signers.
                // IDs here were set up on each field while templatizing the PDF.
                Payloads = new
                {
                    // 'sampleTemplate' is the sample template ID specified above
                    sampleTemplate = new
                    {
                        data = new Dictionary<string, dynamic>
                        {
                            {"name", signerName},
                            {"email", signerEmail},
                            {"ssn", "456454567"},
                            {"ein", "897654321"},
                            {
                                "usAddress", new Dictionary<string, string>
                                {
                                    {"street1", "123 Main St #234"},
                                    {"city", "San Francisco"},
                                    {"state", "CA"},
                                    {"zip", "94106"},
                                    {"country", "US"},
                                }
                            }
                        }
                    },
                    // 'fileUploadNDA' is the NDA's file ID specified above
                    fileUploadNDA = new
                    {
                        fontSize = 8,
                        textColor = "#0000CC",
                        data = new
                        {
                            // The IDs here match the fields we created in the
                            // files property above
                            recipientName = signerName,
                            recipientSignatureName = signerName,
                            recipientEmail = signerEmail,
                        }
                    },
                },
            }
        };

        return etchPacketPayload;
    }

    public override async Task Run(string apiKey, string otherArg)
    {
        // The PDF template ID to fill. This PDF template ID is a sample template
        // available to anyone.
        //
        // See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
        // on setting up your own template
        var pdfTemplateID = "CR0M7TNU4HDvUOVoqoyu";

        // Signer information
        var signerName = "Testy Signer";
        // Signer email comes from a CLI argument
        var signerEmail = otherArg;

        var payload = GetPacketVariables(signerName, signerEmail);

        var client = new GraphQLClient(apiKey);

        Console.WriteLine("Creating Etch e-sign packet...");
        var response = await client.CreateEtchPacket(payload);

        Console.WriteLine("Finished!");
    }
}