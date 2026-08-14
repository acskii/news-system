from datetime import timedelta

class BreakingNewsService:
    def detect_breaking_news(self, articles: list) -> list:
        valid_articles = [a for a in articles if a.published_at is not None]
        sorted_articles = sorted(valid_articles, key=lambda x: x.published_at)

        clusters = []
        i = 0
        n = len(sorted_articles)

        while i < n:
            base = sorted_articles[i]
            window_end = base.published_at + timedelta(minutes=60)
            window = []

            j = i
            while j < n and sorted_articles[j].published_at < window_end:
                window.append(sorted_articles[j])
                j += 1

            if len(window) >= 4:
                unique_sources = {a.source.name if a.source else "Unknown" for a in window}
                if len(unique_sources) >= 2:
                    clusters.append({
                        "timestamp": base.published_at.isoformat(),
                        "uniqueSourcesCount": len(unique_sources),
                        "sampleTitle": base.title,
                        "articles": [
                            {
                                "id": a.id,
                                "title": a.title,
                                "url": a.url,
                                "published_at": a.published_at.isoformat()
                            }
                            for a in window
                        ]
                    })
                    i = j - 1 # Jump past current cluster
            i += 1

        return clusters