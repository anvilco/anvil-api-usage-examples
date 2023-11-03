# Anvil usage examples in Ruby

Welcome to Anvil API usage examples in Ruby! There are [example scripts](examples) for the following API actions:

* [Making an arbitrary GraphQL request](examples/make_graphql_request.rb)

## Usage

All scripts will require that you have an Anvil API key. See [the getting started documentation](https://www.useanvil.com/docs/api/getting-started) for help grabbing your API key.

When you have your API key, copy `.env.example` in this folder to `.env`, then set ANVIL_API_KEY to your API key. Your `.env` will look like this:

```sh
ANVIL_API_KEY=yourAPIKey2uzgMH0ps4cyQyadhA2Wdt
```

Each script is set up to be runnable with minimal input. Once you have an API key, you can clone this repo and run the example scripts from this directory.

Once you have this repo cloned, cd into this `ruby` directory and install the dependencies:

```sh
cd ruby
bundle install
```

Scripts will output filled and generated PDFs into the [output directory](output).

[Make an arbitrary GraphQL request](examples/make_graphql_request.rb):

```sh
$ ruby examples/make_graphql_request.rb
```
