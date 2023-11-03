require 'dotenv/load'
require "graphql/client"
require "graphql/client/http"

# Anvil GraphQL reference
# https://www.useanvil.com/docs/api/graphql/reference/#group-Operations-Queries

# More on GraphQL client usage at
# https://github.com/github/graphql-client

module AnvilAPI
  URL = 'https://app.useanvil.com/graphql'

  HTTP = GraphQL::Client::HTTP.new(URL) do
    def headers(context)
      apiKey = Base64.encode64("#{ENV['ANVIL_API_KEY']}:")
      {
        "Authorization": "Basic #{apiKey}".strip()
      }
    end
  end

  # Fetch latest schema on init, this will make a network request
  Schema = GraphQL::Client.load_schema(HTTP)

  # However, it's smart to dump this to a JSON file and load from disk
  #
  # Run it from a script or rake task
  #   GraphQL::Client.dump_schema(AnvilAPI::HTTP, "path/to/schema.json")
  #
  # Schema = GraphQL::Client.load_schema("path/to/schema.json")

  Client = GraphQL::Client.new(schema: Schema, execute: HTTP)
end

Queries = AnvilAPI::Client.parse <<-'GRAPHQL'
  query CurrentUserQuery {
    currentUser {
      eid
      name
      organizations {
        eid
        slug
        name
        casts {
          eid
        }
      }
    }
  }

  query CastQuery ($eid: String!) {
    cast (eid: $eid) {
      eid
      name
      isTemplate
    }
  }
GRAPHQL

# Run a query without any variables
result = AnvilAPI::Client.query(Queries::CurrentUserQuery)
result.data.to_h # Everything to a hash

puts("Current user:")
puts(result.data.current_user.eid)
puts(result.data.current_user.name)

# Run a query with variables
cast = result.data.current_user.organizations[0].casts[0]
result = AnvilAPI::Client.query(Queries::CastQuery, variables: { eid: cast.eid })

puts("\nFirst PDF Template (cast) object:")
puts(result.data.cast.eid)
puts(result.data.cast.name)
