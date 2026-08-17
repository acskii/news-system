from datetime import datetime
from components.database.models import DailyAnalytic
from services.sentiment import SentimentService
from services.trend import TrendService
from services.breaking_news import BreakingNewsService
from components.database.query import DatabaseQuery

class AnalysisProcessor:
    def __init__(self):
        self.sentiment_service = SentimentService()
        self.trend_service = TrendService(self.sentiment_service)
        self.breaking_news_service = BreakingNewsService()

    def process_day(self) -> dict:
        # Get start of analysis
        now = datetime.now()

        # Fetch articles
        today_articles = DatabaseQuery.get_today_articles()
        week_articles = DatabaseQuery.get_week_articles()

        # Add sentiment for all articles
        for a in today_articles:
            a.sentiment = self.sentiment_service.analyse_text(f"{a.title or ''} {a.description or ''}")
            DatabaseQuery.update_article(a)

        trending = self.trend_service.compute_spike_trends(today_articles, week_articles)
        breaking = self.breaking_news_service.detect_breaking_news(today_articles)
        overall_sentiment = self.sentiment_service.compute_overall_sentiment(today_articles)

        # Create analysis record
        analytic = DailyAnalytic(
            total_articles=len(today_articles),

            trending_keywords={ 
                "trending_topics": trending 
            },

            breaking_news={ 
                "breaking_news": breaking 
            },

            overall_sentiment=overall_sentiment,
            analysed_at=now,
            created_at=now
        )

        # Save to database
        DatabaseQuery.add_analytic(analytic)

        # TODO: Create a more appropriate return result that would be used in the calling application (collector)
        return {
            "id": analytic.id,
            "status": "SUCCESS",
            "processedArticles": len(today_articles),
            "trendingTopicsCount": len(trending),
            "breakingNewsClustersCount": len(breaking)
        }