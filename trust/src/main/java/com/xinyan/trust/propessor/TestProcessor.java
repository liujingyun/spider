package com.xinyan.trust.propessor;

import com.xinyan.trust.util.Tool_Html;
import org.jsoup.nodes.Document;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.io.UnsupportedEncodingException;

/**
 * @ClassName TestProcessor 专门用来测试用的爬虫
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/26 15:02
 * @Version V1.0
 **/
public class TestProcessor implements PageProcessor {

    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("GBK").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");


    @Override
    public void process(Page page) {
        Document document = page.getHtml().getDocument();

        Tool_Html tool_html = new Tool_Html("", "");
        if (document.toString().contains("上海医保")) {


        }


    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new TestProcessor()).addUrl("http://202.96.245.182/xxcx/ddyd.jsp").thread(1).run();
    }
}
