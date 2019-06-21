package com.xinyan.trust.util;

import com.baidu.aip.ocr.AipOcr;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;


@Slf4j
public class OCRUtil {
    private static final String API_KEY2 = "iYmQITUw0RPhwTXMN3pheXDW";
    private static final String SECRET_KEY2 = "DnteVSRZ7v54daYSxPal37qOfbhbhYtE";
    private static final String APP_ID = "16577956";
    private static final String API_KEY = "kB0TpOLGtsALluzhbAH8f0G1";
    private static final String SECRET_KEY = "ZscGQRHp9BYF1t4x5Tjr3bmHbtVZUGkw";
    //16579906@iYmQITUw0RPhwTXMN3pheXDW@DnteVSRZ7v54daYSxPal37qOfbhbhYtE
    //16577956@kB0TpOLGtsALluzhbAH8f0G1@ZscGQRHp9BYF1t4x5Tjr3bmHbtVZUGkw
    //add("14584148@9kjTspzFehGYGRb4RK2iNTgr@iw0jSiBYeezUpYlP3cK9SLbxyxhzbWOI");
    //add("15764112@YKthQd4kY0nVgCzCheQAVWaZ@WpzBkpua7gsrdv7HICPmeYiM9rmq2Rpo");
    //add("15764153@27n4kOdKSgIvFz0WtrpX3T4O@yIS9M1bL9AtSva8iWq5BZv6sVvlECHdQ");
    private static List<AipOcr> ocrList = new ArrayList<>();
    private static AipOcr aipOcr = null;
    static {
        add("14584148@9kjTspzFehGYGRb4RK2iNTgr@iw0jSiBYeezUpYlP3cK9SLbxyxhzbWOI");
        add("15764112@YKthQd4kY0nVgCzCheQAVWaZ@WpzBkpua7gsrdv7HICPmeYiM9rmq2Rpo");
        add("15764153@27n4kOdKSgIvFz0WtrpX3T4O@yIS9M1bL9AtSva8iWq5BZv6sVvlECHdQ");
        add("16577956@kB0TpOLGtsALluzhbAH8f0G1@ZscGQRHp9BYF1t4x5Tjr3bmHbtVZUGkw");
        add("16579906@iYmQITUw0RPhwTXMN3pheXDW@DnteVSRZ7v54daYSxPal37qOfbhbhYtE");
        aipOcr = ocrList.get(0);
    }
    public static void add(String keys){
        String[] split = keys.split("@");
        AipOcr aipOcr = new AipOcr(split[0],split[1],split[2]);
        ocrList.add(aipOcr);
    }

    /**
     * 高精度
     * @param url
     * @return
     */
    public static String getAccurateText(String url){
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("detect_direction", "true");
            options.put("probability", "true");
            String contentType = "image/jpeg; charset=UTF-8";
            InputStream inputStream = HttpUtil.get(url, contentType, "UTF-8");
            byte[] bytes = inputToByte(inputStream);
            aipOcr.setConnectionTimeoutInMillis(2000);
            aipOcr.setSocketTimeoutInMillis(60000);
            JSONObject jsonObject = aipOcr.basicAccurateGeneral(bytes, options);
            //重试五次
            int i = 1;
            while (jsonObject.toString().contains("error_code")){
                if(i == 5){
                    break;
                }
                aipOcr = ocrList.get(new Random(4).nextInt());
                aipOcr.setConnectionTimeoutInMillis(2000);
                aipOcr.setSocketTimeoutInMillis(60000);
                jsonObject = aipOcr.basicAccurateGeneral(bytes, options);
                i++;
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getBasicText(String url){
        try {
            HashMap<String, String> options = new HashMap<>();
            options.put("detect_direction", "true");
            options.put("probability", "true");
            String contentType = "image/jpeg; charset=UTF-8";
            InputStream inputStream = HttpUtil.get(url, contentType, "UTF-8");
            byte[] bytes = inputToByte(inputStream);
            aipOcr.setConnectionTimeoutInMillis(2000);
            aipOcr.setSocketTimeoutInMillis(60000);
            JSONObject jsonObject = aipOcr.basicGeneral(bytes, options);
            //重试五次
            int i = 1;
            while (jsonObject.toString().contains("error_code")){
                if(i == 5){
                    break;
                }
                aipOcr = ocrList.get(new Random(4).nextInt());
                aipOcr.setConnectionTimeoutInMillis(2000);
                aipOcr.setSocketTimeoutInMillis(60000);
                jsonObject = aipOcr.basicGeneral(bytes, options);
                i++;
            }
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * 流转byte数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] inputToByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc;
        while ((rc = inputStream.read(buff, 0, 1024)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        return in2b;
    }



    public static void main(String[] args){
       // String url = "D:\\test111\\4b1075af-d8bb-4408-b74e-dca09facaef4.jpg";
//        String text = result(getAipClient(),url);
        String text = getAccurateText("https://www.zjtrust.com.cn/uploads/84c9469a1a6449c48a0f13256b3dc861.jpg");
        System.out.println(text);
    }
}
