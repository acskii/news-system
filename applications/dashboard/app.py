import logging
from routes.today import today_route
from datetime import datetime, timedelta
from flask import Flask, render_template, request, abort, url_for
from sqlalchemy import select, func, or_
from database.connection import db_session
from models import Article, Source, DailyAnalytic

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key-here'  # change in production

app.register_blueprint(today_route, url_prefix="/today")

# Teardown: remove the session after each request
@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()

def sentiment_label(score):
    """Convert compound score to label and color."""
    if score is None:
        return ("N/A", "secondary")
    if score > 0.05:
        return ("Positive", "success")
    elif score < -0.05:
        return ("Negative", "danger")
    else:
        return ("Neutral", "secondary")

# ---------- Routes ----------
@app.route('/')
def dashboard():
    # Fetch latest analytic
    latest_analytic = db_session.execute(
        select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc()).limit(1)
    ).scalar_one_or_none()

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
    per_page = 8  # matches Spring's PAGE_SIZE

    # Convert date strings to datetime objects (naive, but we'll compare with timezone-aware)
    start_dt = None
    end_dt = None
    if start_date:
        try:
            start_dt = datetime.strptime(start_date, '%Y-%m-%d')
        except ValueError:
            pass
    if end_date:
        try:
            # add one day to include entire end date
            end_dt = datetime.strptime(end_date, '%Y-%m-%d') + timedelta(days=1)
        except ValueError:
            pass

    # 2. Fetch the latest daily analytic
    latest_analytic = db_session.execute(
        select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc()).limit(1)
    ).scalar_one_or_none()

    trending_topics = []
    breaking_news = []
    overall_sentiment = 0.0
    total_articles_today = 0

    if latest_analytic:
        trending_topics = latest_analytic.trending_keywords.get('trending_topics', [])
        breaking_news = latest_analytic.breaking_news.get('breaking_news', [])
        overall_sentiment = latest_analytic.overall_sentiment or 0.0
        total_articles_today = latest_analytic.total_articles or 0

    # 3. Build article query with filters
    stmt = select(Article).join(Article.source)  # eager join for source

    # Apply filters
    if source_id:
        stmt = stmt.where(Article.src == source_id)

    if start_dt:
        stmt = stmt.where(Article.published_at >= start_dt)
    if end_dt:
        stmt = stmt.where(Article.published_at < end_dt)

    if keyword:
        # Search in title or description (case-insensitive)
        stmt = stmt.where(
            or_(
                Article.title.ilike(f'%{keyword}%'),
                Article.description.ilike(f'%{keyword}%')
            )
        )

    # Order by published date descending
    stmt = stmt.order_by(Article.published_at.desc())

    # Pagination
    total_items = db_session.execute(select(func.count()).select_from(stmt.subquery())).scalar()
    total_pages = (total_items + per_page - 1) // per_page if total_items > 0 else 0
    offset = (page - 1) * per_page
    stmt = stmt.offset(offset).limit(per_page)
    articles = db_session.execute(stmt).scalars().all()

    # 4. Get all sources for the filter dropdown
    sources = db_session.execute(select(Source).order_by(Source.name)).scalars().all()

    # Prepare trending keywords (as simple list)
    trending_keywords = [t.get('keyword') for t in trending_topics if t.get('keyword')]

    # For each article, compute sentiment label
    for article in articles:
        article.sentiment_label, article.sentiment_color = sentiment_label(article.sentiment)

    # Alerts
    alerts = []
    if overall_sentiment < -0.2:
        alerts.append(('danger', f'⚠️ Overall sentiment is very negative ({overall_sentiment:.2f}).'))
    if breaking_news:
        alerts.append(('info', f'📢 {len(breaking_news)} breaking news clusters detected.'))

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

@app.route('/article/<int:article_id>')
def article(article_id):
    stmt = select(Article).where(Article.id == article_id)
    article = db_session.execute(stmt).scalar_one_or_none()
    if not article: abort(404)
    article.sentiment_label, article.sentiment_color = sentiment_label(article.sentiment)

    from_param = request.args.get('from')
    
    if from_param == 'today':
        back_url = url_for('today.main')
        back_label = "Back to Today"
    else:
        back_url = url_for('dashboard') # Adjust to your dashboard route name
        back_label = "Back to Dashboard"

    return render_template(
        'article.html', 
        article=article, 
        back_label=back_label,
        back_url=back_url
    )

@app.route('/analytics')
def analytics():
    # Get all DailyAnalytic records, ordered by analysed_at desc
    stmt = select(DailyAnalytic).order_by(DailyAnalytic.analysed_at.desc())
    analytics = db_session.execute(stmt).scalars().all()

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

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)