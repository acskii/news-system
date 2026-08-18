from flask import Blueprint, render_template, request
from components.database.query import DatabaseQuery
from utils.sentiment import sentiment_label
from datetime import datetime, timedelta

dashboard_route = Blueprint('dashboard', __name__)

@dashboard_route.route("")
def dashboard():
    latest_analytic = DatabaseQuery.get_today_analytic()
    
    trending_topics = []
    breaking_news = []
    overall_sentiment = 0.0
    total_articles_today = 0
    
    if latest_analytic:
        trending_topics = latest_analytic.trending_keywords.get('trending_topics', [])
        breaking_news = latest_analytic.breaking_news.get('breaking_news', [])
        overall_sentiment = latest_analytic.overall_sentiment or 0.0
        total_articles_today = latest_analytic.total_articles or 0
    
    # Get query parameters for filtering
    source_id = request.args.get('source_id', type=int)
    start_date = request.args.get('start_date')
    end_date = request.args.get('end_date')
    keyword = request.args.get('keyword', '').strip()
    page = request.args.get('page', 1, type=int)
    
    # Convert date strings to datetime objects
    start_dt = None
    if start_date:
        try:
            start_dt = datetime.strptime(start_date, '%Y-%m-%d')
        except ValueError:
            pass

    end_dt = None
    if end_date:
        try:
            # add one day to include entire end date
            end_dt = datetime.strptime(end_date, '%Y-%m-%d') + timedelta(days=1)
        except ValueError:
            pass
    
    articles, total_pages, total_items = map(DatabaseQuery.get_articles(
        page = page,
        start_date = start_dt,
        end_date = end_dt,
        source_id = source_id,
        keyword = keyword
    ).get, ('articles', 'pages', 'items'))
    sources = DatabaseQuery.get_sources()
    
    # Prepare trending keywords
    trending_keywords = [t.get('keyword') for t in trending_topics if t.get('keyword')]
    
    # For each article, process sentiment label
    for article in articles: article.sentiment_label, article.sentiment_color = sentiment_label(article.sentiment)
    
    # Alerts
    alerts = []
    if breaking_news: alerts.append(('info', f'📢 {len(breaking_news)} breaking news clusters detected.'))
    
    # 6. Render template
    return render_template(
        'index.html',
        articles=articles,
        sources=sources,
        trending_keywords=trending_keywords,
        breaking_news=breaking_news,
        overall_sentiment=overall_sentiment,
        total_articles_today=total_articles_today,
        alerts=alerts,
        # Pagination
        page=page,
        total_pages=total_pages,
        total_items=total_items,
        has_prev=page > 1,
        has_next=page < total_pages,
        selected_source_id=source_id,
        start_date=start_date,
        end_date=end_date,
        keyword=keyword
    )