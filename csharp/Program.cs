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
        }
    };

    public static async Task Main(string[] args)
    {
        // Get your API key from your Anvil organization settings.
        // See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
        var apiKey = Environment.GetEnvironmentVariable("ANVIL_API_KEY");
        var programToRun = args[0];

        var found = programsList.Find(obj => obj.Name.Equals(programToRun));
        if (found != null)
        {
            var runnable = (RunnableBaseExample) Activator.CreateInstance(found.Klass, apiKey);
            await runnable.Run(apiKey);
        }
        else
        {
            Console.WriteLine("Example not found");
        }
    }
}