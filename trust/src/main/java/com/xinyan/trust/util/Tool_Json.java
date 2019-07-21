package com.xinyan.trust.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Slf4j
public class Tool_Json {
    // 记录当前的 page 的 key
    private String PAGE = "";
    /**
     * AQL_SEPARATOR 特殊符号
     */
    private static String AQL_SEPARATOR = ".";
    private JsonParser jsonparser = new JsonParser();

    private String website = "";
    private String crawl_token = "";

    public Tool_Json(String website, String crawl_token) {
        this.website = website;
        this.crawl_token = crawl_token;
    }

    public static void main(String[] args) throws IOException, ParseException {
    }

    public JsonObject[] get_jsons_safety(Map<String, String[]> parameterMap, String page) {
        return getJsonObjects(parameterMap, page, false);
    }

    /**
     * 取完就remove掉parameterMap中的key
     *
     * @param parameterMap
     * @param page
     * @return
     */
    public JsonObject[] get_clean_jsons_safety(Map<String, String[]> parameterMap, String page) {
        return getJsonObjects(parameterMap, page, true);
    }

    public JsonObject[] getJsonObjects(Map<String, String[]> parameterMap, String page, boolean isRemove) {
        PAGE = page;
        String[] strings = parameterMap.get(page);
        if (strings == null || strings.length == 0) {
            log.debug("数据源".concat(page).concat("页面为空 "));
            return new JsonObject[0];
        }
        JsonObject[] json_objects = new JsonObject[strings.length];
        for (int i = 0; i < strings.length; i++) {
            json_objects[i] = parser_json(strings[i]);
        }

        if (isRemove) {
            parameterMap.remove(page);
        }
        return json_objects;
    }

    public JsonObject get_json_safety(Map<String, String[]> parameterMap, String page) {
        return getJsonObject(parameterMap,page,false);
    }

    public JsonObject get_clean_json_safety(Map<String, String[]> parameterMap, String page) {
        return getJsonObject(parameterMap,page,true);
    }

    public JsonObject getJsonObject(Map<String, String[]> parameterMap, String page,boolean isRemove) {
        String defaule_value = "{}";
        PAGE = page;
        String[] strings = parameterMap.get(page);
        if (strings != null && strings.length >= 1) {
            defaule_value = strings[0];
        } else {
            log.debug("数据源页面为空");
        }

        if (isRemove) {
            parameterMap.remove(page);
        }
        return parser_json(defaule_value);
    }

    /**
     * 解析 js 方法中的 json 数据
     *
     * @param value
     * @return
     */
    public JsonObject parser_json(String value) {
        JsonObject json_object = null;

        if (StringUtils.isEmpty(value) || value.equals("[]") || value.equals("timeout")) {
            log.debug("待解析json数据为空");
            return new JsonObject();
        }

        int start = value.indexOf("{");
        int end = value.lastIndexOf("}");

        if (start > -1 && end > -1) {
            try {
                json_object = jsonparser.parse(value.substring(start, end + 1)).getAsJsonObject();
            } catch (Exception e) {
                log.debug("字符串转json失败", e);
            }
        }

        if (json_object == null) {
            json_object = new JsonObject();
        }

        return json_object;
    }

    /**
     * 解析 js 方法中的 json 数据
     *
     * @param value
     * @return
     */
    public JsonArray parser_array(String value) {
        JsonArray jsonArray = null;

        if (StringUtils.isEmpty(value) || value.equals("[]") || value.equals("timeout")) {
            log.debug("待解析json数据为空");
            return null;
        }

        int start = value.indexOf("[");
        int end = value.lastIndexOf("]");

        if (start > -1 && end > -1) {
            try {
                jsonArray = jsonparser.parse(value.substring(start, end + 1)).getAsJsonArray();
            } catch (Exception e) {
                log.debug("字符串转jsonarray失败", e);
            }
        }


        if (jsonArray == null) {
            jsonArray = new JsonArray();
        }
        return jsonArray;
    }

    // ///////////////////////////////////////////////////////////////公有方法/////////////////////////////////////////////////////////////////////////////

    /**
     * 获取安全方法链的 jsonarray
     */
    public JsonArray array(String attrbute, JsonObject json, String aql) {
        log.trace("#获取 json 节点组 safety ".concat(aql));
        JsonArray json_array = new JsonArray();
        StringTokenizer token = new StringTokenizer(aql, AQL_SEPARATOR);
        try {
            while (token.hasMoreElements()) {
                String args_string = (String) token.nextElement();
                if (token.hasMoreElements()) {
                    json = get_jsonobject_byjsonobject(json, args_string);
                } else {
                    json_array = json.get(args_string).getAsJsonArray();
                }
            }
        } catch (Exception e) {
            // Tool_Collect.collect(website, crawl_token, PAGE, Level.WARN.name(), "解析 json 节点".concat(attrbute).concat("错误:").concat(aql), null);
            log.debug("解析 json 节点".concat(attrbute).concat("错误:").concat(aql), e);
        }
        if (json_array == null) {
            json_array = new JsonArray();
        }
        return json_array;
    }

