package com.acskii.common.models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@Table( name = "daily_analytic")
@EntityListeners(AuditingEntityListener.class)
public class Analytic {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column( name = "total_articles", nullable = false )
    private int totalArticles;

    // Stores JSONB as string
    @JdbcTypeCode(SqlTypes.JSON)
    @Column( name = "trending_keywords", nullable = false )
    private String trendingKeywords;

    // Stores JSONB as string
    @JdbcTypeCode(SqlTypes.JSON)
    @Column( name = "breaking_news", nullable = false )
    private String breakingNews;

    /* Metadata DateTimes */
    @Column( name = "analysed_at", nullable = false )
    private Instant analysedAt;

    @CreatedDate
    @Column( name = "created_at", nullable = false )
    private Instant createdAt;

    /* NoArg Constructor */
    public Analytic() {}

    /* Getters & Setters */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getTotalArticles() { return totalArticles; }
    public void setTotalArticles(int totalArticles) { this.totalArticles = totalArticles; }
    public String getTrendingKeywords() { return trendingKeywords; }
    public void setTrendingKeywords(String trendingKeywords) { this.trendingKeywords = trendingKeywords; }
    public Instant getAnalysedAt() { return analysedAt; }
    public void setAnalysedAt(Instant analysedAt) { this.analysedAt = analysedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public String getBreakingNews() { return breakingNews; }
    public void setBreakingNews(String breakingNews) { this.breakingNews = breakingNews; }
}
