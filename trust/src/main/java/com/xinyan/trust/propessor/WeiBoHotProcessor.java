package com.xinyan.trust.propessor;

import com.xinyan.trust.entity.WeiBo;
import com.xinyan.trust.entity.WeiBoHotAll;
import com.xinyan.trust.pipeline.SavePipeline;
import com.xinyan.trust.repository.WeiBoRepository;
import com.xinyan.trust.util.Tools;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class WeiBoHotProcessor implements PageProcessor {
    /**
     *抓取配置
     */
    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
    @Autowired
    private WeiBoRepository weiBoRepository;
    @Override
    public void process(Page page) {
        Tools tools = new Tools("新浪微博热点","");
        // 根据URL判断页面类型
        Html html = page.getHtml();
        List<Selectable> nodes =
                html.xpath("//div[@id=pl_top_realtimehot]/table/tbody/tr").nodes();
        WeiBoHotAll weiBoHotAll = new WeiBoHotAll();
        List<WeiBo> weiBos = new ArrayList<>();
        for (Selectable node : nodes){
            WeiBo weiBo = new WeiBo();
            String rank = node.xpath("//td[@class=td-01]/text()").get();
            weiBo.setRank(tools.to_integer(rank,null));
            String key = node.xpath("//td[@class=td-02]/a/text()").get();
            weiBo.setKey(key);
            String uri =  node.xpath("//td[@class=td-02]/a").links().get();
            weiBo.setUri(uri);
            String hotNum = node.xpath("//td[@class=td-02]/span/text()").get();
            weiBo.setHotNum(tools.to_Long(hotNum,null));
            String flag = node.xpath("//td[@class=td-03]/i/text()").get();
            weiBo.setFlag(flag);
            weiBos.add(weiBo);
        }
        weiBoHotAll.setWeiBoList(weiBos);
        weiBoHotAll.setCreateTime(new Date());
        weiBoRepository.save(weiBoHotAll);
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new WeiBoHotProcessor()).addUrl("https://s.weibo.com/top/summary?cate=realtimehot").thread(1).run();
    }
}
