from sqlalchemy import Column, Integer, BigInteger, Text, Float, DateTime, ForeignKey, func
from sqlalchemy.dialects.postgresql import JSONB
from sqlalchemy.orm import relationship
from database import Base


class Source(Base):
    __tablename__ = 'sources'

    id = Column(Integer, primary_key=True)
    name = Column(Text, nullable=False)
    description = Column(Text, nullable=False)
    url = Column(Text, nullable=False)

    # Relationship back to articles
    articles = relationship("Article", back_populates="source", cascade="all, delete")


class Article(Base):
    __tablename__ = 'articles'

    id = Column(BigInteger, primary_key=True)
    title = Column(Text, nullable=False)
    author = Column(Text, nullable=False)
    src = Column(Integer, ForeignKey('sources.id', ondelete='CASCADE'), nullable=False)
    description = Column(Text, nullable=False)
    content = Column(Text, nullable=False)
    url = Column(Text, nullable=False)
    published_at = Column(DateTime(timezone=True), nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), server_default=func.now(), onupdate=func.now())

    # Relationship to source
    source = relationship("Source", back_populates="articles")


class DailyAnalytic(Base):
    __tablename__ = 'daily_analytic'

    id = Column(BigInteger, primary_key=True)
    total_articles = Column(Integer, nullable=False)
    trending_keywords = Column(JSONB, nullable=False)
    breaking_news = Column(JSONB, nullable=False)
    overall_sentiment = Column(Float, nullable=False)
    analysed_at = Column(DateTime(timezone=True), nullable=False)
    created_at = Column(DateTime(timezone=True), nullable=False, server_default=func.now())