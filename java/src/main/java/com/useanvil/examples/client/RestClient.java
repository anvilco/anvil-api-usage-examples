package com.useanvil.examples.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.Constants;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

public class RestClient extends BaseClient {

    public RestClient(String apiKey) {
        this._apiKey = apiKey;
        this._objectMapper = new ObjectMapper();

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build();
    }

    public HttpResponse<byte[]> fillPdf(String templateId, String payload) throws IOException, InterruptedException {
        String fillPdfPart = String.format(Constants.FillPdf, templateId);

        HttpRequest request = this.createRequestBuilder(Constants.REST_ENDPOINT + fillPdfPart)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    public HttpResponse<byte[]> generatePdf(String payload) throws IOException, InterruptedException {
        HttpRequest request = this.createRequestBuilder(Constants.REST_ENDPOINT + Constants.GeneratePdf)
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        return this.client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }
}
