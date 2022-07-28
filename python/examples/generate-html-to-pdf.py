# Example: Generate a PDF from HTML and CSS via the Anvil API
#
# * PDF filling API docs: https://www.useanvil.com/docs/api/fill-pdf
# * Anvil Python client: https://github.com/anvilco/python-anvil/
# * Anvil Python docs: https://python-anvil.readthedocs.io/
# * See our invoice HTML template for a more complex HTML to PDF example:
# https://github.com/anvilco/html-pdf-invoice-template
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable. By default, this script fills a
# global sample template.
#
# ANVIL_API_KEY=<yourAPIKey> python examples/generate-html-to-pdf.py
#
# The filled PDF will be saved to `output/generate-html-output.pdf`. You can
# open the filled PDF immediately after saving the file on OSX machines with
# the `open` command:
#
# ANVIL_API_KEY=<yourAPIKey> python examples/generate-html-to-pdf.py \
#   && open output/generate-html-output.pdf

import os
from python_anvil.api import Anvil
from helpers import get_output_file_path

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
API_KEY = os.environ.get("ANVIL_API_KEY")

FILE_OUTPUT = get_output_file_path(__file__, "generate-html-output.pdf")


def get_data():
    return {
        "title": "Example HTML to PDF",
        "type": "html",
        "data": {
            "html": """
                <h1 class='header-one'>What is Lorem Ipsum?</h1>
                <p>
                    Lorem Ipsum is simply dummy text of the printing and typesetting
                    industry. Lorem Ipsum has been the industry's standard dummy text
                    ever since the <strong>1500s</strong>, when an unknown printer took
                    a galley of type and scrambled it to make a type specimen book.
                </p>
                <h3 class='header-two'>Where does it come from?</h3>
                <p>
                    Contrary to popular belief, Lorem Ipsum is not simply random text.
                    It has roots in a piece of classical Latin literature from
                    <i>45 BC</i>, making it over <strong>2000</strong> years old.
                </p>
            """,
            "css": """
                body { font-size: 14px; color: #171717; }
                .header-one { text-decoration: underline; }
                .header-two { font-style: underline; }
            """
        },
    }


def generate_html_pdf():
    anvil = Anvil(api_key=API_KEY)
    payload = get_data()

    print("Making HTML PDF generation request...")

    # `data` will be the filled PDF binary data. It is important that the
    # data is saved with no encoding! Otherwise, the PDF file will be corrupt.
    res = anvil.generate_pdf(payload)

    # Write the bytes to disk
    with open(FILE_OUTPUT, "wb") as f:
        f.write(res)

    print("Finished!")


if __name__ == "__main__":
    generate_html_pdf()
