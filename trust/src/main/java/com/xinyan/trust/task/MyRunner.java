package com.xinyan.trust.task;

import com.xinyan.trust.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @ClassName MyRunner
 * @Description
 * @Author jingyun_liu
 * @Date 2019/9/9 17:28
 * @Version V1.0
 **/
@Component
public class MyRunner implements CommandLineRunner {
    @Autowired
    private PublisherService publisherService;
    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i<100;i++){
            publisherService.sendMessage("test"+i);
        }
    }
}
