package com.xinyan.trust;

import com.xinyan.trust.controller.ZiJinController;
import com.xinyan.trust.service.PublisherService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @ClassName ControllerTest
 * @Description
 * @Author jingyun_liu
 * @Date 2019/9/9 10:19
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ControllerTest {

    @Autowired
    private PublisherService publisherService;

    @Test
    public void test(){
        for (int i = 0; i<100;i++){
            publisherService.sendMessage("test"+i);
        }
    }
}
