// Example: Make an arbitrary GraphQL request to the Anvil API
//
// Feel free to copy and paste queries and mutations from the GraphQL reference
// docs into the functions in this script.
//
// * GraphQL guide: https://www.useanvil.com/docs/api/graphql
// * GraphQL ref docs: https://www.useanvil.com/docs/api/graphql/reference
// * Anvil Node.js client: https://github.com/anvilco/node-anvil
//
// This script is runnable as is, all you need to do is supply your own API key
// in the ANVIL_API_KEY environment variable.
//
// node examples/make-graphql-request.js

const fs = require('fs')
const path = require('path')
const Anvil = require('@anvilco/anvil')

const run = require('../lib/run')

// Get your API key from your Anvil organization settings.
// See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
const apiKey = process.env.ANVIL_API_KEY

async function callQueries () {
  const anvilClient = new Anvil({ apiKey })
  const currentUser = await callCurrentUserQuery({ anvilClient })
  console.log('currentUser:', JSON.stringify(currentUser, null, 2))

  const firstWeld = currentUser.organizations[0].welds[0]
  if (firstWeld) {
    const weldFromQuery = await callWeldQuery({ anvilClient, weldEid: firstWeld.eid })
    console.log('First weld details:', JSON.stringify(weldFromQuery, null, 2))
  } else {
    console.log('No welds found! Skipping the weld call')
  }
}

// The currentUser query takes no variables
async function callCurrentUserQuery ({ anvilClient }) {
  // See the reference docs for examples of all queries and mutations:
  // https://www.useanvil.com/docs/api/graphql/reference/
  //
  // Queries and mutations both use this same syntax
  const currentUserQuery = `
    query CurrentUser {
      currentUser {
        eid
        name
        organizations {
          eid
          slug
          name
          casts {
            eid
            name
          }
          welds {
            eid
            name
          }
        }
      }
    }
  `

  const variables = {}
  const { data, errors } = await anvilClient.requestGraphQL(
    {
      query: currentUserQuery,
      variables,
    },
    { dataType: 'json' }
  )

  if (errors) {
    // Note: because of the nature of GraphQL, statusCode may be a 200 even when
    // there are errors.
    console.log(JSON.stringify(errors, null, 2))
    throw new Error('There were errors fetching the current user')
  }
  return data.data.currentUser
}

// The weld() query is an example of a query that takes variables
async function callWeldQuery ({ anvilClient, weldEid }) {
  const currentUserQuery = `
    query WeldQuery (
      $eid: String,
    ) {
      weld (
        eid: $eid,
      ) {
        eid
        name
        forges {
          eid
          slug
          name
        }
      }
    }
  `

  const variables = { eid: weldEid }
  const { data, errors } = await anvilClient.requestGraphQL(
    {
      query: currentUserQuery,
      variables,
    },
    { dataType: 'json' }
  )

  if (errors) {
    // Note: because of the nature of GraphQL, statusCode may be a 200 even when
    // there are errors.
    console.log(JSON.stringify(errors, null, 2))
    throw new Error('There were errors fetching the current user')
  }
  return data.data.weld
}

run(callQueries)
