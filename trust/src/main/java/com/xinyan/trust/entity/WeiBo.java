package com.xinyan.trust.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassNameWeiBo
 * @Description
 * @Author jingyun_liu
 * @Date2019/7/1 15:24
 * @Version V1.0
 **/
@Data
public class WeiBo {
    private String key;
    private String flag;
    private Long hotNum;
    private String uri;
    private Integer rank;
}
