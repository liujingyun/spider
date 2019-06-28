package com.xinyan.trust.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;


@Data
@Document
public class ExcelBean {
    @Id
    private ObjectId id;
    @Indexed(unique = true)
    private String token;
    private List<ZiJinBean> message;
    private Date updateTime;
}
