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

// Example: Create + update a Workflow submission via the Anvil API
//
// * Workflow API docs: https://www.useanvil.com/docs/api/workflows
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable in the .env file at the root of the
// typescript directory.
//
// By default, the script uses the sample workflow in your organization when you
// signed up for an Anvil account.
//
// ANVIL_API_KEY=<yourAPIKey> java -jar target/Examples-1.0-SNAPSHOT.jar create-update-workflow <my-org-slug>
//
// The script will:
//
// * Fetch Workflow details from human-readable slugs
// * Find the Workflow's first webform
// * Start the Workflow by submitting data to the webform
// * Update the new submission with extra data
//
// By default, the script uses the sample workflow added to your organization
// when you signed up for an Anvil account. If you do not have this workflow,
// change `weldSlug` variable below to a workflow you do have.
//
// Objects within Workflows have their own terminology. See the following for
// more info: https://www.useanvil.com/docs/api/getting-started#terminology

public class CreateUpdateWorkflowSubmission implements IRunnable {
    private GraphqlClient client;
    private final ObjectMapper _objectMapper = new ObjectMapper();

    public String buildWorkflowSubmissionDetailsUrl(String organizationSlug, String weldSlug, String weldDataEid) {
        String urlFormat = "%s/org/%s/w/%s/%s";
        return String.format(urlFormat, Constants.BASE_URL, organizationSlug, weldSlug, weldDataEid);
    }

    private String getCreationPayload() throws JsonProcessingException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        var creationPayload = String.format("Workflow start! %s", dateFormat.format(new Date()));

        // We are relying on the sample workflow's Field Aliases here. See this help
        // article for more info:
        // https://www.useanvil.com/help/workflows/add-field-aliases-to-a-workflow
        Map<String, String> payload = new HashMap<>(Map.of(
                "creationPayload", creationPayload
        ));

        return this._objectMapper.writeValueAsString(payload);
    }

    private String getUpdatePayload() throws JsonProcessingException {
        // After the workflow has been started, we will update the new submission with this data
        Map<String, Serializable> name = new HashMap<>(Map.of(
                "firstName", "Sally",
                "lastName", "Jones"
        ));
        Map<String, Object> payload = new HashMap<>(Map.of(
                "name", name,
                "email", "sally@example.com"
        ));
        return this._objectMapper.writeValueAsString(payload);
    }

    private JsonNode getWeld(String weldSlug, String organizationSlug) throws IOException, InterruptedException {
        // Ref docs:
        // https://www.useanvil.com/docs/api/graphql/reference/#operation-weld-Queries
        Map<String, Serializable> variables = new HashMap<>(Map.of(
                "organizationSlug", organizationSlug,
                "slug", weldSlug
        ));

        HttpResponse<String> response = this.client.doRequest(Paths.get("src/main/resources/queries/wf-weld.graphql"), variables);

        JsonNode resNode = this._objectMapper.readTree(response.body());

        // Response is in `{ "data": { "weld": { ... } } }` format
        return resNode.get("data").get("weld");
    }

    private JsonNode submitToWorkflowWebform(Map<String, Serializable> variables) throws IOException, InterruptedException {
        // This function will submit data to a Workflow's Webform. In our system these
        // objects are called Weld (Workflow), and Forge (webform).
        HttpResponse<String> response = this.client.doRequest(Paths.get("src/main/resources/mutations/forge-submit.graphql"), variables);

        JsonNode resNode = this._objectMapper.readTree(response.body());

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

        //
        // Find the workflow
        //

        try {
            this.client = new GraphqlClient(apiKey);
            // Workflows are 'Weld' objects in Anvil's system
            weld = this.getWeld(weldSlug, organizationSlug);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Found weld: " + weld.get("eid").asText());

        //
        // Start the workflow
        //

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

        // We have the newly created objects
        String detailsUrl = this.buildWorkflowSubmissionDetailsUrl(organizationSlug, weldSlug, weldDataEid);

        System.out.println("Workflow started");
        System.out.println("View on your dashboard: " + detailsUrl);
        System.out.println("Submission eid: " + submission.get("eid").asText());
        System.out.println("WeldData eid: " + weldDataEid);
        System.out.println(submission);

        //
        // Update the webform's submission data
        //

        // Updating the submission uses the same mutation, but you just specify a
        // submission and weldData information. You can add more data or change
        // payload data
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
