# Example: Create an e-sign packet via the Anvil API
#
# * E-sign API docs: https://www.useanvil.com/docs/api/e-signatures
# * Anvil Python client: https://github.com/anvilco/python-anvil/
# * Anvil Python docs: https://python-anvil.readthedocs.io/
#
# This script is runnable as is, all you need to do is supply your own API key
# in the ANVIL_API_KEY environment variable.
#
# ANVIL_API_KEY=<yourAPIKey> python examples/create-etch-e-sign-packet.py <your.real.email@ex.com>
#
# This script will create an e-sign packet with one signer and two documents.
# Then it will send a signature request to the email you specified.
# Use your real email address!
#
# This script will:
#
# * Use a template sample PDF document
# * Upload a new PDF document
# * Insert data (names, emails, addresses, etc) on both PDFs
# * Have the signer sign both documents
#
# After you run the script, you can find the new packet from the e-sign "Sent"
# area in your dashboard. The dashboard URL to the new packet will be output as
# well.

import os
import sys
import base64
from python_anvil.api import Anvil
from python_anvil.api_resources.mutations.create_etch_packet import CreateEtchPacket
from python_anvil.api_resources.payload import (
    DocumentUpload,
    Base64Upload,
    EtchCastRef,
    SignatureField,
    EtchSigner,
    SignerField,
)

# Get your API key from your Anvil organization settings.
# See https://www.useanvil.com/docs/api/getting-started#api-key for more details.
from examples.helpers import get_output_file_path, get_test_pdf_path

API_KEY = os.environ.get("ANVIL_API_KEY")

# The PDF template ID to fill. This PDF template ID is a sample template
# available to anyone.
#
# See https://www.useanvil.com/help/tutorials/set-up-a-pdf-template for details
# on setting up your own template
PDF_TEMPLATE_ID = "05xXsZko33JIO6aq5Pnr"

# The second file is an NDA we'll upload and specify the field locations
FILE_UPLOAD_PATH = get_output_file_path(__file__, "test-pdf-nda.pdf")

TEST_PDF_PATH = get_test_pdf_path(__file__)


def get_signer_data(signer_name, signer_email):
    # Signers will sign in the order they are specified in this array.
    # e.g. `employer` will sign after `employee` has finished signing
    return EtchSigner(
        # `employee` is the first signer
        id="signer1",
        name=signer_name,
        email=signer_email,
        # These fields will be presented when this signer signs.
        # The signer will need to click through the signatures in
        # the order of this array.
        fields=[
            SignerField(
                # File IDs are specified in the `files` property in the main
                # packet builder.
                file_id="sampleTemplate",
                field_id="signature"
            ),
            SignerField(
                fileId="sampleTemplate",
                fieldId="signatureInitial",
            ),
            SignerField(
                fileId="sampleTemplate",
                fieldId="signatureDate",
            ),
            SignerField(
                fileId="sampleTemplate",
                fieldId="signerName",
            ),
            SignerField(
                fileId="sampleTemplate",
                fieldId="signerEmail",
            ),
            SignerField(
                fileId="fileUploadNDA",
                # NDA field IDs are specified in the `files[].fields` property above
                fieldId="recipientSignature",
            ),
            SignerField(
                fileId="fileUploadNDA",
                fieldId="recipientSignatureDate",
            ),
        ]
    )


