// Example: Generate a PDF from Markdown via the Anvil API
//
// * PDF generation API docs: https://www.useanvil.com/docs/api/generate-pdf
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// ANVIL_API_KEY=<yourAPIKey> node examples/generate-markdown-to-pdf.js
//
// The filled PDF will be saved to `output/generate-markdown-output.pdf`. You can
// open the filled PDF immediately after saving the file on OSX machines with
// the `open` command:
//
// ANVIL_API_KEY=<yourAPIKey> \
//   node examples/generate-markdown-to-pdf.js && open output/generate-markdown-output.pdf