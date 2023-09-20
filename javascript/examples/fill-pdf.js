// Example: Fill a PDF via the Anvil API
//
// * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable. By default this script fills a
// global sample template.
//
// node examples/fill-pdf.js
//
// The filled PDF will be saved to `output/fill-output.pdf`. You can open the
// filled PDF immediately after saving the file on OSX machines with the
// `open` command:
//
// node examples/fill-pdf.js && open output/fill-output.pdf

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// The PDF template ID to fill. This PDF template ID is a sample template
// available to anyone.
//
// See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
// on setting up your own template
const pdfTemplateID = '05xXsZko33JIO6aq5Pnr'

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

const outputFilepath = path.join(__dirname, '..', 'output', 'fill-output.pdf')

async function fillPDF () {
  const anvilClient = new Anvil({ apiKey })
  const exampleData = getExampleFillData()

  console.log('Making fill request...')
  const { statusCode, data, errors } = await anvilClient.fillPDF(pdfTemplateID, exampleData)
  console.log('Finished! Status code:', statusCode) // => 200, 400, 404, etc

  if (statusCode === 200) {
    // `data` will be the filled PDF binary data. It is important that the
    // data is saved with no encoding! Otherwise the PDF file will be corrupt.
    fs.writeFileSync(outputFilepath, data, { encoding: null })
    console.log('Filled PDF saved to:', outputFilepath)
  } else {
    console.log('There were errors!')
    console.log(JSON.stringify(errors, null, 2))
  }
}

function getExampleFillData () {
  // JSON data to fill the PDF
  return {
    title: 'My PDF Title',
    fontSize: 10,
    textColor: '#333333',
    data: {
      shortText: 'HELLOOW',
      date: '2022-07-08',
      name: {
        firstName: 'Robin',
        mi: 'W',
        lastName: 'Smith'
      },
      email: 'testy@example.com',
      phone: {
        num: '5554443333',
        region: 'US',
        baseRegion: 'US'
      },
      usAddress: {
        street1: '123 Main St #234',
        city: 'San Francisco',
        state: 'CA',
        zip: '94106',
        country: 'US'
      },
      ssn: '456454567',
      ein: '897654321',
      checkbox: true,
      radioGroup: 'cast68d7e540afba11ecaf289fa5a354293a',
      decimalNumber: 12345.67,
      dollar: 123.45,
      integer: 12345,
      percent: 50.3,
      longText: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.',
      textPerLine: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.',
      textPerLetter: 'taH9QGigei6G5BtTUA4',
      image: 'https://placekitten.com/800/495'
    }
  }
}

run(fillPDF)
