
// Run an async function and wait until it is finished.
module.exports = (fn) => {
  fn().then(() => {
    process.exit(0)
  }).catch((err) => {
    console.log(err.stack || err.message)
    process.exit(1)
  })
}
