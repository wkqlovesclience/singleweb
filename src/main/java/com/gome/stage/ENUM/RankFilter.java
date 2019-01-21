package com.gome.stage.ENUM;

public enum RankFilter {
    HOT("hot","爆品"),NEW("new","新品"),BRAND("brand","品牌"),PRICE("price","价格"),OTHER("other","功能");
    //防止字段值被修改，增加的字段也统一final表示常量
    private final String key;
    private final String value;

    private RankFilter(String key,String value){
        this.key = key;
        this.value = value;
    }
    //根据key获取枚举
    public static String getEnumValueByKey(String key){
        if(null == key){
            return null;
        }
        for(RankFilter temp:RankFilter.values()){
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
