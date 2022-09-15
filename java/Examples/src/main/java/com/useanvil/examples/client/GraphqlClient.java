package com.useanvil.examples.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.Constants;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphqlClient extends BaseClient {

    public GraphqlClient(String apiKey) throws IOException, InterruptedException {
        this._apiKey = apiKey;
        this._objectMapper = new ObjectMapper();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public HttpRequest.Builder createRequestBuilder() throws RuntimeException {
        return this.createRequestBuilder(Constants.GRAPHQL_ENDPOINT);
    }

    public HttpResponse<String> doRequest(Path queryFile) throws IOException, InterruptedException {
        return this.doRequest(new String(Files.readAllBytes(queryFile)), null);
    }

    public HttpResponse<String> doRequest(Path queryFile, Map<String, Serializable> variables) throws IOException, InterruptedException {
        String json = this._objectMapper.writeValueAsString(variables);
        return this.doRequest(new String(Files.readAllBytes(queryFile)), json);
    }

    public HttpResponse<String> doRequest(Path queryFile, String variables) throws IOException, InterruptedException {
        return this.doRequest(new String(Files.readAllBytes(queryFile)), variables);
    }


    public HttpResponse<String> doRequest(String query, String variables) throws IOException, InterruptedException {
//        String query = new String(Files.readAllBytes(Paths.get("src/main/resources/queries/current-user.graphql")));

        // GraphQL requests are in the format `{ "query": "", "variables": "" }`
        Map<String, String> graphqlMap = new LinkedHashMap<>();
        graphqlMap.put("query", query);
        graphqlMap.put("variables", variables);
        String json = this._objectMapper.writeValueAsString(graphqlMap);

        HttpRequest request = this.createRequestBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }


    public String fillPdf(String s) {
        return "";
    }
}
