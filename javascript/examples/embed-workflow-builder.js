// This script shows how to use Anvil's embedded PDF template builder feature.
//
// This script will:
//
// * Upload a new PDF to Anvil to create a PDF template (Cast object)
// * Generate an embeddable URL to the PDF template editor
// * Open the embedded experience in an iframe
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable, and organization EID in the
// ANVIL_ORGANIZATION_EID env var. See the readme in this directory for steps to
// set up a .env file with these options.
//
// Once you have your environment variables set up, you will need to enable
// iframe embedding:
//
// 1. Enable embedding in your organization https://www.useanvil.com/docs/api/e-signatures/#enabling-iframe-embedding
// 2. Add http://localhost:8089 to the list of trusted domains
//
// To have it open the embedded experience in an iframe, start the static
// server in another terminal. From the root of this language directory, run:
//
// $ yarn serve:static
//
// OR
//
// $ npm run serve:static
//
// Then run this script!
//
// $ node examples/embed-pdf-template-builder.js

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

const EXISTING_WELD_EID = 'ABsfXg56ShtfGlVbwEEP'

const apiKey = process.env.ANVIL_API_KEY

const embedPageURL = 'http://localhost:8089/static/embedded-builder-iframe.html?url='

const anvilClient = new Anvil({
  apiKey,
})

async function graphQLResponse (graphQLQuery) {
  const { data, errors } = await graphQLQuery
  if (errors) {
    throw new Error(JSON.stringify(errors, null, 2))
  }
  return data
}

function generateEmbedURL (variables) {
  const query = `
    mutation GenerateEmbedURL(
      $eid: String!,
      $type: String!,
      $validForSeconds: Int,
      $metadata: JSON,
      $options: JSON,
    ) {
      generateEmbedURL(
        eid: $eid
        type: $type
        validForSeconds: $validForSeconds
        metadata: $metadata
        options: $options
      ) {
        url,
        requestTokenEid
      }
    }
  `
  return graphQLResponse(
    anvilClient.requestGraphQL({
      query,
      variables,
    })
  )
}

async function main () {
  const templateEid = EXISTING_WELD_EID

  // Generate an embed URL for the PDF Template

  const generateResponse = await generateEmbedURL({
    eid: templateEid,
    type: 'edit-workflow',
    validForSeconds: 90 * 60, // 90 minutes
    metadata: {
      myUserId: '1234',
      anythingElse: 'you want',
    },
    options: {
      // Optional things to simplify the UI
      showSettingsPage: false,
      showWebformOptions: false,
    },
  })

  const { url: embedURL } = generateResponse.data.generateEmbedURL
  console.log(
    'generateEmbedURL response',
    JSON.stringify(generateResponse, null, 2)
  )

  // Pop open the embedded experience
  //
  // Please start the static server to make this work!
  //
  // $ yarn serve:static
  //
  // OR
  //
  // $ npm run serve:static
  require('child_process').exec(`open ${embedPageURL}${embedURL}`)
}

run(main)
