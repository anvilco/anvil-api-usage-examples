// Example: Create + update a Workflow submission via the Anvil API
//
// * Workflow API docs: https://www.useanvil.com/docs/api/workflows
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// By default the script uses the sample workflow in your organization when you
// signed up for an Anvil account.
//
// ANVIL_API_KEY=<yourAPIKey> node examples/create-update-workflow-submission.js <my-org-slug>
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

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

// Workflow information
const weldSlug = 'sample-workflow'
const organizationSlug = process.argv[2]
if (!organizationSlug) {
  console.log('Enter your organization\'s slug as the 1st argument')
  console.log(`Usage: node ${process.argv[1]} my-org`)
  process.exit(1)
}

// We will start the workflow by submitting this data
const creationPayload = {
  // We are relying on the sample workflow's Field Aliases here. See this help
  // article for more info:
  // https://www.useanvil.com/help/workflows/add-field-aliases-to-a-workflow
  shortText: `Workflow start! ${new Date().toLocaleString()}`,
}

// After the workflow has been started, we will update the new submission with this data
const updatePayload = {
  name: { firstName: 'Sally', lastName: 'Jones' },
  email: 'sally@example.com',
}

async function createAndUpdateWorkflowSubmission () {
  const anvilClient = new Anvil({ apiKey })

  //
  // Find the workflow
  //

  // Workflows are 'Weld' objects in Anvil's system
  console.log(`>>> Fetching Weld ${organizationSlug}/${weldSlug}`)
  const weld = await getWeld({
    anvilClient,
    weldSlug,
    organizationSlug,
  })
  console.log(`Found Weld ${weld.eid}\n`)

  //
  // Start the workflow
  //

  // Now we get the workflow's first webform to start the workflow
  // Webforms are `Forge` objects in Anvil's system
  const startForge = weld.forges[0]
  console.log(`>>> Starting workflow with webform`, formatJSON(startForge))

  // Start the workflow by submitting data to the webform of your choice. You
  // will receive a new Submission object and a new WeldData.
  let submission = await submitToWorkflowWebform({
    anvilClient,
    variables: {
      isTest: true,
      forgeEid: startForge.eid,
      payload: creationPayload
    },
  })

  // We have the newly created objects
  const detailsURL = buildWorkflowSubmissionDetailsURL({
    organizationSlug,
    weldSlug,
    weldDataEid: submission.weldData.eid
  })
  console.log(`Workflow started`)
  console.log(`View on your dashboard: ${detailsURL}`)
  console.log(`Submission eid: ${submission.eid}, WeldData eid ${submission.weldData.eid}`)
  console.log(formatJSON(submission), '\n')

  //
  // Update the webform's submission data
  //

  // Updating the submission uses the same mutation, but you just specify a
  // submission and weldData information. You can add more data or change
  // payload data
  console.log('>>> Updating the submission...')
  submission = await submitToWorkflowWebform({
    anvilClient,
    variables: {
      forgeEid: startForge.eid,
      weldDataEid: submission.weldData.eid,
      submissionEid: submission.eid,

      // We'll update new fields, but you can also overwrite existing data
      payload: updatePayload
    },
  })

  // We have the new objects
  console.log(`Submission updated!`)
  console.log(formatJSON(submission), '\n')
}


// This function will submit data to a Workflow's Webform. In our system these
// objects are called Weld (Workflow), and Forge (webform).
async function submitToWorkflowWebform ({ anvilClient, variables }) {
  // Ref docs:
  // https://www.useanvil.com/docs/api/graphql/reference/#operation-forgesubmit-Mutations
  const responseQuery = `{
    eid
    createdAt
    updatedAt
    resolvedPayload
    weldData {
      eid
      displayTitle
      isTest
      createdAt
      updatedAt
    }
  }`
  const { data, errors } = await anvilClient.forgeSubmit({ variables, responseQuery })
  if (errors) {
    console.log(formatJSON(errors))
    throw new Error('There were errors submitting to the workflow')
  }
  return data.data.forgeSubmit
}


// We need some information from the workflow, so we will fetch it from your
// account before we can submit data to it.
async function getWeld ({anvilClient, weldSlug, organizationSlug}) {
  // Ref docs:
  // https://www.useanvil.com/docs/api/graphql/reference/#operation-weld-Queries
  const weldQuery = `
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
  `

  const variables = { slug: weldSlug, organizationSlug }
  const { data, errors } = await anvilClient.requestGraphQL(
    {
      query: weldQuery,
      variables,
    },
    { dataType: 'json' }
  )

  if (errors) {
    console.error(formatJSON(errors))
    throw new Error(`Cannot find Weld: ${organizationSlug}/${weldSlug}`)
  }

  // Need to make sure it has webforms!
  const { weld } = data.data
  if (weld.forges.length < 1) {
    throw new Error(`Weld ${organizationSlug}/${weldSlug} has no webforms!`)
  }

  return weld
}


// Utilities

// Returns a URL where you can see the submission on your Anvil dashboard
function buildWorkflowSubmissionDetailsURL ({ organizationSlug, weldSlug, weldDataEid }) {
  return `https://app.useanvil.com/org/${organizationSlug}/w/${weldSlug}/${weldDataEid}`
}

function formatJSON (jsonObj) {
  return JSON.stringify(jsonObj, null, 2)
}

run(createAndUpdateWorkflowSubmission)
