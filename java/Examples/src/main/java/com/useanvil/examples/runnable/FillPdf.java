package com.useanvil.examples.runnable;

import com.useanvil.examples.client.RestClient;

import java.io.IOException;
import java.net.http.HttpResponse;
import java.nio.file.*;

public class FillPdf implements IRunnable {
    @Override
    public void run(String apiKey) {
        String pdfTemplateEid = "B5Loz3C7GVortDmn4p2P";
        String payload;

        try {
            payload = new String(Files.readAllBytes(Paths.get("src/main/resources/payloads/pdf-fill.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RestClient client = new RestClient(apiKey);
        HttpResponse<byte[]> response;
        try {
            // response data will be the filled PDF binary data. It is important that the
            // data is saved with no encoding! Otherwise, the PDF file will be corrupt.
            response = client.fillPdf(pdfTemplateEid, payload);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(Paths.get("output/pdf-fill.pdf"), response.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run(String apiKey, String otherArg) throws Exception {

    }
}
