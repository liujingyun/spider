package com.xinyan.trust.util;

import org.springframework.util.StringUtils;


public enum Status {
    START("1","开始爬取"),
    ING("2","正在爬取"),
    END("3","爬取完成"),
    FAILED("4","失败")
    ;

    private String code;
    private String desc;

    Status(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static String getValue(String code){
        if (StringUtils.isEmpty(code)) {
            return null;
        } else {
            Status[] values = values();
            Status[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                Status s = var2[var4];
                if (s.getCode().equalsIgnoreCase(code)) {
                    return s.getDesc();
                }
            }

            return null;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
