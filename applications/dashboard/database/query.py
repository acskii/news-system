# Interactions with database

from datetime import timedelta, datetime
from sqlalchemy import select
from database.connection import db_session
from models import Article, DailyAnalytic
from log import get_logger

logger = get_logger(__name__)

class DatabaseQuery:
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

    def get_today_analytic():
        return db_session.execute(
            select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc()).limit(1)
        ).scalars().one_or_none()