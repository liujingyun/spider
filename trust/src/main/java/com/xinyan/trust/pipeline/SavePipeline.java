package com.xinyan.trust.pipeline;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.xinyan.trust.util.OCRUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

@Component
public class SavePipeline implements Pipeline {
    private HSSFWorkbook wb;
    private Sheet sheet;

    public SavePipeline() {
        this.wb = new HSSFWorkbook();
        this.sheet = wb.createSheet("紫金信托");
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        Map<String, Object> all = resultItems.getAll();
        if (!all.isEmpty()) {
            if (all.containsKey("title")) {
                String url = resultItems.getRequest().getUrl();
                Row row = this.sheet.createRow(this.sheet.getLastRowNum() + 1);
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(url);
                String title = (String) all.get("title");
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(title);
                if (all.containsKey("imageUrl")) {
                    //调用百度OCR识别
                    List<String> imageUrl = (List<String>) all.get("imageUrl");

                    StringBuilder words = new StringBuilder();

                    Cell cell3 = row.createCell(3);
                    String images = "";
                    for (String str : imageUrl) {
                       // String text = OCRUtil.getPicText(str, 0);
                        String text = OCRUtil.getAccurateText(str);
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
                        System.out.println(words.toString());
                    }
                    cell3.setCellValue(images);
                    // 第六行
                    Cell cell = row.createCell(4);
                    cell.setCellValue(words.toString());
                } else {
                    //直接文档
                    String contenBox = (String)all.get("contenBox");
                    if(!StringUtils.isEmpty(contenBox)){
                        Cell cell = row.createCell(4);
                        cell.setCellValue(contenBox);
                    }
                }
            }
        }
    }

    public void downloadExcel(String url) {
        try {
            File file = new File(url);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            this.wb.write(fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
