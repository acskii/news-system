import os
from sqlalchemy import create_engine
from sqlalchemy.orm import scoped_session, sessionmaker, declarative_base

DATABASE_USER = os.getenv("DB_USER")
DATABASE_PASS = os.getenv("DB_PASS")

# TODO: Needs changing to accomodate connection string formatting alongside Spring applications 
DATABASE_URL = f"postgresql+psycopg://{DATABASE_USER}:{DATABASE_PASS}@localhost:5432/news"

engine = create_engine(DATABASE_URL)
db_session = scoped_session(sessionmaker(autocommit=False, autoflush=False, bind=engine))
Base = declarative_base()
Base.query = db_session.query_property()