def get_file_payloads(file_path):
    # Get your file(s) ready to sign.
    # For this example, the PDF hasn't been uploaded to Anvil yet, so we need
    # to: open the file, upload the file as a base64 encoded payload along with
    # some data about where the user should sign.
    with open(file_path, "rb") as f:
        b64file = base64.b64encode(f.read())

    if not b64file:
        raise ValueError('base64-encoded file not found')

    file_upload_nda = DocumentUpload(
        # This is a file we will upload and specify the fields ourselves
        id="fileUploadNDA",
        title="Demo NDA",
        file=Base64Upload(
            data=b64file.decode("utf-8"),
            filename="fileUploadNDA.pdf",
        ),
        fields=[
            SignatureField(
                id="recipientName",
                type="fullName",
                page_num=0,
                rect=dict(x=223, y=120, height=12, width=140)
            ),
            SignatureField(
                id="recipientEmail",
                type="email",
                page_num=0,
                rect=dict(x=367, y=120, height=12, width=166)
            ),
            SignatureField(
                id="recipientSignatureName",
                type="fullName",
                page_num=1,
                rect=dict(x=107, y=374, height=22, width=157)
            ),
            SignatureField(
                id="recipientSignature",
                type="signature",
                page_num=1,
                rect=dict(x=270, y=374, height=22, width=142)
            ),
            SignatureField(
                id="recipientSignatureDate",
                type="signatureDate",
                page_num=1,
                rect=dict(x=419, y=374, height=22, width=80)
            ),
        ]
    )

    sample_template = EtchCastRef(
        # Our ID we will use to reference and fill it with data.
        # It can be any string you want!
        id="sampleTemplate",
        # The id to the ready-made sample template. Fields and their ids are
        # specified when building out the template in the UI.
        cast_eid=PDF_TEMPLATE_ID,
    )

    return file_upload_nda, sample_template


def build_packet_payload(file_path: str, signer_name: str, signer_email: str):
    packet = CreateEtchPacket(
        # Indicate the packet is all ready to send to the
        # signers. An email will be sent to the first signer.
        is_draft=False,
        # Test packets will use development signatures and
        # not count toward your billed packets
        is_test=True,
        # Subject & body of the emails to signers
        name=f"Test Docs - {signer_name}",
        signature_email_subject="Custom email subject",
        signature_email_body="Custom please sign these documents....",
        # Merge all PDFs into one PDF before signing.
        # Signing users will get one PDF instead of all PDFs as separate files.
        # merge_pdfs=False,
    )

    nda_file, sample_template = get_file_payloads(file_path)
    signer1 = get_signer_data(signer_name, signer_email)

    # Add the file payloads we just built
    packet.add_file(nda_file)
    packet.add_file(sample_template)

    # Add signer(s) afterwards
    packet.add_signer(signer1)

    # The file payload key here needs to match the file payload id
    # given in `get_file_payloads`
    packet.add_file_payloads("sampleTemplate", dict(
        data=dict(
            name=signer_name,
            email=signer_email,
            ssn='456454567',
            ein='897654321',
            usAddress=dict(
                street1='123 Main St #234',
                city='San Francisco',
                state='CA',
                zip='94106',
                country='US',
            ),
        )
    ))

    packet.add_file_payloads("fileUploadNDA", dict(
        font_size=8,
        text_color="#0000CC",
        data=dict(
            # The IDs here match the fields we created in the
            # files property above
            recipientName=signer_name,
            recipientSignatureName=signer_name,
            recipientEmail=signer_email,
        )
    ))

    return packet


def create_etch_packet():
    # Signer information
    signer_name = "Testy Signer"
    signer_email = None
    has_error = None

    try:
        signer_email = sys.argv[1]
    except IndexError:
        has_error = True

    if has_error or not signer_email:
        print("Please provide a signer email")
        print(f"Usage: python {sys.argv[0]} your.real.email@example.com")
        return

    anvil = Anvil(api_key=API_KEY)
    nda_file = TEST_PDF_PATH

    payload = build_packet_payload(nda_file, signer_name, signer_email)

    print("Creating Etch e-sign packet...")
    res = anvil.create_etch_packet(payload=payload)

    if res.get("errors"):
        print("There were errors!")
        print(res["errors"])
        return

    packet_details = res["data"]["createEtchPacket"]
    print("Visit the new packet on your dashboard:", packet_details.get("detailsURL"))
    print(packet_details)


if __name__ == "__main__":
    create_etch_packet()
