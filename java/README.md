# Anvil usage examples in Java

Welcome to Anvil API usage examples in Java! There are [example scripts](examples) for the following API actions.

All of these files are within the `com.useanvil.examples` namespace.

* [Filling a PDF template with your own data](src/main/java/com/useanvil/examples/runnable/FillPdf.java)
* [Generating a PDF from HTML and CSS](src/main/java/com/useanvil/examples/runnable/GenerateHtmlToPdf.java)
* [Generating a PDF from Markdown](src/main/java/com/useanvil/examples/runnable/GenerateMarkdownToPdf.java)
* [Creating an e-sign packet](src/main/java/com/useanvil/examples/runnable/CreateEtchESignPacket.java)
* [Starting a workflow and updating workflow submissions](src/main/java/com/useanvil/examples/runnable/CreateUpdateWorkflowSubmission.java)
* [Making an arbitrary GraphQL request](src/main/java/com/useanvil/examples/runnable/MakeGraphqlRequest.java)

## Requirements

* Java 11 and above
* Maven - https://maven.apache.org/install.html

These examples were built with Java 11 in mind -- mainly for `java.net.http.HttpClient`. If you would like to run these
examples in older versions of Java, the `com.useanvil.examples.client` clients should be changed to use a different
HTTP client.

## Usage

All scripts will require that you have an Anvil API key.
See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API
key.

Each example is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the
example scripts from this directory.


Once you have this repo cloned, cd into this `java` directory and install the dependencies:

```sh
cd java

# Download dependencies and built the .jar file
mvn clean package

[INFO] Building jar: /anvil-api-usage-examples/java/target/Examples-1.0-SNAPSHOT.jar

# Run the example that you want.
# Replace `<example-name>` with one of:
# make-graphql-request, fill-pdf,
# generate-html-to-pdf, generate-markdown-to-pdf,
# create-etch-packet, create-update-workflow
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar <example-name>
```

The program will output filled and generated PDFs into the [output directory](output).


[Fill a PDF template](src/main/java/com/useanvil/examples/runnable/FillPdf.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar fill-pdf
```

[Generate a PDF from HTML and CSS](src/main/java/com/useanvil/examples/runnable/GenerateHtmlToPdf.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar generate-html-to-pdf
```

[Generate a PDF from Markdown](src/main/java/com/useanvil/examples/runnable/GenerateMarkdownToPdf.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar generate-markdown-to-pdf
```

[Creating an e-sign packet](src/main/java/com/useanvil/examples/runnable/CreateEtchESignPacket.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar create-etch-packet <your-real-email@address.com>
```

[Start and update workflow submissions](src/main/java/com/useanvil/examples/runnable/CreateUpdateWorkflowSubmission.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar create-update-workflow <your-org-slug>
```

[Make an arbitrary GraphQL request](src/main/java/com/useanvil/examples/runnable/MakeGraphqlRequest.java):

```sh
ANVIL_API_KEY="YOUR_KEY_HERE" java -jar target/Examples-1.0-SNAPSHOT.jar make-graphql-request
```
