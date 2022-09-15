package com.useanvil.examples;

import com.useanvil.examples.runnable.*;

import java.util.AbstractMap;
import java.util.Map;

public class Main {

    private static final Map<String, Class<? extends IRunnable>> runnableMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("make-graphql-request", MakeGraphqlRequest.class),
            new AbstractMap.SimpleEntry<>("fill-pdf", FillPdf.class),
            new AbstractMap.SimpleEntry<>("generate-html-to-pdf", GenerateHtmlToPdf.class),
            new AbstractMap.SimpleEntry<>("generate-markdown-to-pdf", GenerateMarkdownToPdf.class),
            new AbstractMap.SimpleEntry<>("create-update-workflow", CreateUpdateWorkflowSubmission.class)
    );


    public static void main(String[] args) throws Exception {
        // Get your API key from your Anvil organization settings.
        // See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
        String apiKey = System.getenv("ANVIL_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("API key must be provided");
        }

        String toRun = null;
        String otherArg = null;
        if (args.length > 0) {
            toRun = args[0];
            if (args.length > 1 && args[1] != null) {
                otherArg = args[1];
            }
        }

        IRunnable runnable = Main.runnableMap
                .get(toRun)
                .getDeclaredConstructor()
                .newInstance();

        if (otherArg != null && !otherArg.isBlank()) {
            runnable.run(apiKey, otherArg);
        } else {
            runnable.run(apiKey);
        }
    }
}
