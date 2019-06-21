package com.xinyan.trust.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ZiJinBean {
    private String url;
    private String title;
    private String imageData;
    private String date;
}
