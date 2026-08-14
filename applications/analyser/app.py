import logging
from flask import Flask, jsonify
from database import db_session
from analyser import AnalysisProcessor

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger(__name__)

app = Flask(__name__)
processor = AnalysisProcessor()

@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()

@app.route('/process', methods=['GET'])
def run_daily_analysis():
    try:
        logger.info("Received request to trigger daily analysis pipeline...")
        result = processor.process_day()
        logger.info(f"Analysis completed successfully. Analytic ID: {result['id']}")
        return jsonify(result), 200
    except Exception as e:
        logger.exception("Error occurred during daily analysis processing")
        db_session.rollback()
        return jsonify({"status": "ERROR", "message": str(e)}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8010, debug=False)