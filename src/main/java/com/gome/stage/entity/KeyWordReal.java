package com.gome.stage.entity;

public class KeyWordReal {
    private Integer id;

    private Integer titleId;

    private Integer keywords;

    private Integer articleId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTitleId() {
        return titleId;
    }

    public void setTitleId(Integer titleId) {
        this.titleId = titleId;
    }

    public Integer getKeywords() {
        return keywords;
    }

    public void setKeywords(Integer keywords) {
        this.keywords = keywords;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    @Override
    public String toString() {
        return "KeyWordReal{" +
                "id=" + id +
                ", titleId=" + titleId +
                ", keywords=" + keywords +
                ", articleId=" + articleId +
                '}';
    }
}