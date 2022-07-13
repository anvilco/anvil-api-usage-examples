// Example: Create an e-sign packet via the Anvil API
//
// * E-sign API docs: https://www.useanvil.com/docs/api/e-signatures
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> node examples/create-etch-e-sign-packet.js <your.real.email@ex.com>
//
// This script will create an e-sign packet with one signer and two documents.
// Then it will send a signature request to the email you specified.
// Use your real email address!
//
// This script will:
//
// * Use a template sample PDF document
// * Upload a new PDF document
// * Insert data (names, emails, addresses, etc) on both PDFs
// * Have the signer sign both documents
//
// After you run the script, you can find the new packet from the e-sign "Sent"
// area in your dashboard. The dashboard URL to the new packet will be output as
// well.

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

// The PDF template ID to fill. This PDF template ID is a sample template
// available to anyone.
//
// See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
// on setting up your own template
const pdfTemplateID = '05xXsZko33JIO6aq5Pnr'

// The second file is an NDA we'll upload and specify the field locations
const fileUploadPath = path.join(__dirname, '..', '..', 'static', 'test-pdf-nda.pdf')

// Signer information
const signerName = 'Testy Signer'
const signerEmail = process.argv[2]

if (!signerEmail) {
  console.log('Enter your email address as the script\'s 1st argument')
  console.log(`Usage: node ${process.argv[1]} your.real.email@example.com`)
  process.exit(1)
}

async function createEtchPacket () {
  const anvilClient = new Anvil({ apiKey })
  const ndaFile = Anvil.prepareGraphQLFile(fileUploadPath)
  const variables = getPacketVariables(ndaFile)

  console.log('Creating Etch e-sign packet...')
  const { statusCode, data, errors } = await anvilClient.createEtchPacket({ variables })
  console.log('Finished! Status code:', statusCode) // => 200, 400, 404, etc

  if (errors) {
    // Note: because of the nature of GraphQL, statusCode may be a 200 even when
    // there are errors.
    console.log('There were errors!')
    console.log(JSON.stringify(errors, null, 2))
  } else {
    const packetDetails = data.data.createEtchPacket
    console.log('Visit the new packet on your dashboard:', packetDetails.detailsURL)
    console.log(JSON.stringify(packetDetails, null, 2))
  }
}

function getPacketVariables (ndaFile) {
  return {
    // Indicate the packet is all ready to send to the
    // signers. An email will be sent to the first signer.
    isDraft: false,

    // Test packets will use development signatures and
    // not count toward your billed packets
    isTest: true,

    // Subject & body of the emails to signers
    name: `Test Docs - ${signerName}`,
    signatureEmailSubject: 'Custom email subject',
    signatureEmailBody: 'Custom please sign these documents....',

    // Merge all PDFs into one PDF before signing.
    // Signing users will get one PDF instead of all PDFs as separate files.
    // mergePDFs: false,

    files: [
      {
        // Our ID we will use to reference and fill it with data.
        // It can be any string you want!
        id: 'sampleTemplate',

        // The id to the ready-made sample template. Fields and their ids are
        // specified when building out the template in the UI.
        castEid: pdfTemplateID,
      },
      {
        // This is a file we will upload and specify the fields ourselves
        id: 'fileUploadNDA',
        title: 'Demo NDA',
        file: ndaFile, // The file to be uploaded
        fields: [
          {
            id: 'recipientName',
            type: 'fullName',
            rect: { x: 223, y: 120, height: 12, width: 140 },
            pageNum: 0,
          },
          {
            id: 'recipientEmail',
            type: 'email',
            rect: { x: 367, y: 120, height: 12, width: 166 },
            pageNum: 0,
          },
          {
            id: 'recipientSignatureName',
            type: 'fullName',
            rect: { x: 107, y: 374, height: 22, width: 157 },
            pageNum: 1,
          },
          {
            id: 'recipientSignature',
            type: 'signature',
            rect: { x: 270, y: 374, height: 22, width: 142 },
            pageNum: 1,
          },
          {
            id: 'recipientSignatureDate',
            type: 'signatureDate',
            rect: { x: 419, y: 374, height: 22, width: 80 },
            pageNum: 1,
          },
        ],
      },
    ],

    data: {
      // This data will fill the PDF before it's sent to any signers.
      // IDs here were set up on each field while templatizing the PDF.
      payloads: {
        // 'sampleTemplate' is the sample template ID specified above
        sampleTemplate: {
          data: {
            name: signerName,
            email: signerEmail,
            ssn: '456454567',
            ein: '897654321',
            usAddress: {
              street1: '123 Main St #234',
              city: 'San Francisco',
              state: 'CA',
              zip: '94106',
              country: 'US',
            },
          },
        },

        // 'fileUploadNDA' is the NDA's file ID specified above
        fileUploadNDA: {
          fontSize: 8,
          textColor: '#0000CC',
          data: {
            // The IDs here match the fields we created in the
            // files property above
            recipientName: signerName,
            recipientSignatureName: signerName,
            recipientEmail: signerEmail,
          },
        },
      },
    },

    signers: [
      // Signers will sign in the order they are specified in this array.
      // e.g. `employer` will sign after `employee` has finished signing
      {
        // `employee` is the first signer
        id: 'signer1',
        name: signerName,
        email: signerEmail,
        signerType: 'email',

        // These fields will be presented when this signer signs.
        // The signer will need to click through the signatures in
        // the order of this array.
        fields: [
          {
            // File IDs are specified in the `files` property above
            fileId: 'sampleTemplate',
            fieldId: 'signature',
          },
          {
            fileId: 'sampleTemplate',
            fieldId: 'signatureInitial',
          },
          {
            fileId: 'sampleTemplate',
            fieldId: 'signatureDate',
          },
          {
            fileId: 'sampleTemplate',
            fieldId: 'signerName',
          },
          {
            fileId: 'sampleTemplate',
            fieldId: 'signerEmail',
          },
          {
            fileId: 'fileUploadNDA',
            // NDA field IDs are specified in the `files[].fields` property above
            fieldId: 'recipientSignature',
          },
          {
            fileId: 'fileUploadNDA',
            fieldId: 'recipientSignatureDate',
          },
        ],
      },
    ],
  }
}

run(createEtchPacket)
