package com.xinyan.trust.service.impl;

import com.xinyan.trust.service.PublisherService;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @ClassName PublisherServiceImpl
 * @Description
 * @Author jingyun_liu
 * @Date 2019/9/9 17:44
 * @Version V1.0
 **/
@Service
@Slf4j
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public String sendMessage(String name) {
        try {
            redisTemplate.convertAndSend("TOPIC_USERNAME", name);
            return "消息发送成功了";

        } catch (Exception e) {
            e.printStackTrace();
            return "消息发送失败了";
        }
    }
}
