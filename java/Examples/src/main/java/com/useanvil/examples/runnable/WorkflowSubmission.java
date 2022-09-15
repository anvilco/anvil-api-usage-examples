package com.useanvil.examples.runnable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.useanvil.examples.Constants;
import com.useanvil.examples.client.GraphqlClient;

import java.io.IOException;
import java.io.Serializable;
import java.net.http.HttpResponse;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WorkflowSubmission implements IRunnable {
    private GraphqlClient client;
    private final ObjectMapper _om = new ObjectMapper();

    public String buildWorkflowSubmissionDetailsUrl(String organizationSlug, String weldSlug, String weldDataEid) {
        String urlFormat = "%s/org/%s/w/%s/%s";
        return String.format(urlFormat, Constants.BASE_URL, organizationSlug, weldSlug, weldDataEid);
    }

    private String getCreationPayload() throws JsonProcessingException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        var creationPayload = String.format("Workflow start! %s", dateFormat.format(new Date()));

        Map<String, String> payload = new HashMap<>(Map.of(
                "creationPayload", creationPayload
        ));

        return this._om.writeValueAsString(payload);
    }

    private String getUpdatePayload() throws JsonProcessingException {
        Map<String, Serializable> name = new HashMap<>(Map.of(
                "firstName", "Sally",
                "lastName", "Jones"
        ));
        Map<String, Object> payload = new HashMap<>(Map.of(
                "name", name,
                "email", "sally@example.com"
        ));
        return this._om.writeValueAsString(payload);
    }

    private JsonNode getWeld(String weldSlug, String organizationSlug) throws IOException, InterruptedException {
        Map<String, Serializable> variables = new HashMap<>(Map.of(
                "organizationSlug", organizationSlug,
                "slug", weldSlug
        ));

        HttpResponse<String> response = this.client.doRequest(Paths.get("src/main/resources/queries/wf-weld.graphql"), variables);

        JsonNode resNode = this._om.readTree(response.body());

        // Response is in `{ "data": { "weld": { ... } } }` format
        return resNode.get("data").get("weld");
    }

    private JsonNode submitToWorkflowWebform(Map<String, Serializable> variables) throws IOException, InterruptedException {
        HttpResponse<String> response = this.client.doRequest(Paths.get("src/main/resources/mutations/forge-submit.graphql"), variables);

        JsonNode resNode = this._om.readTree(response.body());

        // Response is in `{ "data": { "forgeSubmit": { ... } } }` format
        return resNode.get("data").get("forgeSubmit");
    }

    @Override
    public void run(String apiKey) throws Exception {
        throw new Exception("This runnable should use `run(apiKey, orgSlug)`");
    }


    @Override
    public void run(String apiKey, String organizationSlug) throws IOException, InterruptedException {
        JsonNode weld;
        String weldSlug = "sample-workflow";

        try {
            this.client = new GraphqlClient(apiKey);
            weld = this.getWeld(weldSlug, organizationSlug);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Found weld: " + weld.get("eid").asText());

        // Now we get the workflow's first webform to start the workflow
        // Webforms are `Forge` objects in Anvil's system
        JsonNode startForge = weld.get("forges").get(0);
        String startForgeEid = startForge.get("eid").asText();
        System.out.println(">>> Starting workflow with webform \n" + startForge);

        JsonNode submission = this.submitToWorkflowWebform(new HashMap<>(Map.of(
                "isTest", true,
                "forgeEid", startForgeEid,
                "payload", this.getCreationPayload()
        )));

        System.out.println(submission);

        String weldDataEid = submission.get("weldData").get("eid").asText();
        String detailsUrl = this.buildWorkflowSubmissionDetailsUrl(organizationSlug, weldSlug, weldDataEid);

        System.out.println("Workflow started");
        System.out.println("View on your dashboard: " + detailsUrl);
        System.out.println("Submission eid: " + submission.get("eid").asText());
        System.out.println("WeldData eid: " + weldDataEid);
        System.out.println(submission);

        // Update the webform's submission data
        System.out.println(">>> Updating the submission...");


        JsonNode updatedSubmission = this.submitToWorkflowWebform(new HashMap<>(Map.of(
                "forgeEid", startForgeEid,
                "submissionEid", submission.get("eid").asText(),
                // We'll update new fields, but you can also overwrite existing data
                "payload", this.getUpdatePayload()
        )));

        System.out.println("Submission updated!");
        System.out.println(updatedSubmission);
    }
}
