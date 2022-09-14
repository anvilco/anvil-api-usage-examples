package com.useanvil.examples.runnable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.client.GraphqlClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Paths;

public class MakeGraphqlRequest implements IRunnable {
    @Override
    public void run(String apiKey) {
        ObjectMapper om = new ObjectMapper();

        try {
            GraphqlClient client = new GraphqlClient(apiKey);
            HttpResponse<String> response = client.doRequest(Paths.get("src/main/resources/queries/current-user.graphql"), null);
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
}
