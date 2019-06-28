package com.xinyan.trust.propessor;

import com.xinyan.trust.pipeline.SavePipeline;
import com.xinyan.trust.util.RegexUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

@Component
public class ZJtrustPropessor implements PageProcessor {

    private String value;

    private String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ZJtrustPropessor() {
        this.value = "https://www.zjtrust.com.cn/cn/page/115.html";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private Site site = Site.me().setSleepTime(1000).setRetryTimes(30).setCharset("utf-8").setTimeOut(300000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

    @Override
    public void process(Page page) {
        Html html = page.getHtml();
        //1.获取列表页
        if (page.getUrl().toString().contains(value)) {
            //Selectable selectable = html.$("table.t1/tbody");
            Document document = html.getDocument();
            Elements elements = document.select("table.t1 >tbody").select("a");
            for (Element element : elements) {
                String onclick = element.attr("onclick");
                List<String> list = RegexUtil.getMatches("'(.*?)'", onclick);
                if (list.size() == 2) {
                    page.addTargetRequest("https://www.zjtrust.com.cn/cn/page/" + list.get(0) + "/" + list.get(1) + ".html");
                }
            }
            //下一页
            Element first = document.select("ul>li.next").first().select("a").first();
            String href = first.attr("href");
            String number = RegexUtil.getValue("\\((\\d+)\\)", href, 1);
            if (!StringUtils.isEmpty(number)) {
                page.addTargetRequest(this.value+"?pageIndex=" + number);
            }
        } else if (page.getUrl().toString().contains("https://www.zjtrust.com.cn/cn/page/")) {
            //详情页
            Document document = html.getDocument();
            String text = document.select("div.font22").text();
            page.putField("title", text);
            List<String> imageUrl = new ArrayList<>();
            Element first = document.select("div.contenBox>div>div>div").first();
            Elements select = first.select("p>img");
            if (select!=null && select.size()>1) {
                for (Element element : first.select("p")) {
                    String src = element.select("img").attr("src");
                    if (!StringUtils.isEmpty(src)) {
                        imageUrl.add("https://www.zjtrust.com.cn" + src);
                    }
                }
                if (!imageUrl.isEmpty()) {
                    page.putField("imageUrl", imageUrl);
                }
            } else {
                //html清空标签 一行一行输出。
                String clean = Jsoup.clean(first.html(), "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
                page.putField("contenBox",clean);
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        //热销中 114
        //存续中
        long l = System.currentTimeMillis();
        SavePipeline savePipeline = new SavePipeline();
        //https://www.zjtrust.com.cn/cn/page/115.html https://www.zjtrust.com.cn/cn/page/115.html?pageIndex=7
        Spider thread = Spider.create(new ZJtrustPropessor()).addUrl("https://www.zjtrust.com.cn/cn/page/115.html").addPipeline(savePipeline).thread(1);
        thread.run();
        System.out.println("over......." + (System.currentTimeMillis() - l) / 1000);
        //已清算 https://www.zjtrust.com.cn/cn/page/116.html

    }
}
