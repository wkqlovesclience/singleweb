package com.gome.stage.entity;

import java.util.Date;

public class Title {
    private Integer id;

    private String paths;

    private String title;

    //对应productId
    private String goodsId;

    private String goodsName;

    private String creator;

    private Date createTime;

    private Date updateTime;

    private String publicStat;

    private String checkStat;

    private String modifier;

    private String editStat;

    private String site;

    private String indexStat;

    private String skuId;

    private String sources;

    //上是数据库对应字段
    //下是新增对应业务字段

    private Integer newsCount;

    private String keywords;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getPublicStat() {
        return publicStat;
    }

    public void setPublicStat(String publicStat) {
        this.publicStat = publicStat;
    }

    public String getCheckStat() {
        return checkStat;
    }

    public void setCheckStat(String checkStat) {
        this.checkStat = checkStat;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public String getEditStat() {
        return editStat;
    }

    public void setEditStat(String editStat) {
        this.editStat = editStat;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getIndexStat() {
        return indexStat;
    }

    public void setIndexStat(String indexStat) {
        this.indexStat = indexStat;
    }

    public String getSkuId() {
        return skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public Integer getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(Integer newsCount) {
        this.newsCount = newsCount;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    @Override
    public String toString() {
        return "Title{" +
                "id=" + id +
                ", paths='" + paths + '\'' +
                ", title='" + title + '\'' +
                ", goodsId='" + goodsId + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", publicStat='" + publicStat + '\'' +
                ", checkStat='" + checkStat + '\'' +
                ", modifier='" + modifier + '\'' +
                ", editStat='" + editStat + '\'' +
                ", site='" + site + '\'' +
                ", indexStat='" + indexStat + '\'' +
                ", skuId='" + skuId + '\'' +
                ", sources='" + sources + '\'' +
                ", newsCount=" + newsCount +
                ", keywords='" + keywords + '\'' +
                '}';
    }
}