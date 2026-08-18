from applications.dashboard.utils.sentiment import sentiment_label
from components.database.query import DatabaseQuery
from flask import Blueprint, render_template, request, abort, url_for

article_route = Blueprint('article', __name__)

@article_route.route("/<int:id>")
def article(id: int):
    article = DatabaseQuery.get_article(id)
    if not article: abort(404)
    article.sentiment_label, article.sentiment_color = sentiment_label(article.sentiment)
    
    from_param = request.args.get('from')
    back_url, back_label = _get_return_url(from_param)
    
    return render_template(
        'article.html', 
        article=article, 
        back_label=back_label,
        back_url=back_url
    )

def _get_return_url(label: str) -> tuple:
    if label == 'today': return (url_for('today.main'), "Back to Today")
    return (url_for('dashboard.dashboard'), "Back to Dashboard")