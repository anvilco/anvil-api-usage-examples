// Example: Generate a PDF from Markdown via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run generate-markdown-to-pdf
//
// The filled PDF will be saved to `output/generate-markdown-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run generate-markdown-to-pdf \
//   && open output/generate-markdown-output.pdf

using Anvil.Client;
using Anvil.Payloads.Request;
using Anvil.Payloads.Request.Types;

namespace AnvilExamples.examples;

class GenerateMarkdownToPdf : RunnableBaseExample
{
    private GeneratePdf GetPayload()
    {
        return new GeneratePdf()
        {
            Title = "Example Invoice",
            Data = new List<IGeneratePdfListable>
            {
                new GeneratePdfItem()
                {
                    Label = "Name",
                    Content = "Sally Jones",
                },
                new GeneratePdfItem()
                {
                    Content = @"
Lorem **ipsum** dolor sit _amet_, consectetur adipiscing elit, sed [do eiusmod](https://www.useanvil.com/docs) tempor incididunt ut labore et dolore magna aliqua. Ut placerat orci nulla pellentesque dignissim enim sit amet venenatis.

Mi eget mauris pharetra et ultrices neque ornare aenean.

* Sagittis eu volutpat odio facilisis.

* Erat nam at lectus urna.",
                },
                new GeneratePdfTable()
                {
                    Table = new GeneratePdfTableContent()
                    {
                        FirstRowHeaders = true,
                        Rows = new List<List<string>>()
                        {
                            new()
                            {
                                "Description",
                                "Quantity",
                                "Price",
                            },
                            new()
                            {
                                "4x Large Widgets", "4", "$40.00"
                            },
                            new()
                            {
                                "10x Medium Sized Widgets in dark blue", "10", "$100.00"
                            },
                            new()
                            {
                                "10x Small Widgets in white", "6", "$60.00",
                            }
                        }
                    },
                },
            }
        };
    }

    public override async Task Run(string apiKey)
    {
        var payload = GetPayload();
        var client = new RestClient(apiKey);
        var wasWritten = await client.GeneratePdf(payload, "./output/generate-markdown-output.pdf");
        if (wasWritten)
        {
            Console.WriteLine("PDF generated successfully");
        }
        else
        {
            Console.WriteLine("There was an error generating the PDF");
        }
    }
}