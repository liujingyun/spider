package com.xinyan.trust.task;

import com.xinyan.trust.propessor.WeiBoHotProcessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;

/**
 * @ClassName WeiBoTask
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/1 17:55
 * @Version V1.0
 **/
@Component
public class WeiBoTask {

    private WeiBoHotProcessor processor;
    @Scheduled(cron = "* 0/5 * * * ?")
    public void getWeiBoHot(){
        Spider.create(processor)
                .addUrl("https://www.zjtrust.com.cn/cn/page/115.html")
                .thread(3).run();
    }
}
