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

const apiKey = process.env.ANVIL_API_KEY

const filePath = path.join(__dirname, '..', '..', 'static', 'sample-template-fillable.pdf')
const title = 'Test Upload'
const filename = 'Test Embedded Upload.pdf'
const mimetype = 'application/pdf'

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

async function createCast (variables) {
  const query = `
    mutation CreateCast(
      $title: String,
      $file: Upload!,
      $isTemplate: Boolean,
      $detectFields: Boolean,
      $allowedAliasIds: [String],
    ) {
      createCast (
        title: $title,
        file: $file,
        isTemplate: $isTemplate,
        allowedAliasIds: $allowedAliasIds,
        detectFields: $detectFields,
      ) {
        eid
        name
        title
        isTemplate
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
  const fileBytes = fs.readFileSync(filePath)
  const buffer = Buffer.from(fileBytes)
  const file = Anvil.prepareGraphQLFile(buffer, {
    filename, // Required with a buffer or stream
    mimetype,
  })

  // Upload the PDF template

  const createCastResponse = await createCast({
    title,
    file,
    isTemplate: true,
  })

  const newPDFTemplate = createCastResponse.data.createCast
  console.log(
    'createCast response',
    JSON.stringify(createCastResponse, null, 2)
  )

  // Generate an embed URL for the PDF Template

  const generateResponse = await generateEmbedURL({
    eid: newPDFTemplate.eid,
    type: 'edit-pdf-template',
    // 10 minutes
    validForSeconds: 10 * 60,
    metadata: {
      myUserId: '1234',
      anythingElse: 'you want',
    },
    options: {
      pageTitle: 'Title of the page',
      mode: 'preset-fields',
      title: 'Welcome',
      description: 'Please draw fields indicated below.',
      selectionDescription:
        'Select the field that best represents the box drawn.',

      // Other possible copy changes
      // finishButtonText: 'Custom text',
      // selectionAddAnotherFieldText: 'Plz add another field',

      // You can hide the title bar with showPageTitleBar. This removes the
      // finish button so you can show your own finish button. You will need to
      // submit the iframe with the `castEditSubmit` iframe event.
      // showPageTitleBar: true,

      fields: [
        // * `aliasId` can be anything you'd like
        //   * You will use the aliasId you choose to fill PDF data and assign signers to the field
        //   * https://www.useanvil.com/docs/api/fill-pdf/#field-ids
        // * All types: https://www.useanvil.com/docs/api/fill-pdf/#all-field-types
        {
          name: 'Full name',
          type: 'fullName',
          aliasId: 'name',
          required: true,

          // optional fields
          // alignment: 'center', // `left`, `center`, `right`
          // fontSize: '12',
          // fontWeight: 'boldItalic', // 'normal', `bold`, `boldItalic`, `italic`
          // fontFamily: 'Futura', // Any google font, 'Helvetica', 'Times new roman', 'Courier'
          // textColor: '#a00000',
        },
        {
          name: 'Email',
          type: 'email',
          aliasId: 'email',
          required: true,
        },
        {
          name: 'Date of birth',
          type: 'date',
          aliasId: 'dob',
          required: false,

          // optional date fields:
          format: 'MMMM Do YYYY', // see moment.js docs
        },
        {
          name: 'Client signature',
          type: 'signature',
          aliasId: 'clientSignature',
          required: false,
        },
        {
          name: 'Client initials',
          type: 'initial',
          aliasId: 'clientInitials',
          required: false,
        },
        {
          name: 'Client signature date',
          type: 'signatureDate',
          aliasId: 'clientSignatureDate',
          required: false,
        },
      ],
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
