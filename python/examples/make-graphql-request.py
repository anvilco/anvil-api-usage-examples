# Example: Make an arbitrary GraphQL request to the Anvil API
#
# Feel free to copy and paste queries and mutations from the GraphQL reference
# docs into the functions in this script.
#
# * GraphQL guide: https://www.useanvil.com/docs/api/graphql
# * GraphQL ref docs: https://www.useanvil.com/docs/api/graphql/reference
# * Anvil Node.js client: https://github.com/anvilco/node-anvil
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable.
#
# ANVIL_API_KEY=<yourAPIKey> node examples/make-graphql-request.js

import json
import os
from python_anvil.api import Anvil

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
API_KEY = os.environ.get("ANVIL_API_KEY")


def call_current_user_query(anvil: Anvil) -> dict:
    """
    Gets the user data attached to the current API key.
    :param anvil:
    :type anvil: Anvil
    :return:
    """
    # See the reference docs for examples of all queries and mutations:
    # https://www.useanvil.com/docs/api/graphql/reference/
    user_query = """
        query CurrentUser {
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
        }
    """
    res = anvil.query(query=user_query, variables=None)
    return res["data"]["currentUser"]


def call_weld_query(anvil: Anvil, weld_eid: str):
    """
    Call the weld query.
    The weld() query is an example of a query that takes variables.
    :param anvil:
    :type anvil: Anvil
    :param weld_eid:
    :type weld_eid: str
    :return:
    """
    weld_query = """
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
        }
    """

    variables = {"eid": weld_eid}
    res = anvil.query(query=weld_query, variables=variables)
    return res["data"]["weld"]


def call_queries():
    anvil = Anvil(api_key=API_KEY)
    current_user = call_current_user_query(anvil)

    first_weld = current_user["organizations"][0]["welds"][0]
    weld_data = call_weld_query(anvil, weld_eid=first_weld["eid"])

    print("currentUser: ", json.dumps(current_user))
    print("First weld datails: ", json.dumps(weld_data))


if __name__ == "__main__":
    call_queries()
