package com.useanvil.examples.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizosoft.methanol.*;
import com.useanvil.examples.Constants;
import com.useanvil.examples.entity.CreateEtchPacket;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Format;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphqlClient extends BaseClient {

    public GraphqlClient(String apiKey) throws IOException, InterruptedException {
        this._apiKey = apiKey;
        this._objectMapper = new ObjectMapper();

        client = Methanol.newBuilder()
                .baseUri(Constants.BASE_URL)
                .defaultHeader("Accept", "application/json")
                .requestTimeout(Duration.ofSeconds(20))
                .headersTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .autoAcceptEncoding(true)
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

    public HttpResponse<String> doRequest(Path queryFile, CreateEtchPacket variables, Path[] files) throws IOException, InterruptedException {
        // GraphQL requests are in the format `{ "query": "", "variables": { ... } }`
        Map<String, Serializable> graphqlMap = new HashMap<>();
        graphqlMap.put("query", new String(Files.readAllBytes(queryFile)));
        graphqlMap.put("variables", variables);
        String json = this._objectMapper.writeValueAsString(graphqlMap);

        // Most of this is to handle multipart uploads.
        // For normal files, a base64-encoded file in the payload should work
        // fine, if the payload size is too large, you will have to use
        // multipart as described in the spec here:
        // https://github.com/jaydenseric/graphql-multipart-request-spec
        //
        // This will be handled automatically in a future Anvil Java library.
        LinkedHashMap<String, String[]> fileMap = new LinkedHashMap<>();
        for (int fileIdx = 0; fileIdx < files.length; fileIdx++) {
            String mapStr = String.format("variables.files.%s.file", fileIdx);
            // The value part of the map needs to be an array.
            fileMap.put(String.valueOf(fileIdx), new String[]{mapStr});
        }

        // We're using Methanol's Multipart request builder here since it's
        // easier to deal with for our purposes here.
        // It's compatible with the built-in Java 11 HTTP client, so we can
        // just pass that into `client.send()`.
        MultipartBodyPublisher.Builder multipartBody = MultipartBodyPublisher.newBuilder()
                .formPart("operations", HttpRequest.BodyPublishers.ofString(json))
                .formPart("map", HttpRequest.BodyPublishers.ofString(this._objectMapper.writeValueAsString(fileMap)));

        // Go through the files and add them to our multipart builder
        for (String fileKey : fileMap.keySet()) {
            var file = files[Integer.parseInt(fileKey)];
            multipartBody.filePart(fileKey, files[Integer.parseInt(fileKey)]);
        }

        HttpRequest request = this.createRequestBuilder()
                .POST(multipartBody.build())
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    public HttpResponse<String> doRequest(String query, String variables) throws IOException, InterruptedException {
        // GraphQL requests are in the format `{ "query": "", "variables": "" }`
        Map<String, String> graphqlMap = new HashMap<>();
        graphqlMap.put("query", query);
        graphqlMap.put("variables", variables);
        String json = this._objectMapper.writeValueAsString(graphqlMap);

        HttpRequest request = this.createRequestBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
