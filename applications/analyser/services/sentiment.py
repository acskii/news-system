from nltk.sentiment.vader import SentimentIntensityAnalyzer

class SentimentService:
    def __init__(self):
        self.analyser = SentimentIntensityAnalyzer()

    def analyse_text(self, text: str) -> float:
        """Returns compound sentiment score as a float (-1.0 to 1.0)."""
        if not text:
            return 0.0

        scores = self.analyser.polarity_scores(text)
        return round(scores['compound'], 3)

    def compute_overall_sentiment(self, articles: list) -> float:
        """Aggregates daily sentiment across all articles by processing title and description."""
        if not articles:
            return 0.0

        total_compound = 0.0
        for article in articles:
            content = f"{article.title or ''} {article.description or ''}"
            total_compound += self.analyse_text(content)

        return round(total_compound / len(articles), 3)