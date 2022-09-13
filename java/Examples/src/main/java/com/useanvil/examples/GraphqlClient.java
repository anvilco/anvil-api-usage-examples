package com.useanvil.examples;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphqlClient {

    private HttpClient client;
    private String _apiKey;
    private ObjectMapper _objectMapper;

    public GraphqlClient(String apiKey) throws IOException, InterruptedException {
        this._apiKey = apiKey;
        this._objectMapper = new ObjectMapper();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();

//        HttpRequest request = this.createRequest();

        // Async version
        //        this.client
        //                .sendAsync(request, HttpResponse.BodyHandlers.ofString())
        //                .thenApply(HttpResponse::body)
        //                .thenAccept(System.out::println);

        // Maybe don't need to fully deserialize now, since we probably need to define classes for
        // deserializing.
//        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString());

//        System.out.println(response.statusCode());
//        System.out.println(response.body());
    }

    public String getApiKey() throws RuntimeException {
        if (this._apiKey.isBlank() || this._apiKey == null) {
            throw new RuntimeException("API key cannot be blank or null");
        }

        return new String(Base64.getEncoder().encode((this._apiKey + ":").getBytes()));
    }

    public HttpRequest.Builder createRequestBuilder() throws RuntimeException {
        return HttpRequest.newBuilder()
                .uri(URI.create(Constants.GRAPHQL_ENDPOINT))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + this.getApiKey())
                .header("Content-Type", "application/json");
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
