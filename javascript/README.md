# Anvil usage examples in JavaScript

Welcome to Anvil API usage examples in JavaScript! There are [example scripts](examples) for the following API actions:

* [Filling a PDF template with your own data](examples/fill-pdf.js)
* Generating a PDF from HTML and CSS
* Generating a PDF from Markdown
* Creating an e-sign packet
* Starting a workflow and updating workflow submissions
* Making an arbitrary GraphQL request

## Usage

All scripts will require that you have an Anvil API key. See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started#api-key) for help grabbing your API key.


Each script is setup to be runnable with minimal input. Once you have an API key, you can clone this repo and run the example scripts from this directory.
Scripts will output filled and generated PDFs into the [output directory](output).

[Fill a PDF template](examples/fill-pdf.js):

```sh
ANVIL_API_KEY=<yourAPIKey> node examples/fill-pdf.js
```
