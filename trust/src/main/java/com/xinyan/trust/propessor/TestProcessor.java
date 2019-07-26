package com.xinyan.trust.propessor;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @ClassName TestProcessor
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/26 15:02
 * @Version V1.0
 **/
public class TestProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {

    }

    @Override
    public Site getSite() {
        return site;
    }
}
