# Interactions with database

from datetime import timedelta, datetime
from sqlalchemy import select
from components.database.connection import db_session
from components.database.models import Article, DailyAnalytic
from log import get_logger

logger = get_logger(__name__)

class DatabaseQuery:
    # Update article record #
    def update_article(article: Article):
        db_session.add(article)
        db_session.commit()
        logger.info(f"Update article of ID: {article.id}")

    # Add new analytic record #
    def add_analytic(analytic: DailyAnalytic):
        db_session.add(analytic)
        db_session.commit()
        logger.info(f"Add analytic for {analytic.analysed_at} of ID: {analytic.id}")

    # Get today's articles #
    def get_today_articles():
        now = datetime.now()

        return db_session.execute(
                select(Article).where(
                    Article.published_at.between(
                        now - timedelta(days=2),
                        now,
                    )
                )
            ).scalars().all()

    # Get a week history of articles from today #
    def get_week_articles():
        now = datetime.now()

        return db_session.execute(
            select(Article).where(
                Article.published_at.between(
                    now - timedelta(days=9),
                    now - timedelta(days=2),
                )
            )
        ).scalars().all()

    # Get latest analytic #
    def get_today_analytic():
        return db_session.execute(
            select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc()).limit(1)
        ).scalars().one_or_none()