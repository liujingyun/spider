package com.xinyan.trust.entity;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * @ClassNameWeiBoHotAll
 * @Description
 * @Author jingyun_liu
 * @Date2019/7/1 16:41
 * @Version V1.0
 **/
@Data
@Document("weibo")
public class WeiBoHotAll {
    @Id
    private ObjectId id;
    private List<WeiBo> weiBoList;
    private Date createTime;
}
