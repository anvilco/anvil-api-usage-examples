using System.Runtime.CompilerServices;
using Anvil.Client;
using Newtonsoft.Json.Linq;

class Program
{
    static async Task<JObject> CallCurrentUserQuery(GraphQLClient client)
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

    static async Task Main(string[] args)
    {
        var apiKey = Environment.GetEnvironmentVariable("ANVIL_API_KEY");
        var client = new GraphQLClient(apiKey);
        var userResponse = await CallCurrentUserQuery(client);
        var firstWeld = userResponse["currentUser"]["organizations"][0]["welds"][0];
        var weldResponse = await CallWeldQuery(client, (string) firstWeld["eid"]);

        Console.WriteLine(weldResponse);
    }
}