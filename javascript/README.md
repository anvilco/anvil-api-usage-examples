# Anvil usage examples in JavaScript

Welcome to Anvil API usage examples in JavaScript! There are [example scripts](examples) for the following API actions:

* [Filling a PDF template with your own data](examples/fill-pdf.js)
* [Generating a PDF from HTML and CSS](examples/generate-html-to-pdf.js)
* [Generating a PDF from Markdown](examples/generate-markdown-to-pdf.js)
* [Creating an e-sign packet](examples/create-etch-e-sign-packet.js)
* [Starting a workflow and updating workflow submissions](examples/create-update-workflow-submission.js)
* [Making an arbitrary GraphQL request](examples/make-graphql-request.js)

## Usage

All scripts will require that you have an Anvil API key. See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API key.

When you have your API key, copy `.env.example` in this folder to `.env`, then set ANVIL_API_KEY to your API key. Your `.env` will look like this:

```sh
ANVIL_API_KEY=yourAPIKey2uzgMH0ps4cyQyadhA2Wdt
ANVIL_ORGANIZATION_EID=yourEID20Hw5RkPIeabc
```

Each script is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the example scripts from this directory.

Once you have this repo cloned, cd into this `javascript` directory and install the dependencies:

```sh
cd javascript
yarn install
```

Scripts will output filled and generated PDFs into the [output directory](output).

[Fill a PDF template](examples/fill-pdf.js):

```sh
$ node examples/fill-pdf.js
```

[Generate a PDF from HTML and CSS](examples/generate-html-to-pdf.js):

```sh
$ node examples/generate-html-to-pdf.js
```

[Generate a PDF from Markdown](examples/generate-markdown-to-pdf.js):

```sh
$ node examples/generate-markdown-to-pdf.js
```

[Creating an e-sign packet](examples/create-etch-e-sign-packet.js):

```sh
$ node examples/create-etch-e-sign-packet.js <your-real-email@address.com>
```

[Start and update workflow submissions](examples/create-update-workflow-submission.js):

```sh
$ node examples/create-update-workflow-submission.js <your-org-slug>
```

[Make an arbitrary GraphQL request](examples/make-graphql-request.js):

```sh
$ node examples/make-graphql-request.js
```
