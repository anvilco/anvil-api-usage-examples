﻿// Example: Fill a PDF via the Anvil API
//
// * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
//
// This example is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run fill-pdf
//
// The filled PDF will be saved to `output/fill-output.pdf`. You can open the
// filled PDF immediately after saving the file on OSX machines with the
// `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run fill-pdf && open output/fill-output.pdf

using Anvil.Client;

namespace AnvilExamples.examples;

class FillPDF : RunnableBaseExample
{
    private Anvil.Payloads.Request.FillPdf GetFillData()
    {
        // The `FillPdf` type contains the available options you can use
        // for the pdf fill request.
        return new Anvil.Payloads.Request.FillPdf
        {
            Title = "My PDF Title",
            FontSize = 10,
            TextColor = "#333333",
            // `Data` contains the fill payload that fills in the template's
            // PDF fields.
            // The keys used in this Dictionary should match the field's ids
            // as shown in the template editor or the API section of template.
            Data = new Dictionary<string, dynamic>()
            {
                {"shortText", "HELLOOW"},
                {"date", "2022-07-08"},
                {
                    "name", new Dictionary<string, object>()
                    {
                        {"firstName", "Robin"},
                        {"mi", "W"},
                        {"lastName", "Smith"}
                    }
                },
                {"email", "testy@example.com"},
                {
                    "phone", new Dictionary<string, object>()
                    {
                        {"num", "5554443333"},
                        {"region", "US"},
                        {"baseRegion", "U"},
                    }
                },
                {
                    "usAddress", new Dictionary<string, object>()
                    {
                        {"street1", "123 Main St #234"},
                        {"city", "San Francisco"},
                        {"state", "CA"},
                        {"zip", "94106"},
                        {"country", "U"},
                    }
                },
                {"ssn", "456454567"},
                {"ein", "897654321"},
                {"checkbox", true},
                {"radioGroup", "cast68d7e540afba11ecaf289fa5a354293a"},
                {"decimalNumber", 12345.67},
                {"dollar", 123.45},
                {"integer", 12345},
                {"percent", 50.3},
                {"longText", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor."},
                {"textPerLine", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor."},
                {"textPerLetter", "taH9QGigei6G5BtTUA4"},
                {"image", "https://placekitten.com/800/495"}
            }
        };
    }

    public override async Task Run(string apiKey)
    {
        // The PDF template ID to fill. This PDF template ID is a sample template
        // available to anyone.
        //
        // See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
        // on setting up your own template
        var pdfTemplateEid = "f9eQzbUgCCRVDrd4gt8b";

        var payload = GetFillData();
        var client = new RestClient(apiKey);

        // `dotnet run` should be run on the same directory that contains the `output` directory.
        // If not, there will be an `System.IO.DirectoryNotFoundException` exception.
        var wasWritten = await client.FillPdf(pdfTemplateEid, payload, "./output/fill-output.pdf");

        // Version number support
        // A version number can also be passed in. This will retrieve a specific
        // version of the PDF to be filled if you don't want the current version
        // to be used.
        //
        // You can also use the constant `ClientConstants.VERSION_LATEST` to fill a PDF with
        // your latest, unpublished changes. Use this if you'd like to fill out a
        // draft version of your template/PDF.
        // var wasWritten = await client.FillPdf(pdfTemplateEid, payload, ClientConstants.VERSION_LATEST);

        // const outputFilepath = path.join(__dirname, '..', 'output', 'fill-output.pdf')
        Console.WriteLine(wasWritten ? "Fill PDF finished" : "Fill PDF did not finish successfully");
    }
}