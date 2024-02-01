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

Once you have this repo cloned, cd into this `python` directory and install the dependencies.

You can install your dependencies via the normal `pip` install process or with `poetry`.

Also note that `python-anvil` (the official Anvil API library) has a requirement of Python 3.6+.

### Install via `poetry`

Make sure you have `poetry` installed. If not check the official site for
installation instructions: https://python-poetry.org/docs/

```sh
cd python

# Install dependencies and local venv
poetry install

# Check if dependencies installed properly
$ poetry run anvil
Usage: anvil [OPTIONS] COMMAND [ARGS]...

Options:
  --debug / --no-debug
  --help                Show this message and exit.

Commands:
  cast                Fetch Cast data given a Cast eid.
  create-etch         Create an etch packet with a JSON file.
  current-user        Show details about your API user
  download-documents  Download etch documents
  fill-pdf            Fill PDF template with data.
  generate-etch-url   Generate an etch url for a signer
  generate-pdf        Generate a PDF.
  gql-query           Run a raw graphql query
  weld                Fetch weld info or list of welds.
```

### Install via `pip`

```sh
# When starting from the repository's base directory
cd python

# Create a virtualenv in the `./python` directory.
# Depending on your system, the `python` command may be linked to an older
# Python 2 version. `python3` should be available on most systems that have
# Python 3 installed.
python3 -m venv ./env

# Activate the newly created environment.
# This is where we'll install dependencies so they don't potentially interfere
# with anything on your system.
source ./env/bin/activate

# Install the dependencies
pip install -r requirements.txt

# Check if it installed properly by running the `anvil` CLI app
$ anvil

Usage: anvil [OPTIONS] COMMAND [ARGS]...

Options:
  --debug / --no-debug
  --help                Show this message and exit.

Commands:
  cast                Fetch Cast data given a Cast eid.
  create-etch         Create an etch packet with a JSON file.
  current-user        Show details about your API user
  download-documents  Download etch documents
  fill-pdf            Fill PDF template with data.
  generate-etch-url   Generate an etch url for a signer
  generate-pdf        Generate a PDF.
  gql-query           Run a raw graphql query
  weld                Fetch weld info or list of welds.
```

### Running the examples

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
