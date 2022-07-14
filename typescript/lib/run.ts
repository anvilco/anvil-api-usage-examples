import 'dotenv/config'

// Run an async function and wait until it is finished.
export default function run (fn: Function) {
  fn().then(() => {
    process.exit(0)
  }).catch((err: Error) => {
    console.log(err.stack || err.message)
    process.exit(1)
  })
}
