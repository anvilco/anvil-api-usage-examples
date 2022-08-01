#  Example: Create + update a Workflow submission via the Anvil API
#
# * Workflow API docs: https://www.useanvil.com/docs/api/workflows
# * Anvil Python client: https://github.com/anvilco/python-anvil/
# * Anvil Python docs: https://python-anvil.readthedocs.io/
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable.
#
# By default, the script uses the sample workflow in your organization when you
# signed up for an Anvil account.
#
# ANVIL_API_KEY=<yourAPIKey> python examples/create-update-workflow-submission.py <my-org-slug>
#
# The script will:
#
# * Fetch Workflow details from human-readable slugs
# * Find the Workflow's first webform
# * Start the Workflow by submitting data to the webform
# * Update the new submission with extra data
#
# By default, the script uses the sample workflow added to your organization
# when you signed up for an Anvil account. If you do not have this workflow,
# change `weldSlug` variable below to a workflow you do have.
#
# Objects within Workflows have their own terminology. See the following for
# more info: https://www.useanvil.com/docs/api/getting-started#terminology

import os
import sys
import json
from datetime import datetime
from python_anvil.api import Anvil

from forge_submit import (
    FORGE_SUBMIT,
    DEFAULT_RESPONSE_QUERY as FORGE_SUBMIT_RESPONSE_QUERY
)

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
API_KEY = os.environ.get("ANVIL_API_KEY")


def get_weld(anvil: Anvil, weld_slug: str, org_slug: str) -> dict:
    # We need some information from the workflow, so we will fetch it from your
    # account before we can submit data to it.

    # Ref docs:
    # https://www.useanvil.com/docs/api/graphql/reference/#operation-weld-Queries
    weld_query = """
        query WeldQuery (
          $eid: String,
          $organizationSlug: String,
          $slug: String
        ) {
          weld (
            eid: $eid,
            organizationSlug: $organizationSlug,
            slug: $slug
          ) {
            eid
            name
            forges {
              eid
              slug
              name
            }
          }
        }
    """
    variables = {"slug": weld_slug, "organizationSlug": org_slug}
    res = anvil.query(query=weld_query, variables=variables)

    if res.get("errors"):
        raise Exception(f"GraphQL Error: {res['errors']}")

    weld = res["data"]["weld"]
    if len(weld["forges"]) < 1:
        raise Exception(f"Weld {org_slug}/{weld_slug} has no webforms!")

    return weld


def submit_to_workflow_webform(anvil: Anvil, variables: dict):
    # This function will submit data to a Workflow's Webform. In our system these
    # objects are called Weld (Workflow), and Forge (webform).
    # Ref docs:
    # https://www.useanvil.com/docs/api/graphql/reference/#operation-forgesubmit-Mutations
    res = anvil.query(
        FORGE_SUBMIT.format(query=FORGE_SUBMIT_RESPONSE_QUERY),
        variables=variables,
    )

    return res["data"]["forgeSubmit"]


def create_and_update_workflow_submission(org_slug: str, weld_slug: str):
    anvil = Anvil(api_key=API_KEY)

    # We will start the workflow by submitting this data
    creation_payload = {
        # We are relying on the sample workflow's Field Aliases here. See this help
        # article for more info:
        # https://www.useanvil.com/help/workflows/add-field-aliases-to-a-workflow
        "shortText": f"Workflow start! {datetime.now()}"
    }

    # After the workflow has been started, we will update the new submission with this data
    update_payload = {
        "name": {"firstName": "Sally", "lastName": "Jones"},
        "email": "sally@example.com",
    }

    # Find the workflow.
    # Workflows are 'Weld' objects in Anvil's system
    print(">>> Fetching Weld ${organizationSlug}/${weldSlug}")

    weld = get_weld(anvil=anvil, weld_slug=weld_slug, org_slug=org_slug)

    # Start the workflow
    # Now we get the workflow's first webform to start the workflow
    # Webforms are `Forge` objects in Anvil's system
    start_forge = weld["forges"][0]

    print(">>> Starting workflow with webform", json.dumps(start_forge))

    # Start the workflow by submitting data to the webform of your choice. You
    # will receive a new Submission object and a new WeldData.
    variables = {
        "isTest": True,
        "forgeEid": start_forge["eid"],
        "payload": creation_payload,
    }
    submission = submit_to_workflow_webform(anvil, variables=variables)

    # We have the newly created objects
    details_url = build_workflow_submission_details_url(
        org_slug, weld_slug,
        weld_data_eid=submission["weldData"]["eid"]
    )

    print("Workflow started")
    print(f"View on your dashboard: {details_url}")
    print(f"Submission eid: {submission['eid']}, WeldData eid {submission['weldData']['eid']}")
    print(json.dumps(submission))

    # Update the webform's submission data
    # Updating the submission uses the same mutation, but you just specify a
    # submission and weldData information. You can add more data or change
    # payload data
    print(">>> Updating the submission...")

    submission = submit_to_workflow_webform(anvil, variables={
        "forgeEid": start_forge["eid"],
        "weldDataEid": submission["weldData"]["eid"],
        "submissionEid": submission["eid"],

        # We'll update new fields, but you can also overwrite existing data
        "payload": update_payload,
    })

    print("Submission updated!")
    print(json.dumps(submission))


def build_workflow_submission_details_url(org_slug, weld_slug, weld_data_eid):
    return f"https://app.useanvil.com/org/{org_slug}/w/{weld_slug}/{weld_data_eid}"


def run():
    weld_slug = "sample-workflow"
    org_slug = None
    has_error = False

    try:
        org_slug = sys.argv[1]
    except IndexError:
        has_error = True

    if has_error or not org_slug:
        print("Enter your organization's slug as a command-line argument.")
        print(f"Usage: python {sys.argv[0]} my-org")
        return

    create_and_update_workflow_submission(org_slug, weld_slug)


if __name__ == "__main__":
    run()
