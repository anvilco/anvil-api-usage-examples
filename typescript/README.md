# Anvil usage examples in TypeScript

Welcome to Anvil API usage examples in TypeScript! There are [example scripts](examples) for the following API actions:

* [Filling a PDF template with your own data](examples/fill-pdf.ts)
* [Generating a PDF from HTML and CSS](examples/generate-html-to-pdf.ts)
* [Generating a PDF from Markdown](examples/generate-markdown-to-pdf.ts)
* [Creating an e-sign packet](examples/create-etch-e-sign-packet.ts)
* [Starting a workflow and updating workflow submissions](examples/create-update-workflow-submission.ts)
* [Making an arbitrary GraphQL request](examples/make-graphql-request.ts)

## Usage

All scripts will require that you have an Anvil API key. See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API key.

When you have your API key, copy `.env.example` in this folder to `.env`, then set ANVIL_API_KEY to your API key. Your `.env` will look like this:

```sh
ANVIL_API_KEY=yourAPIKey2uzgMH0ps4cyQyadhA2Wdt
```

Each script is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the example scripts from this directory.

Once you have this repo cloned, cd into this `typescript` directory and install the dependencies:

```sh
cd typescript
yarn install
```

Scripts will output filled and generated PDFs into the [output directory](output).

[Fill a PDF template](examples/fill-pdf.ts):

```sh
$ yarn ts-node examples/fill-pdf.ts
```

[Generate a PDF from HTML and CSS](examples/generate-html-to-pdf.ts):

```sh
$ yarn ts-node examples/generate-html-to-pdf.ts
```

[Generate a PDF from Markdown](examples/generate-markdown-to-pdf.ts):

```sh
$ yarn ts-node examples/generate-markdown-to-pdf.ts
```

[Creating an e-sign packet](examples/create-etch-e-sign-packet.ts):

```sh
$ yarn ts-node examples/create-etch-e-sign-packet.ts <your-real-email@address.com>
```

[Start and update workflow submissions](examples/create-update-workflow-submission.ts):

```sh
$ yarn ts-node examples/create-update-workflow-submission.ts <your-org-slug>
```

[Make an arbitrary GraphQL request](examples/make-graphql-request.ts):

```sh
$ yarn ts-node examples/make-graphql-request.ts
```
