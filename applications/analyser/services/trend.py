from collections import Counter
import applications.analyser.nltk_data as nltk_data
from nltk.corpus import stopwords

class TrendService:
    def __init__(self, sentiment_service):
        self.stop_words = set(stopwords.words('english'))
        self.sentiment_service = sentiment_service

    def _extract_tokens(self, articles: list) -> list:
        tokens = []
        for article in articles:
            if not article.title:
                continue
            words = nltk_data.word_tokenize(article.title)
            # Filter non-alphanumeric tokens and stop words
            tokens.extend([w.lower() for w in words if w.isalnum() and w.lower() not in self.stop_words])
        return tokens

    def compute_spike_trends(self, today_articles: list, historical_articles: list) -> list:
        today_counts = Counter(self._extract_tokens(today_articles))
        hist_counts = Counter(self._extract_tokens(historical_articles))

        results = []
        for keyword, count_today in today_counts.items():
            if count_today < 2:
                continue

            count_7day = hist_counts.get(keyword, 0)
            daily_baseline = count_7day / 7.0
            spike_score = count_today / (daily_baseline + 1.0)

            if spike_score >= 1.8:
                matching_titles = [a.title for a in today_articles if a.title and keyword in a.title.lower()]
                sentiments = [self.sentiment_service.analyse_text(t) for t in matching_titles]
                avg_sentiment = round(sum(sentiments) / len(sentiments), 2) if sentiments else 0.0

                results.append({
                    "keyword": keyword,
                    "todayCount": count_today,
                    "spikeScore": round(spike_score, 2),
                    "avgSentiment": avg_sentiment
                })

        results.sort(key=lambda x: x["spikeScore"], reverse=True)
        return results