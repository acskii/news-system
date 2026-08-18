from flask import Blueprint, render_template
from components.database.query import DatabaseQuery

analytics_route = Blueprint('analytics', __name__)

@analytics_route.route("")
def analytics():
    # Get all DailyAnalytic records, ordered by analysed_at desc
    analytics = DatabaseQuery.get_analytics()
    
    # Prepare data for charts (chronological order for line charts)
    chrono = list(reversed(analytics))  # oldest first
    dates = [a.analysed_at.strftime('%Y-%m-%d') for a in chrono]
    article_counts = [a.total_articles for a in chrono]
    sentiment_scores = [a.overall_sentiment for a in chrono]
    
    return render_template(
        'analytics.html',
        analytics=analytics,
        dates=dates,
        article_counts=article_counts,
        sentiment_scores=sentiment_scores
    )