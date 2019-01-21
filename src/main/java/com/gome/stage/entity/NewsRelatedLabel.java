package com.gome.stage.entity;

/**
 * 用户文章相关标签列表页
 * @author wangzhibo-ds1
 *
 */
public class NewsRelatedLabel {
	private String id;
	private String labelId;
	private String labelsUrl; //数组
	private String articleId;//文章ID
	private String topic;
	private String keywords;
	private String remark;
	private String contents;
	private String public_time;
	private String site;
	private String picurl;
	private String labels;  //数组处理
	private String labelNames;//数组处理
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLabelId() {
		return labelId;
	}
	public void setLabelId(String labelId) {
		this.labelId = labelId;
	}
	public String getLabelsUrl() {
		return labelsUrl;
	}
	public void setLabelsUrl(String labelsUrl) {
		this.labelsUrl = labelsUrl;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	public String getPublic_time() {
		return public_time;
	}
	public void setPublic_time(String public_time) {
		this.public_time = public_time;
	}
	public String getSite() {
		return site;
	}
	public void setSite(String site) {
		this.site = site;
	}
	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	public String getLabelNames() {
		return labelNames;
	}
	public void setLabelNames(String labelNames) {
		this.labelNames = labelNames;
	}
	public String getArticleId() {
		return articleId;
	}
	public void setArticleId(String articleId) {
		this.articleId = articleId;
	}
	
}
