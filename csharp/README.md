# Anvil usage examples in JavaScript

Welcome to Anvil API usage examples in JavaScript! There are [example scripts](examples) for the following API actions:

* [Filling a PDF template with your own data](examples/FillPdf.cs)
* [Generating a PDF from HTML and CSS](examples/GenerateHtmlToPdf.cs)
* [Generating a PDF from Markdown](examples/GenerateMarkdownToPdf.cs)
* [Creating an e-sign packet](examples/CreateEtchESignPacket.cs)
* [Starting a workflow and updating workflow submissions](examples/CreateUpdateWorkflowSubmission.cs)
* [Making an arbitrary GraphQL request](examples/MakeGraphqlRequest.cs)

## Usage

All scripts will require that you have an Anvil API key.
See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API
key.

Each script is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the
example scripts from this directory.

Once you have this repo cloned, cd into this `javascript` directory and install the dependencies:

```sh
cd csharp

# Download dependencies
dotnet restore

# Run the example that you want.
# Replace `<example-name>` with one of:
# make-graphql-request, fill-pdf,
# generate-html-to-pdf, generate-markdown-to-pdf,
# create-etch-packet, create-update-workflow
ANVIL_API_KEY="YOUR_KEY_HERE" dotnet run <example-name>
```

Scripts will output filled and generated PDFs into the [output directory](output).

[Fill a PDF template](examples/FillPdf.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run fill-pdf
```

[Generate a PDF from HTML and CSS](examples/GenerateHtmlToPdf.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run generate-html-to-pdf
```

[Generate a PDF from Markdown](examples/GenerateMarkdownToPdf.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run generate-markdown-to-pdf
```

[Creating an e-sign packet](examples/CreateEtchESignPacket.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run create-etch-packet <your-real-email@address.com>
```

[Start and update workflow submissions](examples/CreateUpdateWorkflowSubmission.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run create-update-workflow <your-org-slug>
```

[Make an arbitrary GraphQL request](examples/MakeGraphqlRequest.cs):

```sh
$ ANVIL_API_KEY=<yourAPIKey> dotnet run make-graphql-request
```