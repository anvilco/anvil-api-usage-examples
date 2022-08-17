// Example: Generate a PDF from HTML and CSS via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
// * See our invoice HTML template for a more complex HTML to PDF example:
//   https://github.com/anvilco/html-pdf-invoice-template
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run generate-html-to-pdf
//
// The filled PDF will be saved to `output/generate-html-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run generate-html-to-pdf \
//   && open output/generate-html-output.pdf

using Anvil.Client;
using Anvil.Payloads.Request;
using Anvil.Payloads.Request.Types;
using AnvilExamples;

class GenerateHtmlToPdf : RunnableBaseExample
{
    public GenerateHtmlToPdf(string apiKey) : base(apiKey)
    {
    }

    private GeneratePdf GetPayload()
    {
        return new GeneratePdf()
        {
            Title = "Example HTML to PDF",
            Type = "html",
            Data = new GeneratePdfHtml()
            {
                Html = @"
                    <h1 class='header-one'>What is Lorem Ipsum?</h1>
                    <p>
                    Lorem Ipsum is simply dummy text of the printing and typesetting
                    industry. Lorem Ipsum has been the industry's standard dummy text
                    ever since the <strong>1500s</strong>, when an unknown printer took
                    a galley of type and scrambled it to make a type specimen book.
                    </p>
                    <h3 class='header-two'>Where does it come from?</h3>
                    <p>
                    Contrary to popular belief, Lorem Ipsum is not simply random text.
                    It has roots in a piece of classical Latin literature from
                    <i>45 BC</i>, making it over <strong>2000</strong> years old.
                    </p>
                ",
                Css = @"
                    body { font-size: 14px; color: #171717; }
                    .header-one { text-decoration: underline; }
                    .header-two { font-style: underline; }
                ",
            },
        };
    }

    public override async Task Run(string apiKey)
    {
        var payload = GetPayload();
        var client = new RestClient(apiKey);
        var wasWritten = await client.GeneratePdf(payload, "./output/generate-html-output.pdf");

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