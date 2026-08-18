from flask import Blueprint, render_template, request, redirect, url_for, make_response
from components.database.query import DatabaseQuery
from utils.sentiment import sentiment_label
from datetime import datetime

today_route = Blueprint('today', __name__)

def get_read_article_ids():
    """Extract read article IDs set from cookie."""
    cookie_val = request.cookies.get('read_article_ids', '')
    if not cookie_val:
        return set()
    return set(int(x) for x in cookie_val.split(',') if x.isdigit())

@today_route.route("")
def main():
    articles = DatabaseQuery.get_today_articles()
    analytic = DatabaseQuery.get_today_analytic()

    if not analytic:
        return render_template(
            'today.html',
            unread_articles=[],
            read_articles=[],
            sources=[],
            trending_topics=[],
            unique_sources_count=0,
            selected_source_id=None,
            keyword='',
            collected_at=datetime.now().strftime("%d/%m/%Y")
        )

    source_id = request.args.get('source_id', type=int)
    keyword = request.args.get('keyword', '').strip()

    unique_sources = list({a.source for a in articles if a.source})
    unique_sources.sort(key=lambda s: s.name)
    unique_sources_count = len(unique_sources)

    raw_topics = analytic.trending_keywords.get('trending_topics', []) if analytic.trending_keywords else []
    trending_topics = [t for t in raw_topics if t.get('keyword')]

    filtered_articles = articles

    if source_id:
        filtered_articles = [
            a for a in filtered_articles 
            if (a.source and a.source.id == source_id)
        ]

    if keyword:
        kw_lower = keyword.lower()
        filtered_articles = [
            a for a in filtered_articles
            if (a.title and kw_lower in a.title.lower()) or 
               (a.description and kw_lower in a.description.lower())
        ]

    for a in filtered_articles:
        score = getattr(a, 'sentiment', None)
        a.sentiment_label, a.sentiment_color = sentiment_label(score)

    # Separate into unread and read articles based on cookie
    read_ids = get_read_article_ids()
    unread_articles = [a for a in filtered_articles if a.id not in read_ids]
    read_articles = [a for a in filtered_articles if a.id in read_ids]

    return render_template(
        'today.html',
        unread_articles=unread_articles,
        read_articles=read_articles,
        unique_sources=unique_sources,
        unique_sources_count=unique_sources_count,
        trending_topics=trending_topics,
        selected_source_id=source_id,
        keyword=keyword,
        collected_at=analytic.analysed_at.strftime("%d/%m/%Y")
    )

@today_route.route("/clear-history", methods=["POST"])
def clear_history():
    """Clear read history cookie and redirect back."""
    resp = make_response(redirect(url_for('today.main')))
    resp.delete_cookie('read_article_ids', path='/')
    return resp