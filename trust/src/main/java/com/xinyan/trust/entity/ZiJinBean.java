package com.xinyan.trust.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class ZiJinBean extends BaseBean{
    private String url;
    private String title;
    private String imageData;
}
