# Sets up any NLTK data files needed for normal functions in services

import nltk
from log import get_logger

logger = get_logger(__name__)

# Record class to store all data file information needed for confirmation and download #
class NLTKDataFile:
    def __init__(self, file_name: str, download_name: str):
        self.file_name = file_name
        self.download_name = download_name

# List of all data files needed for normal functions #
FILES = [
    NLTKDataFile('sentiment/vader_lexicon.zip', 'vader_lexicon'),
    NLTKDataFile('corpora/stopwords', 'stopwords'),
    NLTKDataFile('corpora/punkt_tab', 'punkt_tab'),
]


def setup_nltk():
    """Finds and/or downloads all NLTK data files needed for NLTK functions within analysis services"""
    for file in FILES:
        try:    
            nltk.data.find(file.file_name)
            logger.info(f"Found NLTK data file for {file.download_name}")
        except (LookupError, TypeError):
            nltk.download(file.download_name)
            logger.info(f"Downloaded NLTK data file for {file.download_name}")
    logger.info(f"Finished NLTK data setup")
    