package com.useanvil.examples.client;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.Constants;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

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

    public HttpResponse<byte[]> fillPdf(String templateId, String payload, int versionNumber) throws IOException, InterruptedException, URISyntaxException {
        String fillPdfPart = String.format(Constants.FillPdf, templateId);

        URI uri = new URIBuilder(new URI(Constants.REST_ENDPOINT + fillPdfPart))
                .addParameter("versionNumber", String.valueOf(versionNumber))
                .build();

        HttpRequest request = this.createRequestBuilder(String.valueOf(uri))
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
