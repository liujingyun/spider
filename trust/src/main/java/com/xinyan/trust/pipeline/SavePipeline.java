package com.xinyan.trust.pipeline;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xinyan.trust.entity.ZiJinBean;
import com.xinyan.trust.repository.ZiJinRepository;
import com.xinyan.trust.util.OCRUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;
import java.util.Map;

@Component
public class SavePipeline implements Pipeline {
    @Autowired
    private ZiJinRepository ziJinRepository;
    public SavePipeline() {
//        this.wb = new HSSFWorkbook();
//        this.sheet = wb.createSheet("紫金信托");
    }

    private List<ZiJinBean> ziJinBeans ;

    public List<ZiJinBean> getZiJinBeans() {
        return ziJinBeans;
    }

    public void setZiJinBeans(List<ZiJinBean> ziJinBeans) {
        this.ziJinBeans = ziJinBeans;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> all = resultItems.getAll();
        if (!all.isEmpty()) {
            if (all.containsKey("title")) {
                ZiJinBean ziJinBean = new ZiJinBean();
                String url = resultItems.getRequest().getUrl();
                ziJinBean.setUrl(url);
                String title = (String) all.get("title");
                ziJinBean.setTitle(title);
                if (all.containsKey("imageUrl")) {
                    //调用百度OCR识别
                    List<String> imageUrl = (List<String>) all.get("imageUrl");
                    StringBuilder words = new StringBuilder();
                    String images = "";
                    for (String str : imageUrl) {
                        String text = OCRUtil.getBasicText(str);
//
                        JsonParser jsonparser = new JsonParser();
                        JsonObject parse = jsonparser.parse(text).getAsJsonObject();
                        if (parse == null || parse.get("words_result") == null) {
                            System.out.println(text);
                            continue;
                        }
                        images += str + "\n";
                        for (JsonElement jsonElement : parse.get("words_result").getAsJsonArray()) {
                            //一行一行读
                            words.append(jsonElement.getAsJsonObject().get("words").getAsString() + "\n");
                        }
                    }
                    ziJinBean.setImageData(images);
                    ziJinBean.setData(words.toString());
                    // 第六行
                } else {
                    //直接文档
                    String contenBox = (String) all.get("contenBox");
                    if (!StringUtils.isEmpty(contenBox)) {
                        ziJinBean.setData(contenBox);
                    }
                }
                this.ziJinRepository.save(ziJinBean);
                this.ziJinBeans.add(ziJinBean);
            }
        }
    }
}
