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
        HttpResponse<byte[]> thing;
        try {
            // Fill PDF returns the filled PDF file, so save the bytes directly to a file.
            thing = client.fillPdf(pdfTemplateEid, payload);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Files.write(Paths.get("output/pdf-fill.pdf"), thing.body());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
