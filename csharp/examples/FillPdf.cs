using System.Runtime.CompilerServices;
using Anvil.Client;
using AnvilExamples;
using Newtonsoft.Json.Linq;

class FillPDF : RunnableBaseExample
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

    public FillPDF(string apiKey) : base(apiKey)
    {
        Console.WriteLine(this._apiKey);
    }

    override public async Task Run(string apiKey)
    {
        Console.WriteLine("Fill pdf");
    }
}