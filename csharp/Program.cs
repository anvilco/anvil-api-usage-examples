using AnvilExamples.examples;

namespace AnvilExamples;

public class ProgramItem
{
    public string Name { get; set; }
    public Type Klass { get; set; }
}

public class Program
{
    private static List<ProgramItem> programsList = new()
    {
        new ProgramItem()
        {
            Name = "fill-pdf",
            Klass = typeof(FillPDF)
        },
        new ProgramItem()
        {
            Name = "make-graphql-request",
            Klass = typeof(MakeGraphqlRequest),
        },
        new ProgramItem()
        {
            Name = "generate-html-to-pdf",
            Klass = typeof(GenerateHtmlToPdf),
        },
        new ProgramItem()
        {
            Name = "generate-markdown-to-pdf",
            Klass = typeof(GenerateMarkdownToPdf),
        },
        new ProgramItem()
        {
            Name = "create-etch-packet",
            Klass = typeof(CreateEtchESignPacket),
        },
        new ProgramItem()
        {
            Name = "create-update-workflow",
            Klass = typeof(CreateUpdateWorkflowSubmission),
        }
    };

    public static async Task Main(string[] args)
    {
        // Get your API key from your Anvil organization settings.
        // See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
        var apiKey = Environment.GetEnvironmentVariable("ANVIL_API_KEY");

        if (apiKey == null)
        {
            throw new Exception("API key must be provided");
        }

        var programToRun = args[0];
        string? otherArgs = null;

        if (args.Length > 1)
        {
            otherArgs = args[1];
        }

        var found = programsList.Find(obj => obj.Name.Equals(programToRun));
        if (found != null)
        {
            var runnable = (RunnableBaseExample) Activator.CreateInstance(found.Klass)!;

            if (runnable == null)
            {
                throw new Exception("Unable to run example");
            }

            if (otherArgs == null)
            {
                await runnable.Run(apiKey);
            }
            else
            {
                await runnable.Run(apiKey, otherArgs);
            }
        }
        else
        {
            Console.WriteLine("Example not found");
        }
    }
}