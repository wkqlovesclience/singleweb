package com.gome.stage.entity;

import java.util.Date;

public class TitleIndex {
    private Integer id;

    private Integer titleId;

    private String pinyin;

    private String cindex;

    private Integer source;

    private Integer priority;

    private Integer status;

    private String operator;

    private Date updatetime;

    private String site;

    private String titleName;

    private String titlePath;

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

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getCindex() {
        return cindex;
    }

    public void setCindex(String cindex) {
        this.cindex = cindex;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTitlePath() {
        return titlePath;
    }

    public void setTitlePath(String titlePath) {
        this.titlePath = titlePath;
    }

    @Override
    public String toString() {
        return "TitleIndex{" +
                "id=" + id +
                ", titleId=" + titleId +
                ", pinyin='" + pinyin + '\'' +
                ", cindex='" + cindex + '\'' +
                ", source=" + source +
                ", priority=" + priority +
                ", status=" + status +
                ", operator='" + operator + '\'' +
                ", updatetime=" + updatetime +
                ", site='" + site + '\'' +
                ", titleName='" + titleName + '\'' +
                ", titlePath='" + titlePath + '\'' +
                '}';
    }
}