// Example: Make an arbitrary GraphQL request to the Anvil API
//
// Feel free to copy and paste queries and mutations from the GraphQL reference
// docs into the functions in this script.
//
// * GraphQL guide: https://www.useanvil.com/docs/api/graphql
// * GraphQL ref docs: https://www.useanvil.com/docs/api/graphql/reference
// * Anvil C#/.NET client: https://www.nuget.org/packages/Anvil/
//
// This example is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> dotnet run make-graphql-request

using Anvil.Client;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace AnvilExamples;

class MakeGraphqlRequest : RunnableBaseExample
{
    public static async Task<JObject> CallCurrentUserQuery(GraphQLClient client)
    {
        var query = @"query CurrentUser {
            currentUser {
                eid
                name
                organizations {
                    eid
                    slug
                    name
                    casts {
                        eid
                        name
                    }
                    welds {
                        eid
                        name
                    }
                }
            }
        }";
        return await client.SendQuery(query, null);
    }

    static async Task<JObject> CallWeldQuery(GraphQLClient client, string weldEid)
    {
        var query = @"
            query WeldQuery (
                $eid: String,
            ) {
                weld (
                    eid: $eid,
                ) {
                    eid
                    name
                    forges {
                        eid
                        slug
                        name
                    }
                }
            }";
        return await client.SendQuery(query, new {eid = weldEid});
    }

    /**
     * Main method that gets run from the `Program.cs` entrypoint.
     *
     * `apiKey` is provided through the `ANVIL_API_KEY` environment variable.
     */
    public override async Task Run(string apiKey)
    {
        Console.WriteLine("Make request");
        var client = new GraphQLClient(apiKey);
        var userResponse = await CallCurrentUserQuery(client);
        var userJson = JsonConvert.SerializeObject(userResponse);
        Console.WriteLine("Current user\n" + userJson);

        var firstWeld = userResponse["currentUser"]["organizations"][0]["welds"][0];
        var weldResponse = await CallWeldQuery(client, (string) firstWeld["eid"]);
        Console.WriteLine("First weld details:\n" + weldResponse);
    }

    public MakeGraphqlRequest(string apiKey) : base(apiKey)
    {
    }
}