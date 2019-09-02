package com.xinyan.trust.propessor;

import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @ClassName CrmProcessor
 * @Description
 * @Author jingyun_liu
 * @Date 2019/8/26 16:41
 * @Version V1.0
 **/
@Component
public class CrmProcessor implements PageProcessor {
    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new TestProcessor()).addUrl("http://prd-crm.wejoydata.com/login").thread(1).run();
    }
}
