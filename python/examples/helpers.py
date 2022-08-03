import os
from pathlib import Path


def get_output_file_path(file, output_filename):
    file_path = Path(file).parent.absolute()
    return os.path.join(file_path, '..', 'output', output_filename)


def get_test_pdf_path(curr_file):
    file_path = Path(curr_file).parent.absolute()
    return os.path.join(file_path, '..', '..', 'static', 'test-pdf-nda.pdf')
