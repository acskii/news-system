package models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table( name = "articles" )
public class Article {

    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Long id;

    @ManyToOne
    @JoinColumn( name = "src" )
    @JsonBackReference
    private Source src;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "author", nullable = false )
    private String author;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "title", nullable = false )
    private String title;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "url", nullable = false )
    private String url;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "description", nullable = false )
    private String description;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "content", nullable = false )
    private String content;

    /* Meta DateTimes */
    @CreatedDate
    @Column( name = "created_at", nullable = false )
    private Instant createdAt;

    @LastModifiedDate
    @Column( name = "updated_at", nullable = false )
    private Instant updatedAt;

    @Column( name = "published_at", nullable = false )
    private Instant publishedAt;

    /* NoArg Constructor */
    public Article() {}

    /* Getters & Setters */
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }
    public Source getSrc() { return src; }
    public void setSrc(Source src) { this.src = src; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
