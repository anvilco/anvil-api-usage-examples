# Example: Generate a PDF from HTML and CSS via the Anvil API
#
# * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
# * Anvil Python client: https://github.com/anvilco/python-anvil/
# * Anvil Python docs: https://python-anvil.readthedocs.io/
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable.
#
# ANVIL_API_KEY=<yourAPIKey> python examples/generate-markdown-to-pdf.py
#
# The filled PDF will be saved to `output/generate-markdown-output.pdf`. You can
# open the filled PDF immediately after saving the file on OSX machines with
# the `open` command:
#
# ANVIL_API_KEY=<yourAPIKey> python examples/generate-markdown-to-pdf.py \
#   && open output/generate-markdown-output.pdf

import os
from python_anvil.api import Anvil
from helpers import get_output_file_path

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
API_KEY = os.environ.get("ANVIL_API_KEY")

FILE_OUTPUT = get_output_file_path(__file__, "generate-markdown-output.pdf")


def get_data():
    return {
        "title": "Example Invoice",
        "data": [{
            "label": "Name",
            "content": "Sally Jones",
        }, {
            "content": """
Lorem **ipsum** dolor sit _amet_, consectetur adipiscing elit, sed [do eiusmod](https://www.useanvil.com/docs) tempor incididunt ut labore et dolore magna aliqua. Ut placerat orci nulla pellentesque dignissim enim sit amet venenatis.
\n\n
Mi eget mauris pharetra et ultrices neque ornare aenean.
\n\n
* Sagittis eu volutpat odio facilisis.
\n\n
* Erat nam at lectus urna.
            """,
        }, {
            "table": {
                "firstRowHeaders": True,
                "rows": [
                    ["Description", "Quantity", "Price"],
                    ["4x Large Widgets", "4", "$40.00"],
                    ["10x Medium Sized Widgets in dark blue", "10", "$100.00"],
                    ["10x Small Widgets in white", "6", "$60.00"],
                ],
            },
        }],
    }


def generate_html_pdf():
    anvil = Anvil(api_key=API_KEY)
    payload = get_data()

    print('Making Markdown PDF generation request...')

    # `data` will be the filled PDF binary data. It is important that the
    # data is saved with no encoding! Otherwise, the PDF file will be corrupt.
    res = anvil.generate_pdf(payload)

    # Write the bytes to disk
    with open(FILE_OUTPUT, "wb") as f:
        f.write(res)

    print("Finished!")


if __name__ == "__main__":
    generate_html_pdf()
