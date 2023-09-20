// Example: Generate a PDF from HTML and CSS via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
// * See our invoice HTML template for a more complex HTML to PDF example:
//   https://github.com/anvilco/html-pdf-invoice-template
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// node examples/generate-html-to-pdf.js
//
// The filled PDF will be saved to `output/generate-html-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// node examples/generate-html-to-pdf.js && open output/generate-html-output.pdf

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

const outputFilepath = path.join(__dirname, '..', 'output', 'generate-html-output.pdf')

async function generateHTMLPDF () {
  const anvilClient = new Anvil({ apiKey })
  const exampleData = getExampleHTMLToPDFData()
  const { statusCode, data, errors } = await anvilClient.generatePDF(exampleData)

  console.log('Making HTML PDF generation request...')
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

function getExampleHTMLToPDFData () {
  return {
    title: 'Example HTML to PDF',
    type: 'html',
    data: {
      html: `
        <h1 class='header-one'>What is Lorem Ipsum?</h1>
        <p>
          Lorem Ipsum is simply dummy text of the printing and typesetting
          industry. Lorem Ipsum has been the industry's standard dummy text
          ever since the <strong>1500s</strong>, when an unknown printer took
          a galley of type and scrambled it to make a type specimen book.
        </p>
        <h3 class='header-two'>Where does it come from?</h3>
        <p>
          Contrary to popular belief, Lorem Ipsum is not simply random text.
          It has roots in a piece of classical Latin literature from
          <i>45 BC</i>, making it over <strong>2000</strong> years old.
        </p>
      `,
      css: `
        body { font-size: 14px; color: #171717; }
        .header-one { text-decoration: underline; }
        .header-two { font-style: underline; }
      `,
    },
  }
}

run(generateHTMLPDF)
