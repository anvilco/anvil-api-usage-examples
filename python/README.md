# Anvil usage examples in Python

Welcome to Anvil API usage examples in Python! There are [example scripts](examples) for the following API actions:

* [Filling a PDF template with your own data](examples/fill-pdf.py)
* [Generating a PDF from HTML and CSS](examples/generate-html-to-pdf.py)
* [Generating a PDF from Markdown](examples/generate-markdown-to-pdf.py)
* [Creating an e-sign packet](examples/create-etch-e-sign-packet.py)
* [Starting a workflow and updating workflow submissions](examples/create-update-workflow-submission.py)
* [Making an arbitrary GraphQL request](examples/make-graphql-request.py)

## Usage

All scripts will require that you have an Anvil API key.
See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API
key.

Each script is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the
example scripts from this directory.

Once you have this repo cloned, cd into this `javascript` directory and install the dependencies:

```sh
cd javascript
yarn install
```

Scripts will output filled and generated PDFs into the [output directory](output).

[Fill a PDF template](examples/fill-pdf.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/fill-pdf.py
```

[Generate a PDF from HTML and CSS](examples/generate-html-to-pdf.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/generate-html-to-pdf.py
```

[Generate a PDF from Markdown](examples/generate-markdown-to-pdf.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/generate-markdown-to-pdf.py
```

[Creating an e-sign packet](examples/create-etch-e-sign-packet.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/create-etch-e-sign-packet.py <your-real-email@address.com>
```

[Start and update workflow submissions](examples/create-update-workflow-submission.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/create-update-workflow-submission.py <your-org-slug>
```

[Make an arbitrary GraphQL request](examples/make-graphql-request.py):

```sh
$ ANVIL_API_KEY=<yourAPIKey> python examples/make-graphql-request.py
```
