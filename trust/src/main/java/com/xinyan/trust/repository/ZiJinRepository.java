package com.xinyan.trust.repository;


import com.xinyan.trust.entity.ZiJinBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZiJinRepository extends MongoRepository<ZiJinBean, String> {

}
