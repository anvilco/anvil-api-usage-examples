// Example: Generate a PDF from Markdown via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=8hGbxtgNsA2nA1MH0ps4cyQyadhA2Wdt node examples/generate-markdown-to-pdf.js
//
// The filled PDF will be saved to `output/generate-markdown-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// ANVIL_API_KEY=8hGbxtgNsA2nA1MH0ps4cyQyadhA2Wdt \
//   node examples/generate-markdown-to-pdf.js && open output/generate-markdown-output.pdf

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

const outputFilepath = path.join(__dirname, '..', 'output', 'generate-markdown-output.pdf')

async function generateMarkdownPDF () {
  const anvilClient = new Anvil({ apiKey })
  const exampleData = getExampleMarkdownToPDFData()
  const { statusCode, data, errors } = await anvilClient.generatePDF(exampleData)

  console.log('Making Markdown PDF generation request...')
  console.log('Finished! Status code:', statusCode) // => 200, 400, 404, etc

  if (statusCode === 200) {
    // `data` will be the filled PDF binary data. It is important that the
    // data is saved with no encoding! Otherwise the PDF file will be corrupt.
    fs.writeFileSync(outputFilepath, data, { encoding: null })
    console.log('Generated PDF saved to:', outputFilepath)
  } else {
    console.log('There were errors!')
    console.log(JSON.stringify(errors, null, 2))
  }
}

function getExampleMarkdownToPDFData () {
  return {
    title: 'Example Invoice',
    data: [{
      label: 'Name',
      content: 'Sally Jones',
    }, {
      content: 'Lorem **ipsum** dolor sit _amet_, consectetur adipiscing elit, sed [do eiusmod](https://www.useanvil.com/docs) tempor incididunt ut labore et dolore magna aliqua. Ut placerat orci nulla pellentesque dignissim enim sit amet venenatis.\n\nMi eget mauris pharetra et ultrices neque ornare aenean.\n\n* Sagittis eu volutpat odio facilisis.\n\n* Erat nam at lectus urna.',
    }, {
      table: {
        firstRowHeaders: true,
        rows: [
          ['Description', 'Quantity', 'Price'],
          ['4x Large Widgets', '4', '$40.00'],
          ['10x Medium Sized Widgets in dark blue', '10', '$100.00'],
          ['10x Small Widgets in white', '6', '$60.00'],
        ],
      },
    }],
  }
}

run(generateMarkdownPDF)
