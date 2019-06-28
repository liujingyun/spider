package com.xinyan.trust.repository;

import com.xinyan.trust.entity.ExcelBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExcelRepository extends MongoRepository<ExcelBean,String> {
     ExcelBean findExcelBeanByToken(String token);
}