    /**
     * 获取安全方法链的值，方法中出现错误返回传入的 value
     */
    public String value(String attrbute, JsonObject json, String aql, String defaule_result) {
        log.trace("#获取 json 节点 safety ".concat(aql));
        JsonObject json_object = json;
        StringTokenizer token = new StringTokenizer(aql, AQL_SEPARATOR);
        Object tmp_result = null;
        try {
            while (token.hasMoreElements()) {
                String args_string = (String) token.nextElement();
                if (token.hasMoreElements()) {
                    json_object = get_jsonobject_byjsonobject(json_object, args_string);
                } else {
                    tmp_result = json_object.get(args_string).getAsString();
                }
            }
        } catch (Exception e) {
            String message = "解析 json 节点".concat(attrbute).concat("错误:").concat(aql);
            //Tool_Collect.collect(website, crawl_token, PAGE, Level.WARN.name(), message, null);
            log.debug(message, e);
        }
        defaule_result = (tmp_result == null) ? defaule_result : StringUtils.trim(tmp_result.toString());
        return defaule_result;
    }

    /**
     * 获取方法链的值
     */
    public String value_with_error(String attrbute, JsonObject json, String aql, String defaule_result) {
        log.trace("#获取 json 节点 safety ".concat(aql));
        JsonObject json_object = json;
        StringTokenizer token = new StringTokenizer(aql, AQL_SEPARATOR);
        Object tmp_result = null;
        try {
            while (token.hasMoreElements()) {
                String args_string = (String) token.nextElement();
                if (token.hasMoreElements()) {
                    json_object = get_jsonobject_byjsonobject(json_object, args_string);
                } else {
                    tmp_result = json_object.get(args_string).getAsString();
                }
            }
        } catch (Exception e) {
            log.debug("解析 json 节点".concat(attrbute).concat("错误:").concat(aql), e);
        }
        defaule_result = (tmp_result == null) ? defaule_result : StringUtils.trim(tmp_result.toString()).replaceAll("\"", "");
        return defaule_result;
    }

    // ///////////////////////////////////////////////////////////////私有方法//////////////////////////////////////////////////////////////////////

    /**
     * 获取一个节点
     *
     * @param json_object json对象
     * @param args_string json对象中的字段
     * @return
     * @throws ParseException
     */
    private JsonObject get_jsonobject_byjsonobject(JsonObject json_object, String args_string) throws ParseException {
        json_object = json_object.get(args_string).getAsJsonObject();
        return json_object;
    }

    /**
     * 获取安全方法链的值，方法中出现错误返回传入的 value
     */
    public String value2(String attrbute, JsonObject json, String aql, String defaule_result) {
        log.trace("#获取 json 节点 safety ".concat(aql));
        JsonObject json_object = json;
        StringTokenizer token = new StringTokenizer(aql, AQL_SEPARATOR);
        Object tmp_result = null;
        try {
            while (token.hasMoreElements()) {
                String args_string = (String) token.nextElement();
                if (token.hasMoreElements()) {
                    json_object = get_jsonobject_byjsonobject2(json_object, args_string);
                } else {
                    tmp_result = get_jsonstr2(json_object, args_string);
                }
            }
        } catch (Exception e) {
            String message = "解析 json 节点".concat(attrbute).concat("错误:").concat(aql);
            log.debug(message, e);
        }
        defaule_result = (tmp_result == null) ? defaule_result : StringUtils.trim(tmp_result.toString()).replaceAll("\"", "");
        return defaule_result;
    }

    /**
     * 获取一个节点
     *
     * @param json_object json对象
     * @param args_string json对象中的字段
     * @return
     * @throws ParseException
     */
    private JsonObject get_jsonobject_byjsonobject2(JsonObject json_object, String args_string) throws ParseException {
        if (args_string.matches("[^\\s]+\\[\\d+\\]")) {
            //如果是obj[2]的形式
            Matcher matcher = pattern.matcher(args_string);
            if (matcher.find()) {
                //如果匹配到了数组和序号
                String arrStr = matcher.group(1);
                //数组
                int i = Integer.parseInt(matcher.group(2));
                //序号

                if (json_object.has(arrStr)) {
                    //如果数组存在
                    JsonElement je = json_object.get(arrStr);
                    if (je.isJsonArray()) {
                        //如果是数组
                        JsonArray arr = je.getAsJsonArray();
                        if (arr.size() > i) {
                            //如果序号没有越界
                            //返回数组中的元素
                            return arr.get(i).getAsJsonObject();
                        }
                    }
                }
            }
        }

        //按照原来的规则返回
        return json_object.get(args_string).getAsJsonObject();
    }

