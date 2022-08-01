# Example: Fill a PDF via the Anvil API
#
# * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
# * Anvil Python client: https://github.com/anvilco/python-anvil/
# * Anvil Python docs: https://python-anvil.readthedocs.io/
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable. By default, this script fills a
# global sample template.
#
# ANVIL_API_KEY=<yourAPIKey> python examples/fill-pdf.py
#
# The filled PDF will be saved to `output/fill-output.pdf`. You can open the
# filled PDF immediately after saving the file on OSX machines with the
# `open` command:
#
# ANVIL_API_KEY=<yourAPIKey> python examples/fill-pdf.py && open output/fill-output.pdf

import os
from python_anvil.api import Anvil
from helpers import get_output_file_path

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
API_KEY = os.environ.get("ANVIL_API_KEY")

FILE_OUTPUT = get_output_file_path(__file__, "fill-output.pdf")

# The PDF template ID to fill. This PDF template ID is a sample template
# available to anyone.
#
# See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
# on setting up your own template
PDF_TEMPLATE_EID = "f9eQzbUgCCRVDrd4gt8b"

# PDF fill data can be an instance of `FillPDFPayload` or a plain dict.
# `FillPDFPayload` is from `python_anvil.api_resources.payload import FillPDFPayload`.
# Keep in mind that, if using a plain dict, that the keys are in typical
# Python snake_case with underscores, and not camelCase.
# If you'd like to use camelCase on all data, you can call `Anvil.fill_pdf()`
# with a full JSON payload instead.
FILL_DATA = {
    "title": "My PDF Title",
    "font_size": 10,
    "text_color": "#333333",
    "data": {
        "shortText": "HELLOOW",
        "date": "2022-07-08",
        "name": {
            "firstName": "Robin",
            "mi": "W",
            "lastName": "Smith"
        },
        "email": "testy@example.com",
        "phone": {
            "num": "5554443333",
            "region": "US",
            "baseRegion": "US"
        },
        "usAddress": {
            "street1": "123 Main St #234",
            "city": "San Francisco",
            "state": "CA",
            "zip": "94106",
            "country": "US"
        },
        "ssn": "456454567",
        "ein": "897654321",
        "checkbox": True,
        "radioGroup": "cast68d7e540afba11ecaf289fa5a354293a",
        "decimalNumber": 12345.67,
        "dollar": 123.45,
        "integer": 12345,
        "percent": 50.3,
        "longText": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.",
        "textPerLine": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor.",
        "textPerLetter": "taH9QGigei6G5BtTUA4",
        "image": "https://placekitten.com/800/495"
    }
}


def main():
    anvil = Anvil(api_key=API_KEY)

    print("Making fill request...")

    # `data` will be the filled PDF binary data. It is important that the
    # data is saved with no encoding! Otherwise, the PDF file will be corrupt.
    res = anvil.fill_pdf(PDF_TEMPLATE_EID, FILL_DATA)

    # Write the bytes to disk
    with open(FILE_OUTPUT, "wb") as f:
        f.write(res)


if __name__ == "__main__":
    main()
