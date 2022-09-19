package com.useanvil.examples.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Base64;

public class BaseClient {
    protected HttpClient client;
    protected String _apiKey;
    protected ObjectMapper _objectMapper;

    public String getApiKey() throws RuntimeException {
        if (this._apiKey.isBlank() || this._apiKey == null) {
            throw new RuntimeException("API key cannot be blank or null");
        }

        return new String(Base64.getEncoder().encode((this._apiKey + ":").getBytes()));
    }

    protected HttpRequest.Builder createRequestBuilder(String endpoint) throws RuntimeException {
        return HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .timeout(Duration.ofSeconds(30))
                .header("Authorization", "Basic " + this.getApiKey())
                .header("Content-Type", "application/json");
    }
}
