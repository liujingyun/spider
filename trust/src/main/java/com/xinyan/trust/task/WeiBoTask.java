package com.xinyan.trust.task;

import com.xinyan.trust.propessor.WeiBoHotProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import us.codecraft.webmagic.Spider;

/**
 * @ClassName WeiBoTask
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/1 17:55
 * @Version V1.0
 **/
@Configuration
@EnableScheduling
public class WeiBoTask {
    @Autowired
    private WeiBoHotProcessor processor;

    @Scheduled(fixedRate = 1000*60*1)
    public void getWeiBoHot(){
        System.out.println("微博热点自动化爬取。。。");
        Spider.create(processor).addUrl("https://s.weibo.com/top/summary?cate=realtimehot").thread(1).start();

    }
}