    private String get_jsonstr2(JsonObject json_object, String args_string) throws ParseException {
        if (args_string.matches("[^\\s]+\\[\\d+\\]")) {
            //如果是obj[2]的形式
            Matcher matcher = pattern.matcher(args_string);
            if (matcher.find()) {
                //如果匹配到了数组和序号
                String arrStr = matcher.group(1);
                //数组
                int i = Integer.parseInt(matcher.group(2));
                //序号

                if (json_object.has(arrStr)) {
                    //如果数组存在
                    JsonElement je = json_object.get(arrStr);
                    if (je.isJsonArray()) {
                        //如果是数组
                        JsonArray arr = je.getAsJsonArray();
                        if (arr.size() > i) {
                            //如果序号没有越界
                            return arr.get(i).getAsString();
                            //返回数组中的元素
                        }
                    }
                }
            }
        }

        return json_object.get(args_string).getAsString();
        //按照原来的规则返回
    }


    private Pattern pattern = Pattern.compile("([^\\s]+)\\[(\\d+)\\]");


    /**
     * 在json数组中查找json对象,只要这个对象指定的字段等于给出的字符串
     *
     * @param jsonArray 需要查询的json数组
     * @param key       字段
     * @param val       值
     * @return 如果找到返回json对象, 如果没有找到返回空
     */
    public JsonObject searchJsonInJsonArrayEqual(JsonArray jsonArray, String key, String val) {
        if (val != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement e = jsonArray.get(i);
                //如果数组中是json对象
                if (e != null && !e.isJsonNull() && e.isJsonObject()) {
                    JsonObject o = e.getAsJsonObject();
                    //如果json对象中指定字段等于val
                    if (o.has(key) && val.equals(o.get(key).getAsString())) {
                        return o;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 在json数组中查找json对象,只要这个对象指定的字段包含给出的字符串
     *
     * @param jsonArray 需要查询的json数组
     * @param key       字段
     * @param val       值
     * @return 如果找到返回json对象, 如果没有找到返回空
     */
    public JsonObject searchJsonInJsonArrayContains(JsonArray jsonArray, String key, String val) {
        if (val != null) {
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonElement e = jsonArray.get(i);
                //如果数组中是json对象
                if (e != null && !e.isJsonNull() && e.isJsonObject()) {
                    JsonObject o = e.getAsJsonObject();
                    //如果json对象中指定字段等于val
                    if (o.has(key) && o.get(key).isJsonPrimitive()) {
                        try {
                            String s = o.get(key).getAsString();

                            if (s != null && s.contains(val)) {
                                return o;
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                    }
                }
            }
        }

        return null;
    }


    public JsonElement strToJsonElement(String info) {
        try {
            return jsonparser.parse(info);
        } catch (Exception e) {
            //Tool_Collect.collectInfo(website, crawl_token, "字符串转jsonElement失败");
            log.error("字符串转jsonElement失败", e);
        }
        return null;
    }

    public JsonObject getJsonObject(String attrbute, JsonObject json, String aql) {
        log.trace("#获取 json 节点组 safety ".concat(aql));
        JsonObject jsonObject = new JsonObject();
        StringTokenizer token = new StringTokenizer(aql, AQL_SEPARATOR);
        try {
            while (token.hasMoreElements()) {
                String args_string = (String) token.nextElement();
                if (token.hasMoreElements()) {
                    json = get_jsonobject_byjsonobject(json, args_string);
                } else {
                    jsonObject = json.get(args_string).getAsJsonObject();
                }
            }
        } catch (Exception e) {
            // Tool_Collect.collect(website, crawl_token, PAGE, Level.WARN.name(), "解析 json 节点".concat(attrbute).concat("错误:").concat(aql), null);
            log.debug("解析 json 节点".concat(attrbute).concat("错误:").concat(aql), e);
        }
        if (jsonObject == null) {
            jsonObject = new JsonObject();
        }
        return jsonObject;
    }


    public String elementToString(JsonElement element) {

        try {
            return element.getAsString();
        } catch (Exception e) {
            log.debug("解析 json 节点 错误", e);
        }
        return "";
    }
}