import logging
from routes.today import today_route
from routes.dashboard import dashboard_route
from routes.article import article_route
from routes.analytics import analytics_route
from components.database.connection import db_session
from flask import Flask

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

app = Flask(__name__)
app.config['SECRET_KEY'] = 'your-secret-key-here'  # change in production

app.register_blueprint(today_route, url_prefix="/today")
app.register_blueprint(dashboard_route, url_prefix="/")
app.register_blueprint(article_route, url_prefix="/article")
app.register_blueprint(analytics_route, url_prefix="/analytics")

# Teardown: remove the session after each request
@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)