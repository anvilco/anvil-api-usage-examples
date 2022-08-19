// Example: Create + update a Workflow submission via the Anvil API
//
// * Workflow API docs: https://www.useanvil.com/docs/api/workflows
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable in the .env file at the root of the
// typescript directory.
//
// By default the script uses the sample workflow in your organization when you
// signed up for an Anvil account.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run create-update-workflow <my-org-slug>
//
// The script will:
//
// * Fetch Workflow details from human-readable slugs
// * Find the Workflow's first webform
// * Start the Workflow by submitting data to the webform
// * Update the new submission with extra data
//
// By default, the script uses the sample workflow added to your organization
// when you signed up for an Anvil account. If you do not have this workflow,
// change `weldSlug` variable below to a workflow you do have.
//
// Objects within Workflows have their own terminology. See the following for
// more info: https://www.useanvil.com/docs/api/getting-started#terminology

using Anvil.Client;
using Anvil.Payloads.Request;
using Anvil.Payloads.Response;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace AnvilExamples.examples;

class CreateUpdateWorkflowSubmission : RunnableBaseExample
{
    private async Task<JObject> GetWeld(GraphQLClient client, string organizationSlug, string weldSlug)
    {
        // Ref docs:
        // https://www.useanvil.com/docs/api/graphql/reference/#operation-weld-Queries
        var weldQuery = @"
        query WeldQuery (
            $eid: String,
            $organizationSlug: String,
            $slug: String
            ) {
            weld (
                eid: $eid,
            organizationSlug: $organizationSlug,
            slug: $slug
                ) {
                eid
                    name
                forges {
                    eid
                        slug
                    name
                }
            }
        }
        ";

        var variables = new {slug = weldSlug, organizationSlug};
        return await client.SendQuery(weldQuery, variables);
    }

    private async Task<ForgeSubmitPayload> SubmitToWorkflowWebform(GraphQLClient client, ForgeSubmit payload)
    {
        var responseQuery = @"{
            eid
            createdAt
            updatedAt
            resolvedPayload
            weldData {
                eid
                displayTitle
                isTest
                createdAt
                updatedAt
            }
        }";

        // Build the payload for the webform submission
        return await client.ForgeSubmit(payload);
    }

    private object BuildWorkflowSubmissionDetailsUrl(string organizationSlug, string weldSlug, string weldDataEid)
    {
        return $"https://app.useanvil.com/org/{organizationSlug}/w/{weldSlug}/{weldDataEid}";
    }

    public override async Task Run(string apiKey, string otherArg)
    {
        var client = new GraphQLClient(apiKey);
        var organizationSlug = otherArg;
        var weldSlug = "sample-workflow";

        //
        // Find the workflow
        //

        // Workflows are 'Weld' objects in Anvil's system
        Console.WriteLine($">>> Fetching Weld {organizationSlug}/{weldSlug}");
        var response = await GetWeld(client, organizationSlug, weldSlug);
        var weld = response["weld"];

        Console.WriteLine($"Found weld: {weld["eid"]}");

        //
        // Start the workflow
        //

        // Now we get the workflow's first webform to start the workflow
        // Webforms are `Forge` objects in Anvil's system
        var startForge = weld["forges"][0];
        Console.WriteLine(">>> Starting workflow with webform");
        Console.WriteLine(startForge);

        // Start the workflow by submitting data to the webform of your choice. You
        // will receive a new Submission object and a new WeldData.
        var startPayload = new ForgeSubmit
        {
            IsTest = true,
            ForgeEid = (string?) startForge["eid"],
            Payload = new
            {
                shortText = $"Workflow start! {DateTime.Now}"
            }
        };
        var submission = await SubmitToWorkflowWebform(client, startPayload);
        var weldData = (JObject) submission.ForgeSubmit.WeldData;

        // We have the newly created objects
        var detailsURL =
            BuildWorkflowSubmissionDetailsUrl(organizationSlug, weldSlug, weldDataEid: (string) weldData["eid"]);

        Console.WriteLine("Workflow started");
        Console.WriteLine($"View on your daskboard {detailsURL}");
        Console.WriteLine($"Submission eid: {submission.ForgeSubmit.Eid}, WeldData eid: {weldData["eid"]}");
        Console.WriteLine(JsonConvert.SerializeObject(submission));

        //
        // Update the webform's submission data
        //

        // Updating the submission uses the same mutation, but you just specify a
        // submission and weldData information. You can add more data or change
        // payload data
        Console.WriteLine(">>> Updating the submission...");

        var updatePayload = new ForgeSubmit
        {
            IsTest = true,
            ForgeEid = (string?) startForge["eid"],
            WeldDataEid = (string) weldData["eid"],
            SubmissionEid = submission.ForgeSubmit.Eid,
            Payload = new
            {
                // We'll update new fields, but you can also overwrite existing data
                name = new {firstName = "Sally", lastName = "Jones"},
                email = "sally@example.com",
            }
        };
        var updatedSubmission = await SubmitToWorkflowWebform(client, updatePayload);

        // We have the new objects
        Console.WriteLine("Submission updated!");
        Console.WriteLine(JsonConvert.SerializeObject(updatedSubmission.ForgeSubmit));
    }
}