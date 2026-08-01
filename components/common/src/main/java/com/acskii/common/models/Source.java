package com.acskii.common.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table( name = "sources" )
public class Source {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY )
    private Integer id;

    @OneToMany( mappedBy = "src", cascade = CascadeType.ALL )
    @JsonManagedReference
    private List<Article> articles = new ArrayList<>();

    @Column( name = "name", nullable = false )
    private String name;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "description", nullable = false )
    private String description;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column( name = "url", nullable = false )
    private String url;

    /* NoArgs Constructor */
    public Source() {}

    /* Getters & Setters */
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public List<Article> getArticles() { return articles; }
    public void setArticles(List<Article> articles) { this.articles = articles; }
    public String getName() { return name; }
    public void setName(String name) {this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
