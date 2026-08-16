from datetime import timedelta
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.metrics.pairwise import cosine_similarity

class BreakingNewsService:
    def __init__(self):
        self.similarity_threshold = 0.35

    def detect_breaking_news(self, articles: list) -> list:
        valid_articles = [a for a in articles if a.published_at is not None and a.title]
        sorted_articles = sorted(valid_articles, key=lambda x: x.published_at)

        if len(sorted_articles) < 4:
            return []

        # Vectorize article titles using TF-IDF
        titles = [a.title for a in sorted_articles]
        vectorizer = TfidfVectorizer(stop_words='english')
        tfidf_matrix = vectorizer.fit_transform(titles)

        clusters = []
        visited_indices = set()
        n = len(sorted_articles)

        for i in range(n):
            if i in visited_indices:
                continue

            base_article = sorted_articles[i]
            window_end = base_article.published_at + timedelta(minutes=60)

            # Gather candidate articles in the 60-minute time window
            candidate_indices = [
                j for j in range(i, n)
                if j not in visited_indices and sorted_articles[j].published_at < window_end
            ]

            if len(candidate_indices) < 4:
                continue

            # Calculate textual similarity between the base article and window candidates
            base_vector = tfidf_matrix[i]
            candidate_vectors = tfidf_matrix[candidate_indices]
            similarities = cosine_similarity(base_vector, candidate_vectors).flatten()

            # 4. Filter candidates that match the topic threshold
            topic_cluster_indices = [
                candidate_indices[idx]
                for idx, sim in enumerate(similarities)
                if sim >= self.similarity_threshold
            ]

            # Validate total matched articles and source diversity
            if len(topic_cluster_indices) >= 4:
                cluster_articles = [sorted_articles[idx] for idx in topic_cluster_indices]
                unique_sources = {a.source.name if a.source else "Unknown" for a in cluster_articles}

                if len(unique_sources) >= 2:
                    clusters.append({
                        "timestamp": base_article.published_at.isoformat(),
                        "uniqueSourcesCount": len(unique_sources),
                        "sampleTitle": base_article.title,
                        "articles": [
                            {
                                "id": a.id,
                                "title": a.title,
                                "url": getattr(a, 'url', None),
                                "published_at": a.published_at.isoformat()
                            }
                            for a in cluster_articles
                        ]
                    })
                    # Mark articles as processed to prevent overlap
                    visited_indices.update(topic_cluster_indices)

        return clusters