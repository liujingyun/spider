package com.xinyan.trust.propessor;

import com.xinyan.trust.pipeline.SavePipeline;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class WeiBoHotProcessor implements PageProcessor {
    /**
     *抓取配置
     */
    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        // 根据URL判断页面类型
        Html html = page.getHtml();
        List<Selectable> nodes =
                html.xpath("//div[@id=pl_top_realtimehot]/table/tbody/tr").nodes();
        for (Selectable node : nodes){
            node.xpath("//td[@class=td-02]/a/text()");
            node.xpath("//td[@class=td-02]/a/text()");
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //Spider.create(new WeiBoHotProcessor()).addUrl("https://s.weibo.com/top/summary?Refer=top_hot&topnav=1&wvr=6").addPipeline(new SavePipeline()).thread(5).run();
    }
}
