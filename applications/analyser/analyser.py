from datetime import datetime, timedelta, timezone
from database import db_session
from sqlalchemy import select, func
from models import Article, DailyAnalytic
from services.sentiment import SentimentService
from services.trend import TrendService
from services.breaking_news import BreakingNewsService

class AnalysisProcessor:
    def __init__(self):
        self.sentiment_service = SentimentService()
        self.trend_service = TrendService(self.sentiment_service)
        self.breaking_news_service = BreakingNewsService()

    def process_day(self) -> dict:
        now = datetime.now(timezone.utc)
        twenty_four_hours_ago = now - timedelta(days=2)
        eight_days_ago = now - timedelta(days=8)

        # Fetch articles
        stmt_today = select(Article).where(
            func.timezone('UTC', Article.published_at).between(twenty_four_hours_ago, now)
        )
        today_articles = db_session.execute(stmt_today).scalars().all()

        stmt_historical = select(Article).where(
            func.timezone('UTC', Article.published_at).between(eight_days_ago, twenty_four_hours_ago)
        )
        historical_articles = db_session.execute(stmt_historical).scalars().all()

        # Compute analytics
        for a in today_articles:
            # Get sentiment for each article
            a.sentiment = self.sentiment_service.analyse_text(f"{a.title or ''} {a.description or ''}")
            db_session.add(a)

        trending = self.trend_service.compute_spike_trends(today_articles, historical_articles)
        breaking = self.breaking_news_service.detect_breaking_news(today_articles)
        overall_sentiment = self.sentiment_service.compute_overall_sentiment(today_articles)

        # Build JSON records matching exact frontend contract
        trending_payload = { "trending_topics": trending }
        breaking_payload = { "breaking_news": breaking }

        # Save to database
        analytic = DailyAnalytic(
            total_articles=len(today_articles),
            trending_keywords=trending_payload,
            breaking_news=breaking_payload,
            overall_sentiment=overall_sentiment,
            analysed_at=now,
            created_at=now
        )

        db_session.add(analytic)
        db_session.commit()

        return {
            "id": analytic.id,
            "status": "SUCCESS",
            "processedArticles": len(today_articles),
            "trendingTopicsCount": len(trending),
            "breakingNewsClustersCount": len(breaking)
        }