package com.useanvil.examples.runnable;

import com.useanvil.examples.client.RestClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;

// Example: Generate a PDF from Markdown via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar generate-markdown-to-pdf
//
// The filled PDF will be saved to `output/generate-markdown-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar generate-markdown-to-pdf \
//   && open output/generate-markdown-output.pdf

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
            // response data will be the filled PDF binary data. It is important that the
            // data is saved with no encoding! Otherwise, the PDF file will be corrupt.
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

    @Override
    public void run(String apiKey, String otherArg) throws Exception {

    }
}
