package com.useanvil.examples;

import com.useanvil.examples.runnable.FillPdf;
import com.useanvil.examples.runnable.IRunnable;
import com.useanvil.examples.runnable.MakeGraphqlRequest;

import java.util.AbstractMap;
import java.util.Map;

public class Main {

    private static final Map<String, Class<? extends IRunnable>> runnableMap = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("make-graphql-request", MakeGraphqlRequest.class),
            new AbstractMap.SimpleEntry<>("fill-pdf", FillPdf.class)
    );


    public static void main(String[] args) throws Exception {
        String apiKey = System.getenv("ANVIL_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new Exception("API key must be provided");
        }

        String toRun = null;
        if (args.length > 0) {
            toRun = args[0];
        }

        IRunnable runnable = Main.runnableMap.get(toRun).getDeclaredConstructor().newInstance();

        runnable.run(apiKey);
    }
}
