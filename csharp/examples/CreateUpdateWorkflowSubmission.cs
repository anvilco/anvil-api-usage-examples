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

namespace AnvilExamples;

class CreateUpdateWorkflowSubmission : RunnableBaseExample
{
    public CreateUpdateWorkflowSubmission(string apiKey) : base(apiKey)
    {
    }

    public override async Task Run(string apiKey, string otherArg)
    {
        var client = new GraphQLClient(apiKey);

        var thing = await client.SendQuery();
    }
}