package com.gome.stage.ENUM;

public enum RankTemplate {
    PC_INDEX("pc_index","vm_rank/pc_topic_index"),PC_LIST("pc_list","vm_rank/pc_topic_list"),PC_FACET("pc_facet","vm_rank/pc_topic_facet"),PC_BRAND("pc_brand","vm_rank/pc_topic_brand")
    ,WAP_INDEX("wap_index","vm_rank/wap_topic_index"),WAP_LIST("wap_index","vm_rank/wap_topic_list"),WAP_FILTER("wap_index","vm_rank/wap_topic_filter");
    //防止字段值被修改，增加的字段也统一final表示常量
    private final String key;
    private final String value;

    private RankTemplate(String key, String value){
        this.key = key;
        this.value = value;
    }
    //根据key获取枚举
    public static String getEnumValueByKey(String key){
        if(null == key){
            return null;
        }
        for(RankTemplate temp: RankTemplate.values()){
            if(temp.getKey().equals(key)){
                return temp.getValue();
            }
        }
        return null;
    }
    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
}
