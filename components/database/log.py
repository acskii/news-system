from logging import Logger, getLogger, INFO, StreamHandler, Formatter
import sys

def get_logger(name: str) -> Logger:
  logger = getLogger(name)

  if not logger.handlers:
    logger.setLevel(INFO)

    # Force output to stdout
    handler = StreamHandler(sys.stdout)
    formatter = Formatter(
        "[%(asctime)s] [%(name)s] (%(levelname)s) %(message)s",
        datefmt="%Y-%m-%d %H:%M:%S",
    )
    handler.setFormatter(formatter)
    logger.addHandler(handler)
    logger.propagate = False

  return logger