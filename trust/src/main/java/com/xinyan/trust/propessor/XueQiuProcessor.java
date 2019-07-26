package com.xinyan.trust.propessor;

import com.xinyan.trust.util.Tool_Html;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

/**
 * @ClassName XueQiuProcessor
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/23 17:18
 * @Version V1.0
 **/
public class XueQiuProcessor implements PageProcessor {
    /**
     *抓取配置
     */
    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        Tool_Html tool_html = new Tool_Html("","");
        Document document = html.getDocument();
        //
        Elements elements = tool_html.elements("", document, "-select div.main-content>div.para");
        for(Element element : elements){
            String number = tool_html.value("获取-编号",element,"-owntext","");
            String city = tool_html.value("获取-城市名",element,"-select a -text","");
            if(number.contains("* 1") || number.contains("简而言之") ||number.contains("全部保留") ){
                //第一个不要
                continue;
            }
            number = number.replace("* ","00").replaceAll("--","").trim();
            if(StringUtils.isBlank(city)){
                System.out.println(number);
            }else {
                System.out.println(city+"(\""+number+"\",\""+city+"\"),");
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new XueQiuProcessor()).addUrl("https://baike.baidu.com/item/%E5%9B%BD%E9%99%85%E7%94%B5%E8%AF%9D%E5%8C%BA%E5%8F%B7/445182?fr=aladdin").thread(1).run();
    }
}
