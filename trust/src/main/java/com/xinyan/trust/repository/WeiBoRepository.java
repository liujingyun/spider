package com.xinyan.trust.repository;

import com.xinyan.trust.entity.ExcelBean;
import com.xinyan.trust.entity.WeiBo;
import com.xinyan.trust.entity.WeiBoHotAll;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @ClassNameWeiBoRepository
 * @Description
 * @Author jingyun_liu
 * @Date2019/7/1 16:26
 * @Version V1.0
 **/
@Repository
public interface WeiBoRepository extends MongoRepository<WeiBoHotAll,String> {

}
