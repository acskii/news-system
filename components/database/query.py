# Interactions with database

from datetime import timedelta, datetime
from sqlalchemy import select, func
from components.database.connection import db_session
from components.database.models import Article, DailyAnalytic, Source
from components.database.log import get_logger

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

    # Get all sources #
    def get_sources() -> list:
        return db_session.execute(
            select(Source).order_by(Source.name)
        ).scalars().all()

    # Get article by ID #
    def get_article(id: int):
        return db_session.execute(
            select(Article).where(Article.id == id)
        ).scalar_one_or_none()

    # Get paginated articles based on conditions #
    def get_articles(
            page: int = 0, 
            page_size: int = 8, 
            start_date: datetime = None, 
            end_date: datetime = None, 
            source_id: int = None, 
            keyword: str = None
        ) -> dict:
        stmt = select(Article).join(Article.source)

        if source_id:  stmt = stmt.where(Article.src == source_id)
        if start_date: stmt = stmt.where(Article.published_at >= start_date)
        if end_date:   stmt = stmt.where(Article.published_at < end_date)    
        if keyword:    stmt = stmt.where(Article.title.ilike(f'%{keyword}%'))

        # Order descendingly
        stmt = stmt.order_by(Article.published_at.desc())

        # Pagination
        total_items = db_session.execute(select(func.count()).select_from(stmt.subquery())).scalar()
        total_pages = (total_items + page_size - 1) // page_size if total_items > 0 else 0
        offset = (page - 1) * page_size
        stmt = stmt.offset(offset).limit(page_size)

        return {
            "articles": db_session.execute(stmt).scalars().all(),
            "pages": total_pages,
            "items": total_items
        }

    # Get today's articles #
    def get_today_articles() -> list:
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
    def get_week_articles() -> list:
        now = datetime.now()

        return db_session.execute(
            select(Article).where(
                Article.published_at.between(
                    now - timedelta(days=9),
                    now - timedelta(days=2),
                )
            )
        ).scalars().all()

    # Get all analytics #
    def get_analytics() -> list:
        return db_session.execute(
            select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc())
        ).scalars().all()
    
    # Get latest analytic #
    def get_today_analytic() -> DailyAnalytic:
        return db_session.execute(
            select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc()).limit(1)
        ).scalars().one_or_none()