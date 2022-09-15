package com.useanvil.examples.runnable;

import com.useanvil.examples.client.RestClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GenerateMarkdownToPdf implements IRunnable {
    @Override
    public void run(String apiKey) {
        String payload;

        try {
            payload = new String(Files.readAllBytes(Paths.get("src/main/resources/payloads/generate-markdown-to-pdf.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RestClient client = new RestClient(apiKey);
        HttpResponse<byte[]> response;
        try {
            // Generate PDF returns the filled PDF file, so save the bytes directly to a file.
            response = client.generatePdf(payload);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(Paths.get("output/generate-markdown-output.pdf"), response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
