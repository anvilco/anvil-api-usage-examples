package com.useanvil.examples.runnable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.client.GraphqlClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

// Example: Make an arbitrary GraphQL request to the Anvil API
//
// Feel free to copy and paste queries and mutations from the GraphQL reference
// docs into the functions in this script.
//
// * GraphQL guide: https://www.useanvil.com/docs/api/graphql
// * GraphQL ref docs: https://www.useanvil.com/docs/api/graphql/reference
//
// This is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT-jar-with-dependencies.jar make-graphql-request

public class MakeGraphqlRequest implements IRunnable {
    @Override
    public void run(String apiKey) {
        ObjectMapper om = new ObjectMapper();

        try {
            GraphqlClient client = new GraphqlClient(apiKey);
            HttpResponse<String> response = client.doRequest(Paths.get("src/main/resources/queries/current-user.graphql"));
            String res = response.body();
            JsonNode resNode = om.readTree(res);

            // Response is in `{ "data": { "currentUser": { ... } } }` format
            JsonNode currentUser = resNode.get("data").get("currentUser");

            // Get the first org
            JsonNode organization = currentUser.get("organizations").get(0);

            // Get the first weld from the org
            JsonNode weld = organization.get("welds").get(0);

            System.out.println("currentUser: " + currentUser);
            System.out.println("First weld details: " + weld);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String apiKey, String otherArg) throws Exception {

    }
}
