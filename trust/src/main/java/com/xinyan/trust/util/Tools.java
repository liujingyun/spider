package com.xinyan.trust.util;

import com.google.gson.JsonObject;
import com.sun.org.apache.xml.internal.serializer.Encodings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import sun.misc.BASE64Decoder;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName Tools 
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/24 16:59
 * @Version V1.0
 **/
@Slf4j
public class Tools {
    

        private String website = "";                                                                                        // 数据源
        private String crawl_token = "";                                                                                    // token


        private static final String DES_KEY = "xinyan66";

        /**
         * 使用 数据源 和 token 初始化工具类
         */
        public Tools(String website, String crawl_token) {
            this.website = website;
            this.crawl_token = crawl_token;
        }

        // ======================================================工具方法

        /**
         * 根据正则表达式找到所有匹配的字符串，拼接成一个字符串返回,发生异常时将记录日志并返回默认值
         * <p>
         * 例如:<br>
         * String s = "someother111someother222someother333someother"<br>
         * extract_by_regexPattern(s, "\\d+", null)<br>
         * 返回"111222333"
         *
         * @param in           被截字符串
         * @param regexPattern 需要匹配的正则表达式
         * @param defaultValue 不能匹配返回该默认值
         * @return String 所有匹配的字符串
         */
        public String extract_by_regexPattern(String in, String regexPattern, String defaultValue) {
            if (StringUtils.isEmpty(in) || StringUtils.isEmpty(regexPattern) || in.equals("")) {
                log.debug("提取数值失败");
                return defaultValue;
            }
            try {
                Pattern pattern = Pattern.compile(regexPattern);
                Matcher matcher = pattern.matcher(in);

                StringBuilder result = new StringBuilder();
                while (matcher.find()) {
                    String findItem = matcher.group();
                    result.append(findItem);
                }
                return result.toString();
            } catch (Exception e) {
                log.debug("提取数值失败", e);
            }
            return defaultValue;
        }

        /**
         * 安全截取字符串(会进行异常处理和记录错误日志)
         *
         * @param originStr  原始字符串
         * @param beginIndex 开始角标
         * @param endIndex   结束角标
         * @return 截取到的字符串
         */
        public String safe_substring(String originStr, Integer beginIndex, Integer endIndex) {
            try {
                return originStr.substring(beginIndex, endIndex);
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            return "";
        }

        /**
         * 提取（正数）金额,规则为只提取数字和点号,可以有点号，也可以没有 格式为2,0.2
         *
         * @param string         包含金额的字符串
         * @param default_result 出现错误时默认返回值
         * @return (double类型)金额的值
         */
        public Double extract_double(String string, Double default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                Pattern compile = Pattern.compile("(\\d+\\.\\d+)|(\\d+)");
                Matcher matcher = compile.matcher(string);
                boolean isfind = matcher.find();
                if (isfind) {
                    default_result = Double.valueOf(matcher.group());
                }
            } catch (Exception e) {
                String message = "提取数值失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 按照给定的格式，将字符串转换成Date类型
         *
         * @param formatDate     SimpleDateFormat实例
         * @param string         时间字符串
         * @param default_result 出错后的默认返回值
         * @return 对应的Data对象
         */
        public Date to_date(SimpleDateFormat formatDate, String string, Date default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                return formatDate.parse(string);
            } catch (Exception e) {
                String message = "字符串转 date 失败 ".concat(string);
                log.debug(message, e);
                return default_result;
            }
        }

        /**
         * 根据输入的年和月得到本月的最后一天
         *
         * @param year  年份
         * @param month 月份
         * @return (Date对象)本月的最后一天
         */
        public Date getLastDayOfMonth(Integer year, Integer month, Date default_result) {
            Calendar cal = Calendar.getInstance();

            try {
                cal.set(Calendar.YEAR, year);// 年
                cal.set(Calendar.MONTH, month);// 月
                cal.set(Calendar.DATE, 0);// 日，设为一号
                cal.set(Calendar.HOUR_OF_DAY, 0);// 时，设为0
                cal.set(Calendar.MINUTE, 0);// 分，设为0
                cal.set(Calendar.SECOND, 0);// 秒，设为0
                return cal.getTime();
            } catch (Exception e) {
                String message = "得到本月的最后一天 失败 ".concat("year:" + year + ",month:" + month);
                log.debug(message, e);
                return default_result;
            }

        }

        /**
         * 按照给定的格式，将字符串转化成对应的时间戳对象
         *
         * @param formatDate     SimpleDateFormat实例
         * @param string         时间字符串
         * @param default_result 出错后默认返回值
         * @return (时间戳)时间
         */
        public Timestamp to_timestamp(SimpleDateFormat formatDate, String string, Timestamp default_result) {
            if (formatDate == null || formatDate.toPattern().equals("")) {
                return default_result;
            }
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            // 将hh改为HH
            if (formatDate.toPattern().contains("hh")) {
                log.debug("格式化时间的字符串出现：hh");

                formatDate.applyPattern(formatDate.toPattern().replace("hh", "HH"));
            }
            try {
                return new Timestamp(formatDate.parse(string).getTime());
            } catch (Exception e) {
                String message = "字符串转 timestamp 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        public Timestamp to_timestamp(String formatDate, String string, Timestamp default_result) {
            SimpleDateFormat sourceFormat = new SimpleDateFormat(formatDate);
            return to_timestamp(sourceFormat, string, default_result);
        }

        /**
         * 获取指定月的第一天
         *
         * @param sourcePattern
         * @param string
         * @param targetPattern
         * @return
         */
        public String get_start_date(String string, String sourcePattern, String targetPattern) {
            if (StringUtils.isEmpty(string) || StringUtils.isEmpty(sourcePattern) || StringUtils.isEmpty(targetPattern)) {
                return "";
            }
            String time = "";
            SimpleDateFormat sourceFormat = new SimpleDateFormat(sourcePattern);
            SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);
            Calendar calendar = Calendar.getInstance();

            try {
                calendar.setTime(sourceFormat.parse(string));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                time = targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符串转 date 失败 ".concat(string);
                log.debug(message, e);
            }
            if (StringUtils.isEmpty(time)) {
                time = check_date(string, targetPattern, null);
                try {
                    calendar.setTime(targetFormat.parse(time));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
                    time = targetFormat.format(calendar.getTime());
                } catch (Exception e) {
                    String message = "字符串转 date 失败 ".concat(string);
                    log.debug(message, e);
                }

            }


            return time;
        }


        /**
         * 获取指定月的最后一天
         *
         * @param sourcePattern
         * @param string
         * @param targetPattern
         * @return
         */
        public String get_end_date(String string, String sourcePattern, String targetPattern) {
            if (StringUtils.isEmpty(string) || StringUtils.isEmpty(sourcePattern) || StringUtils.isEmpty(targetPattern)) {
                return "";
            }

            SimpleDateFormat sourceFormat = new SimpleDateFormat(sourcePattern);
            SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);
            Calendar calendar = Calendar.getInstance();

            String time = "";
            try {
                calendar.setTime(sourceFormat.parse(string));
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                time = targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符串转 date 失败 ".concat(string);
                log.debug(message, e);
            }

            if (StringUtils.isEmpty(time)) {
                time = check_date(string, targetPattern, null);
                try {
                    calendar.setTime(targetFormat.parse(time));
                    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                    time = targetFormat.format(calendar.getTime());
                } catch (Exception e) {
                    String message = "字符串转 date 失败 ".concat(string);
                    log.debug(message, e);
                }

            }

            return time;
        }

        /**
         * 将当前时间已指定格式输出
         *
         * @param targetPattern
         * @return
         */
        public String get_now_date(String targetPattern) {
            if (StringUtils.isEmpty(targetPattern)) {
                return "";
            }

            try {
                SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);
                Calendar calendar = Calendar.getInstance();
                return targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符串转 date 失败 ".concat(targetPattern);
                log.debug(message, e);
            }
            return "";
        }

        /**
         * 获取当前月的最后一天
         *
         * @param targetPattern
         * @return
         */
        public String get_lastDayOfMonth(String targetPattern) {
            if (StringUtils.isEmpty(targetPattern)) {
                return "";
            }

            try {
                SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                return targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符串转 date 失败 ".concat(targetPattern);
                log.debug(message, e);
            }
            return "";
        }


        public String get_previous_date(String targetPattern) {
            if (StringUtils.isEmpty(targetPattern)) {
                return "";
            }
            try {
                SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, -1);
                return targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符串转 上一年 失败 ".concat(targetPattern);
                log.debug(message, e);
            }
            return "";
        }


        public String get_true_year(String string, String sourcePattern, String targetPattern) {
            if (StringUtils.isEmpty(string)) {
                return "";
            }
            String time = "";
            if (sourcePattern.contains("y")) {
                time = get_format_date_check(string, sourcePattern, targetPattern, null);
            } else {
                time = get_true_year_check(string, sourcePattern, targetPattern, null);
            }


            if (StringUtils.isEmpty(time)) {
                time = check_date(string, targetPattern, null);
            }

            return time;
        }

        /**
         * 获取指定月的年份
         *
         * @param string
         * @param sourcePattern
         * @param targetPattern
         * @return
         */
        private String get_true_year_check(String string, String sourcePattern, String targetPattern, Locale locale) {
            if (StringUtils.isEmpty(string)) {
                return "";
            }
            String time = "";
            try {
                SimpleDateFormat sourceFormat = null;
                if (locale == null) {
                    sourceFormat = new SimpleDateFormat(sourcePattern);
                } else {
                    sourceFormat = new SimpleDateFormat(sourcePattern, locale);
                }

                SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);

                Calendar calendarNow = Calendar.getInstance();
                int nowMonth = calendarNow.get(Calendar.MONTH) + 1;


                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sourceFormat.parse(string));
                int month = calendar.get(Calendar.MONTH) + 1;


                calendarNow.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
                calendarNow.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
                calendarNow.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                calendarNow.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                calendarNow.set(Calendar.SECOND, calendar.get(Calendar.SECOND));
                if (month > nowMonth) {
                    calendarNow.add(Calendar.YEAR, -1);
                    return targetFormat.format(calendarNow.getTime());
                } else {
                    return targetFormat.format(calendarNow.getTime());
                }

            } catch (Exception e) {
                String message = "字符串转 上一年 失败 ".concat(targetPattern);
                log.debug(message, e);

            }
            return time;
        }

        public String get_format_date(String string, String sourcePattern, String targetPattern) {
            return get_format_date(string, sourcePattern, targetPattern, null);
        }


        public String get_format_date(String string, String sourcePattern, String targetPattern, Locale locale) {
            if (StringUtils.isEmpty(string)) {
                return "";
            }
            String time = "";
            if (sourcePattern.contains("y")) {
                time = get_format_date_check(string, sourcePattern, targetPattern, locale);
            } else {
                time = get_true_year_check(string, sourcePattern, targetPattern, locale);
            }


            if (StringUtils.isEmpty(time)) {
                time = check_date(string, targetPattern, locale);
            }

            return time;
        }

        private String get_format_date_check(String string, String sourcePattern, String targetPattern, Locale locale) {
            if (StringUtils.isEmpty(string)) {
                return "";
            }
            String time = "";
            try {
                SimpleDateFormat sourceFormat = null;
                if (locale == null) {
                    sourceFormat = new SimpleDateFormat(sourcePattern);
                } else {
                    sourceFormat = new SimpleDateFormat(sourcePattern, locale);
                }
                SimpleDateFormat targetFormat = new SimpleDateFormat(targetPattern);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sourceFormat.parse(string));

                time = targetFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "字符年月格式化 失败 ".concat(targetPattern);
                log.debug(message, e);
            }

            return time;
        }

        /**
         * 时间转换为秒
         *
         * @param string
         * @return
         */
        public BigDecimal get_formatTime_to_int(String string) {
            if (StringUtils.isEmpty(string)) {
                return new BigDecimal(0);
            }
            string = string.replace(" ", "");
            if (string.contains("不限") || string.contains("无限")) {
                return new BigDecimal(99999999);
            }

            try {
                String hour = "";
                String minute = "";
                String second = "";
                if (!string.contains("时") && !string.contains("分") && !string.contains("秒")) {

                    Matcher matcher = Pattern.compile("(\\d+):(\\d+)").matcher(string);
                    if (matcher.matches()) {
                        minute = RegexUtil.getValue("(\\d+):(\\d+)", string, 1);
                        if (Pattern.compile("0(\\d+)").matcher(minute).find()) {
                            minute = RegexUtil.getValue("0(\\d+)", minute, 1);
                        }
                        second = RegexUtil.getValue("(\\d+):(\\d+)", string, 2);
                        if (Pattern.compile("0(\\d+)").matcher(second).find()) {
                            second = RegexUtil.getValue("0(\\d+)", second, 1);
                        }
                    }


                    matcher = Pattern.compile("(\\d+):(\\d+):(\\d+)").matcher(string);
                    if (matcher.matches()) {
                        hour = RegexUtil.getValue("(\\d+):(\\d+):(\\d+)", string, 1);
                        if (Pattern.compile("0(\\d+)").matcher(hour).find()) {
                            hour = RegexUtil.getValue("0(\\d+)", hour, 1);
                        }
                        minute = RegexUtil.getValue("(\\d+):(\\d+):(\\d+)", string, 2);
                        if (Pattern.compile("0(\\d+)").matcher(minute).find()) {
                            minute = RegexUtil.getValue("0(\\d+)", minute, 1);
                        }
                        second = RegexUtil.getValue("(\\d+):(\\d+):(\\d+)", string, 3);
                        if (Pattern.compile("0(\\d+)").matcher(second).find()) {
                            second = RegexUtil.getValue("0(\\d+)", second, 1);
                        }
                    }


                    matcher = Pattern.compile("(\\d+)'(\\d+)'(\\d+)").matcher(string);
                    if (matcher.matches()) {
                        hour = RegexUtil.getValue("(\\d+)'(\\d+)'(\\d+)", string, 1);
                        if (Pattern.compile("0(\\d+)").matcher(hour).find()) {
                            hour = RegexUtil.getValue("0(\\d+)", hour, 1);
                        }
                        minute = RegexUtil.getValue("(\\d+)'(\\d+)'(\\d+)", string, 2);
                        if (Pattern.compile("0(\\d+)").matcher(minute).find()) {
                            minute = RegexUtil.getValue("0(\\d+)", minute, 1);
                        }
                        second = RegexUtil.getValue("(\\d+)'(\\d+)'(\\d+)", string, 3);
                        if (Pattern.compile("0(\\d+)").matcher(second).find()) {
                            second = RegexUtil.getValue("0(\\d+)", second, 1);
                        }
                    }


                } else if (string.contains("时") || string.contains("分") || string.contains("秒")) {
                    hour = RegexUtil.getValue("(\\d+)(.*?时)", string, 1);
                    minute = RegexUtil.getValue("(\\d+)分", string, 1);
                    second = RegexUtil.getValue("(\\d+)秒", string, 1);

                }

                if (StringUtils.isEmpty(hour) && StringUtils.isEmpty(minute) && StringUtils.isEmpty(second)) {
                    return to_bigDecimal(string, 0);
                }

                return to_bigDecimal(hour, 0).multiply(new BigDecimal(60 * 60))
                        .add(to_bigDecimal(minute, 0).multiply(new BigDecimal(60)))
                        .add(to_bigDecimal(second, 0));
            } catch (Exception e) {
                String message = "时间字符 转换为秒 失败".concat(string);
                log.debug(message, e);
            }
            return new BigDecimal(0);
        }

        /**
         * 流量转换为kb
         *
         * @param string
         * @return
         */
        public BigDecimal to_kb(String string) {
            if (StringUtils.isEmpty(string)) {
                return new BigDecimal(0);
            }
            string = string.replace(" ", "").replace("(", "").replace(")", "");
            if (string.contains("不限") || string.contains("无限")) {
                return new BigDecimal(99999999);
            }


            try {
                if (!string.toUpperCase().contains("M") && !string.toUpperCase().contains("G") && !string.toUpperCase().contains("K")) {
                    return to_bigDecimal(string, 0);
                }
                String gb = RegexUtil.getValue("([0-9]*\\.?[0-9]*)[G|g]", string, 1);
                String mb = RegexUtil.getValue("([0-9]*\\.?[0-9]*)[M|m]", string, 1);
                String kb = RegexUtil.getValue("([0-9]*\\.?[0-9]*)[K|k]", string, 1);
                return to_bigDecimal(gb, 0, 5).multiply(new BigDecimal(1048576))
                        .add(to_bigDecimal(mb, 0, 5).multiply(new BigDecimal(1024)))
                        .add(to_bigDecimal(kb, 0, 5)).setScale(0, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
                String message = "流量转换为kb 失败".concat(string);
                log.debug(message, e);
            }
            return new BigDecimal(0);
        }


        public Integer get_distance_current_month(String string, String pattern) {
            return getDistanceCurrentMonth(string,pattern,0);

        }

        public Integer get_distance_current_month(String string, String pattern, Integer default_result) {
            return getDistanceCurrentMonth(string,pattern,default_result);
        }

        private Integer getDistanceCurrentMonth(String string, String pattern, Integer default_result) {
            if (StringUtils.isEmpty(string)) {
                return default_result;
            }
            try {
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(format.parse(string));


                Calendar calendarNow = Calendar.getInstance();

                return (calendarNow.get(Calendar.YEAR) - calendar.get(Calendar.YEAR)) * 12 + (calendarNow.get(Calendar.MONTH) - calendar.get(Calendar.MONTH));
            } catch (Exception e) {
                String message = "获取距离当前月 失败".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 将时间字符串转换成为LocalDateTime.
         *
         * @param dataTimeFormatterString 时间格式字符串,例如:yyyy-MM-dd HH:mm:ss(注意必须要时分秒信息)
         * @param localDateTimeString     需要转换的时间字符串
         * @param default_result          出错后默认返回值
         * @return (LocalDateTime对象)时间
         */
        public LocalDateTime to_localdatetime(String dataTimeFormatterString, String localDateTimeString, LocalDateTime default_result) {
            if (dataTimeFormatterString == null || dataTimeFormatterString.equals("")) {
                return default_result;
            }
            if (localDateTimeString == null || localDateTimeString.equals("")) {
                return default_result;
            }
            // 将hh改为HH
            if (dataTimeFormatterString.contains("hh")) {
                log.debug("格式化时间的字符串出现：hh");
                dataTimeFormatterString = dataTimeFormatterString.replace("hh", "HH");
            }

            try {
                DateTimeFormatter dataTimeFormatter = DateTimeFormatter.ofPattern(dataTimeFormatterString);
                return LocalDateTime.parse(localDateTimeString, dataTimeFormatter);
            } catch (Exception e) {
                String message = "字符串转 LocalDateTime 失败 :".concat(localDateTimeString);
                log.debug(message, e);
            }

            return default_result;
        }


        /**
         * 按照指定倍数输出int
         *
         * @param string
         * @param multiple
         * @return
         */
        public Integer to_multiple_integer(String string, Integer multiple) {
            if (StringUtils.isBlank(string)) {
                return 0;
            }
            string = string.replaceAll(",", "").replaceAll("元", "").trim();
            try {
                return new BigDecimal(string).multiply(new BigDecimal(multiple)).intValue();
            } catch (Exception e) {
                String message = "字符串转 Integer 失败 ".concat(string);
                log.debug(message, e);
            }
            return 0;
        }

        /**
         * 按照指定倍数输出bigdecimal
         *
         * @param string
         * @param multiple
         * @param default_result
         * @return
         */
        public BigDecimal to_multiple_bigdecimal(String string, Integer multiple, BigDecimal default_result, Integer scale) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            string = string.replaceAll(",", "").replaceAll("元", "").trim();
            try {
                if (scale == null) {
                    return new BigDecimal(string).multiply(new BigDecimal(multiple)).setScale(0, BigDecimal.ROUND_HALF_UP);
                } else {
                    return new BigDecimal(string).multiply(new BigDecimal(multiple)).setScale(scale, BigDecimal.ROUND_HALF_UP);
                }

            } catch (Exception e) {
                String message = "字符串转 BigDecimal 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 按照指定倍数输出bigdecimal
         *
         * @param string
         * @param multiple
         * @return
         */
        public BigDecimal to_multiple_bigdecimal(String string, Integer multiple) {
            return to_multiple_bigdecimal(string, multiple, new BigDecimal(0), 2).setScale(0, BigDecimal.ROUND_HALF_UP);
        }

        /**
         * 按照指定倍数输出double
         *
         * @param string
         * @param multiple
         * @param default_result
         * @return
         */
        public Double to_multiple_double(String string, Integer multiple, Double default_result, Integer scale) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            string = string.replaceAll(",", "").replaceAll("元", "").trim();
            try {
                if (scale == null) {
                    return new BigDecimal(string).multiply(new BigDecimal(multiple)).doubleValue();
                } else {
                    return new BigDecimal(string).multiply(new BigDecimal(multiple)).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
                }

            } catch (Exception e) {
                String message = "字符串转 BigDecimal 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 字符串转Integer
         * <p>
         * "123" --> 123
         *
         * @param string         整数字符串
         * @param default_result 出错后默认返回值
         * @return Integer对象
         */
        public Integer to_integer(String string, Integer default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            string = string.replaceAll(",", "").replaceAll("元", "").trim();
            try {
                return Integer.valueOf(string);
            } catch (Exception e) {
                String message = "字符串转 Integer 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 字符串转Double
         * <p>
         * "123.321"-->123.321
         *
         * @param string         浮点数字符串
         * @param default_result 出错后默认返回值
         * @return Double 对象
         */
        public Double to_double(String string, Double default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                return Double.valueOf(string.replaceAll(",", "").replaceAll("元", "").trim());
            } catch (Exception e) {
                String message = "字符串转 Double 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 字符串转换为bigdecimal
         *
         * @param string
         * @param default_result
         * @return
         */
        public BigDecimal to_bigDecimal(String string, Integer default_result) {
            return to_bigDecimal(string, default_result, 0);
        }

        public BigDecimal to_bigDecimal(String string, Integer default_result, Integer scale) {

            try {
                if (StringUtils.isBlank(string)) {
                    return new BigDecimal(0).setScale(scale, BigDecimal.ROUND_HALF_UP);
                }
                return new BigDecimal(string.replaceAll(",", "").replaceAll("元", "").trim()).setScale(scale, BigDecimal.ROUND_HALF_UP);
            } catch (Exception e) {
                String message = "字符串转 BigDecimal 失败 ".concat(string);
                log.debug(message, e);
            }
            return new BigDecimal(default_result);
        }

        /**
         * double 转换为 bigdecimal
         *
         * @param string
         * @param default_result
         * @return
         */
        public BigDecimal double_to_bigDecimal(Double string, BigDecimal default_result) {
            if (string == null) {
                return default_result;
            }
            try {
                return new BigDecimal(string.toString());
            } catch (Exception e) {
                String message = "Double转 BigDecimal 失败 ";
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 百分数字符串转Double
         * <p>
         * "12.24%"-->0.1224
         *
         * @param percent        百分数字符串转
         * @param default_result 出错后默认返回值
         * @return Double 对象
         */
        public Double percent_to_double(String percent, Double default_result) {
            if (percent == null || percent.trim().equals("") || !(percent.trim().matches("^(100|[0-9]?\\d)(\\.\\d*)?%$"))) {
                return default_result;
            }
            try {
                return Double.valueOf(percent.trim().replace("%", "")) / 100;
            } catch (Exception e) {
                String message = "百分数字符串转 Double 失败 ".concat(percent);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * double精度 保留小数
         * <p>
         *
         * @param default_result 出错后默认返回值
         * @return Double 对象
         */
        public String to_doubleScale(Double default_result, Integer scale) {
            if (default_result == null) {
                return null;
            }
            try {

                return new BigDecimal(default_result.toString()).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
            } catch (Exception e) {
                String message = "字符串转 Double 失败 ";
                log.debug(message, e);
            }
            return null;
        }

        /**
         * 字符串转Long
         * <p>
         * "123" --> 123
         *
         * @param string         整数字符串
         * @param default_result 出错后默认返回值
         * @return Long对象
         */
        public Long to_Long(String string, Long default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                return Long.valueOf(string);
            } catch (Exception e) {
                String message = "字符串转 Long 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 保留两位小数，转换为字符串
         *
         * @param string
         * @param default_result
         * @return
         */
        public String to_bg_double_str(String string, String default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                return new BigDecimal(string.replaceAll(",", "").replaceAll("元", "").trim()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
            } catch (Exception e) {
                String message = "字符串转 Double 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 保留小数，转换为字符串
         *
         * @param string
         * @param default_result
         * @param scale
         * @return
         */
        public String to_bg_double_str(String string, String default_result, Integer scale) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                if (scale == null) {
                    return new BigDecimal(string.replaceAll(",", "").replaceAll("元", "").trim()).toPlainString();
                } else {
                    return new BigDecimal(string.replaceAll(",", "").replaceAll("元", "").trim()).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
                }

            } catch (Exception e) {
                String message = "字符串转 Double 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }

        /**
         * 字符串转换为double
         *
         * @param string
         * @param default_result
         * @return
         */
        public Double to_bg_double(String string, Double default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                return new BigDecimal(string.replaceAll(",", "").replaceAll("元", "").trim()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            } catch (Exception e) {
                String message = "字符串转 Double 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }


        /**
         * 字符串转换为integer
         *
         * @param string
         * @param default_result
         * @return
         */
        public Integer to_bg_integer(String string, Integer default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }

            string = string.replaceAll(",", "").replaceAll("元", "").trim();
            try {
                return new BigDecimal(string).intValue();
            } catch (Exception e) {
                String message = "字符串转 Integer 失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }


        /**
         * 读取本地文本文件
         * <p>
         * 示例:load_file_by_path("src/xxx.xml", "utf-8")
         *
         * @param path     文件路径
         * @param encoding 编码格式,默认UTF-8
         * @return (String)文件内容
         * @throws java.io.FileNotFoundException 文件不存在
         * @throws IOException                   io异常
         */
        public String load_file_by_path(String path, String encoding) throws IOException {
            if (encoding == null || encoding.equals("")) {
                encoding = "UTF-8";
                log.debug("编码为空，默认使用".concat(encoding));

            }
            StringBuilder string = new StringBuilder("");
            try {
                try (
                        FileInputStream input = new FileInputStream(path.trim()); //
                        BufferedReader br = new BufferedReader(new InputStreamReader(input, encoding))//
                ) {
                    String tmp;
                    while ((tmp = br.readLine()) != null) {
                        string.append(tmp);
                    }
                }
            } catch (Exception e) {
                String message = "文件读取失败：".concat(path) + " 编码：".concat(encoding);// 异常信息
                log.debug(message, e);
                return null;
            }
            return string.toString();
        }

        /**
         * 在单个较长的字符串中,通过不断通过选取上界和下界来逼近,最后得到目标字符串,如果有多条个substring符合条件返回第一条.
         * <p>
         * 如果有一个文本段落：<br>
         * ...,supper,outter,we-want,outter,no-want,outter,no-want,supper,...
         * <p>
         * 而我们需要we-want
         * <p>
         * 不能用简单的正则表达式进行匹配(we-want,no-want可能具有相同的格式)<br>
         * 也不能使用某一个开始信号来截取字符串(outter在段落中出现了多次)
         * <p>
         * 那么可以通过多次截取缩小范围的方法进行截取<br>
         * "supper"..."supper"<br>
         * "outter,"...",outter"
         *
         * @param scope         需要从中截取的字符串
         * @param steps         截取字符串的上界和下界,map中的每一个key是一个上界,每个value是一个下界
         * @param defaultReturn 出现空值时的默认返回值
         * @return 返回的结果
         */
        public String surround_str(String scope, LinkedHashMap<String, String> steps, String defaultReturn) {

            // 非空判断
            if (StringUtils.isEmpty(scope))
                return defaultReturn;

            // 遍历map
            int count = 0;
            for (Map.Entry<String, String> step : steps.entrySet()) {
                count++;
                String s = StringUtils.substringBetween(scope, step.getKey(), step.getValue());
                if (s == null && count < steps.size()) {
                    //根据count做判断，最后一次字符串截取一定要匹配
                    continue;
                }
                scope = s;
            }

            //判断是否成功截取字符串
            if (scope != null){
                return scope;
            }

            //错误处理
            String message = "字符串多步截取失败!";
            log.debug(message);
            return defaultReturn;
        }

        private static final Map<String[], Integer> timeUnits;//定义处理时间字符串的策略

        //初始化时间字符串处理策略
        static {
            timeUnits = new HashMap<>();
            timeUnits.put(new String[]{"天", "日"}, 3600 * 24);
            timeUnits.put(new String[]{"小时", "时", "°"}, 3600);
            timeUnits.put(new String[]{"分钟", "分", "'"}, 60);
            timeUnits.put(new String[]{"秒钟", "秒", "\""}, 1);
        }

        /**
         * 时间字符串转换成等值的秒数(方法忽略大小写).
         * <p>
         * 支持的单位包括:<br>
         * {"天", "日"}<br>
         * {"小时", "时", "°"}<br>
         * {"分钟", "分", "'"}<br>
         * {"秒钟", "秒","\""}<br>
         * <p>
         * 示例：<br>
         * int seconds = tools.time_to_int("4分30秒",0);
         *
         * @param time          时间字符串
         * @param defaultResult 默认返回值
         * @return 秒数
         */
        public Integer time_to_int(String time, Integer defaultResult) {
            return data_to_int(time, timeUnits, defaultResult);
        }

        private static final Map<String[], Integer> subflowUnits;//定义处理网络流量的策略

        //初始化网络流量处理策略
        static {
            subflowUnits = new HashMap<>();
            subflowUnits.put(new String[]{"gb?", "\\(\\s*gb?\\s*\\)", "千兆"}, 1024 * 1024);
            subflowUnits.put(new String[]{"mb?", "\\(\\s*mb?\\s*\\)", "兆"}, 1024);
            subflowUnits.put(new String[]{"kb?", "\\(\\s*kb?\\s*\\)", "千字节"}, 1);
        }

        /**
         * 将流量字符串转换成为等值的kb数(方法忽略大小写)
         * <p>
         * 支持的单位包括:<br>
         * {"gb", "(gb)", "g", "(g)", "千兆"}<br>
         * {"mb", "(mb)", "m", "(m)", "兆"}<br>
         * {"kb", "(kb)", "k", "(k)", "千字节"}<br>
         * <p>
         * 示例：<br>
         * int seconds = tools.flow_to_int("2mb 300kb",0);
         *
         * @param subflow       流量字符串
         * @param defaultResult 默认返回值
         * @return kb数
         */
        public Integer flow_to_int(String subflow, Integer defaultResult) {
            return data_to_int(subflow, subflowUnits, defaultResult);
        }

        /**
         * 解析带单位的数值,返回一个整数,如:3小时2分钟1秒 解析返回3721.
         * 本类中对时间和流量的处理基于这个方法.<br>
         * <p>
         * 例如在Tools中的时间字符串处理中<br>
         * <p>
         * 需要定义一个单位和倍数的对应表Map<br>
         * Map<String[], Integer> timeUnits = new HashMap<String[], Integer>();<br>
         * timeUnits.put(new String[]{"天", "日", "days", "day", "d"}, 3600 * 24);<br>
         * timeUnits.put(new String[]{"小时", "时", "hours", "hour", "h"}, 3600);<br>
         * timeUnits.put(new String[]{"分钟", "分", "minutes", "minute", "m"}, 60);<br>
         * timeUnits.put(new String[]{"秒钟", "秒", "seconds", "second", "s"}, 1);<br>
         * <p>
         * 随后就可以传入需要处理的时间字符串和默认返回值<br>
         * data_to_int("1时30分40秒", timeUnits, 0);
         *
         * @param data  需要解析的字符串
         * @param units 一个map表,键是一个单位数组,值是换算率比如<{亿,万万}:10000000>
         * @return 对应的kb数
         */
        public Integer data_to_int(String data, Map<String[], Integer> units, Integer defaultResult) {

            if (data == null) {
                log.debug("Tools.data_to_int()转换int失败:null");
                return defaultResult;
            } else if (data.trim().equals("")) {
                log.debug("Tools.data_to_int()转换int失败:\"\"");
                return defaultResult;
            }

            Double result = 0.0;// 累加基数
            boolean hasMatch = false;//判断是否匹配过
            // 非空判断
            if (!StringUtils.isEmpty(data) && units != null) {
                // 遍历单位类型
                for (Map.Entry<String[], Integer> unit : units.entrySet()) {
                    // 遍历意思相同的单位数组
                    for (String u : unit.getKey()) {
                        Matcher intMatcher = Pattern.compile("((\\d+)(\\.\\d*)?)\\s*" + u, Pattern.CASE_INSENSITIVE).matcher(data);// 正则匹配
                        if (intMatcher.find()) {
                            hasMatch = true;
                            result += Double.parseDouble(intMatcher.group(1)) * unit.getValue();// 累加
                            break;
                        }
                    }
                }
            }

            if (result == 0 && !hasMatch) {
                if (!data.startsWith("0.")) {
                    //如果是纯数字
                    if (data.trim().matches("^\\d+(\\.\\d*)?$")) {
                        return to_integer(StringUtils.substringBefore(data, "."), null);
                    } else {
                        log.debug("Tools.data_to_int()转换int失败:" + data);
                        return defaultResult;
                    }
                }
            }
            return result.intValue();
        }


        /**
         * 不知道年份的情况下,从月份判断是今年还是去年,如果小于当前月份则是今年,反之是去年.
         * <p>
         * 应用：北京移动
         *
         * @param month 月份
         * @return 月份所在年份
         */
        @SuppressWarnings("deprecation")
        public int check_year(int month) {
            Date date = new Date();
            int year = date.getYear() + 1900;
            year = (date.getMonth() + 1) < month ? year - 1 : year;
            return year;
        }


        /**
         * 对date对象的月份进行操作,可以指定向前或者向后推指定个月,并且将日期设为当月1日
         * <p>例如:<br>
         * month_oper("2014-5-5","yyyy-M-d",1)返回一个Date:2014-6-1,<br>
         * month_oper("2014-5-5","yyyy-M-d",-1)返回一个Date:2014-4-1.
         * <p>
         * 应用：重庆移动
         *
         * @param dateStr       从这个参数的日期算起.
         * @param dateFormatStr 时间格式字符串.
         * @param offset        月份加减的数量.
         * @return 月份操作后的Date对象(当月1日)
         */
        public Timestamp month_oper(String dateStr, String dateFormatStr, int offset) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
                Date fromDate = dateFormat.parse(dateStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                cal.add(Calendar.MONTH, offset);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                return new Timestamp(cal.getTime().getTime());
            } catch (Exception e) {
                // 错误处理
                String message = "月份加减操作 失败".concat(dateStr == null ? "null" : "\"" + dateStr + "\"");// 异常信息
                log.debug(message, e);
                return null;
            }
        }

        /**
         * 冒号格式的时间字符串,计算响应的秒数.<br>
         * <p>
         * 例如传入1:30:40 , 返回5440
         *
         * @param time 冒号格式的时间字符串
         * @return 响应的秒数
         */
        public Integer colon_time_to_int(String time) {
            int seconds = 0;
            if (time == null || time.equals("")){
                return seconds;
            }
            Pattern pattern = Pattern.compile("(\\d{1,2}:\\d{1,2}:\\d{1,2})|(\\d{1,2}:\\d{1,2})|(\\d{1,2})");
            Matcher m = pattern.matcher(time);
            if (m.find()) {
                time = m.group();
            }
            try {
                time = time.replace(":", "-");
                String[] timesplit = time.split("-");
                if (timesplit.length == 3) {
                    seconds = Integer.parseInt(timesplit[0]) * 3600 + Integer.parseInt(timesplit[1]) * 60 + Integer.parseInt(timesplit[2]);
                } else if (timesplit.length == 2) {
                    seconds = Integer.parseInt(timesplit[0]) * 60 + Integer.parseInt(timesplit[1]);
                } else if (timesplit.length == 1) {
                    seconds = Integer.parseInt(timesplit[0]);
                }
            } catch (Exception e) {
                String message = "无效格式字符串";
                log.debug(message, e);
            }

            return seconds;
        }

        /**
         * 提取金额,规则为只提取数字和点号,可以有点号，也可以没有 格式为2,0.2。可提取负数
         *
         * @param string         包含金额的字符串
         * @param default_result 出现错误时默认返回值
         * @return (double类型)金额的值
         */
        public Double extract_to_double(String string, Double default_result) {
            if (StringUtils.isBlank(string)) {
                return default_result;
            }
            try {
                Pattern compile = Pattern.compile("(\\d+\\.\\d+)|(\\d+)|(-\\d+\\.\\d+)|(-\\d+)");
                Matcher matcher = compile.matcher(string);
                boolean isfind = matcher.find();
                if (isfind) {
                    default_result = Double.valueOf(matcher.group());
                }
            } catch (Exception e) {
                String message = "提取数值失败 ".concat(string);
                log.debug(message, e);
            }
            return default_result;
        }


        /**
         * 合并json对象数组
         *
         * @param arrs json对象数组
         * @return 合并后的数组
         */
        public JsonObject[] jsonArrayCollect(JsonObject[]... arrs) {
            return (JsonObject[]) arrayCollect(arrs);
        }

        /**
         * 合并html文档数组
         *
         * @param arrs html文档数组
         * @return 合并后的数组
         */
        public Document[] htmlArrayCollect(Document[]... arrs) {
            return (Document[]) arrayCollect(arrs);
        }

        /**
         * 合并数组
         *
         * @param arrs 可变参数(需要合并的多个数组)
         * @return 合并后的数组
         */
        public Object[] arrayCollect(Object[][] arrs) {

            int size = 0;

            for (Object[] arr : arrs) {
                size += arr.length;
            }

            Object[] result = new Object[size];

            int cursor = 0;

            for (Object[] arr : arrs) {
                System.arraycopy(arr, 0, result, cursor, arr.length);
                cursor = cursor + arr.length;
            }

            return result;
        }

        /**
         * 从字符串获取html和json文档
         *
         * @param tool_html
         * @param tool_json
         * @param parameterMap
         * @param type         页面类型:basic,bill,transaction
         * @param htmls        html列表,如果有html文档,添加到此列表中
         * @param jsons        json对象列表,如果有json对象,添加到此列表中
         */
        public void getHtmlOrJson(
                Tool_Html tool_html,
                Tool_Json tool_json,
                Map<String, String[]> parameterMap,
                String type,
                List<Document> htmls,
                List<JsonObject> jsons) {

            for (String basic_str : parameterMap.get("basic")) {
                //如果字符串不为空
                if (basic_str != null && !(basic_str = basic_str.trim()).isEmpty()) {
                    //如果是json文件
                    if (basic_str.startsWith("{") || basic_str.startsWith("[")) {
                        JsonObject jsonObject = tool_json.parser_json(basic_str);
                        if (jsons != null) {
                            jsons.add(jsonObject);
                        }
                    } else {
                        Document document = tool_html.html(basic_str);
                        if (htmls != null) {
                            htmls.add(document);
                        }
                    }
                }
            }
        }

        /**
         * 选择一个有内容的字符串返回.
         *
         * @param ss 多个字符串
         * @return 第一个不为空的字符串
         */
        public String selectNotBlank(String... ss) {
            for (String s : ss) {
                if (StringUtils.isNoneBlank(s)) {
                    return s;
                }
            }
            return null;
        }

        /**
         * 选择一个不是空的对象返回.
         *
         * @param ts  多个对象
         * @param <T> 泛型
         * @return 第一个不为空的对象
         */
        @SuppressWarnings("unchecked")
        public <T> T selectNotNull(T... ts) {
            for (T t : ts) {
                if (t != null) {
                    return t;
                }
            }
            return null;
        }

        /**
         * 选择一个不是空的对象返回(延迟加载s).
         *
         * @param ss  多个对象生成器(延迟计算)
         * @param <T> 泛型
         * @return 第一个不为空的对象
         */
        @SuppressWarnings("unchecked")
        public <T> T selectNotNull(Supplier<T>... ss) {
            for (Supplier<T> s : ss) {
                T t = s.get();
                if (t != null) {
                    return t;
                }
            }
            return null;
        }

        /**
         * Integer转换为String
         */


        public String timestamp_to_string(SimpleDateFormat formatDate, Timestamp timestamp, String default_result) {
            if (formatDate == null || formatDate.toPattern().equals("")) {
                return default_result;
            }
            if (timestamp == null) {
                return default_result;
            }
            // 将hh改为HH
            if (formatDate.toPattern().contains("hh")) {
                log.debug("格式化时间的字符串出现：hh");

                formatDate.applyPattern(formatDate.toPattern().replace("hh", "HH"));
            }
            try {


                return formatDate.format(new Date(timestamp.getTime()));
            } catch (Exception e) {
                String message = "timestamp转 字符串 失败 ";
                log.debug(message, e);
            }
            return default_result;
        }


        /**
         * 整数字符串转date
         * <p>
         * "123" --> 123
         *
         * @param string         整数字符串
         * @param default_result 出错后默认返回值
         * @return String对象
         */
        public String longStr_to_dateStr(SimpleDateFormat simpleDateFormat, String string, String default_result) {
            if (string == null || string == "") {
                return default_result;
            }
            try {
                return simpleDateFormat.format(new Date(new BigDecimal(string).longValue()));
            } catch (Exception e) {
                String message = "字符串转 date 失败 ";
                log.debug(message, e);
            }
            return default_result;
        }


        /**
         * 获取连个字符串中间的值
         *
         * @param tag
         * @param begin
         * @param end
         * @return
         */
        public String substringBetween(String tag, String begin, String end) {
            try {
                if (StringUtils.isEmpty(end) && !StringUtils.isEmpty(begin)) {
                    return tag.substring(tag.indexOf(begin) + begin.length());
                }
                String result = StringUtils.substringBetween(tag, begin, end);
                return StringUtils.isEmpty(result) ? "" : result;
            } catch (Exception e) {
                log.info("err:", e);
            }
            return "";
        }

        /**
         * 截取字符串
         *
         * @param tag
         * @param begin
         * @param end
         * @return
         */
        public String substring(String tag, Integer begin, Integer end) {
            try {

                return tag.substring(begin, end);
            } catch (Exception e) {
                log.info("err:", e);
            }
            return "";
        }

       // private static final String DEFAULT_CODE = Encodings.UTF8.getValue();


        /**
         * base64解码
         *
         * @param bytes
         * @return
         */
//        public static String decode_base64(byte[] bytes) {
//            try {
//                return decode_base64(bytes, DEFAULT_CODE);
//            } catch (Exception e) {
//                log.error("err:{}", e);
//            }
//
//            return "";
//
//        }

        /**
         * base64解码
         *
         * @param bytes
         * @param charset
         * @return
         */
        public static String decode_base64(byte[] bytes, String charset) {
            try {
                byte[] encode = Base64.getDecoder().decode(bytes);
                return new String(encode, Charset.forName(charset));
            } catch (Exception e) {
                log.error("err:{}", e);
            }

            return "";

        }


        /**
         * base64解码
         *
         * @param string
         * @return
         */
//        public static String decode_base64_string(String string) {
//            try {
//                return decode_base64(string.getBytes(), DEFAULT_CODE);
//            } catch (Exception e) {
//                log.error("err:{}", e);
//            }
//
//            return "";
//
//        }


        public static String decode_base64_string(String string, String charset) {
            try {
                return decode_base64(string.getBytes(), charset);
            } catch (Exception e) {
                log.error("err:{}", e);
            }

            return "";

        }

        public static HSSFWorkbook get_workbook(String string) {
            HSSFWorkbook workbook = null;
            InputStream inputStream = null;
            try {
                inputStream = new ByteArrayInputStream(new BASE64Decoder().decodeBuffer(string));
                workbook = new HSSFWorkbook(inputStream);
                return workbook;
            } catch (Exception e) {
                log.error("err:{}", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return new HSSFWorkbook();
        }


//        public static String decode_user(String content) {
//            try {
//                return ThreeDESUtil.decryptThreeDESECB(content, ThreeDESUtil.CRAWLER_KEY);
//            } catch (Exception e) {
//                log.error("err:{}", e);
//            }
//            return "";
//
//        }

        public static String get_cell_value(HSSFRow hssfRow, Integer cellIndex) {
            try {
                return hssfRow.getCell(cellIndex).getStringCellValue();
            } catch (Exception e) {
                log.error("err:{}", e);
            }
            return "";
        }

        public static String get_opentime(Integer monthLength, String targetPattern) {
            try {
                if (monthLength == null ) {
                    return "";
                }
                SimpleDateFormat format = new SimpleDateFormat(targetPattern);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, -monthLength);


                return format.format(calendar.getTime());
            } catch (Exception e) {
                log.error("err:{}", e);
            }
            return "";
        }



        public String check_date(String string, String partten, Locale locale) {
            try {
                String[][] strings = new String[][]{
                        //yyyy-MM开头
                        {"^[\\d]{4}-[\\d]{1,2}", "yyyy-MM"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2}", "yyyy-MM-dd"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yyyy-MM-dd HH:mm"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{4}", "yyyy-MM-dd HHmm"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyyy-MM-dd HH:mm:ss"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{6}", "yyyy-MM-dd HHmmss"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yyyy-MM-dd HH.mm"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyy-MM-dd HH.mm.ss"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yyyy-MM-dd HH/mm"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyyy-MM-dd HH/mm/ss"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yyyy-MM-dd HH时mm分"},
                        {"^[\\d]{4}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyy-MM-dd HH时mm分ss秒"},
                        //yy-MM开头
                        {"^[\\d]{2}-[\\d]{1,2}", "yy-MM"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2}", "yy-MM-dd"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yy-MM-dd HH:mm"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yy-MM-dd HH:mm:ss"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{4}", "yy-MM-dd HHmm"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{6}", "yy-MM-dd HHmmss"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yy-MM-dd HH.mm"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yy-MM-dd HH.mm.ss"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yy-MM-dd HH/mm"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yy-MM-dd HH/mm/ss"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yy-MM-dd HH时mm分"},
                        {"^[\\d]{2}-[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yy-MM-dd HH时mm分ss秒"},
                        //MM-dd开头
                        {"^[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "MM-dd HH:mm:ss"},
                        {"^[\\d]{1,2}-[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "MM-dd HH:mm"},
                        {"^[\\d]{1,2}-[\\d]{1,2} [\\d]{6}", "MM-dd HHmmss"},
                        {"^[\\d]{1,2}-[\\d]{1,2} [\\d]{4}", "MM-dd HHmm"},
                        {"^[\\d]{1,2}-[\\d]{1,2}", "MM-dd"},
                        //yyyyMM
                        {"^[\\d]{6}", "yyyyMM"},
                        {"^[\\d]{8}", "yyyyMMdd"},
                        {"^[\\d]{8} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyyyMMdd HH:mm:ss"},
                        {"^[\\d]{8} [\\d]{1,2}:[\\d]{1,2}", "yyyyMMdd HH:mm"},
                        {"^[\\d]{8} [\\d]{1,2}:[\\d]{6}", "yyyyMMdd HHmmss"},
                        {"^[\\d]{8} [\\d]{1,2}:[\\d]{4}", "yyyyMMdd HHmm"},
                        {"^[\\d]{8} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyyMMdd HH.mm.ss"},
                        {"^[\\d]{8} [\\d]{1,2}\\.[\\d]{1,2}", "yyyyMMdd HH.mm"},
                        {"^[\\d]{8} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyyMMdd HH时mm分ss秒"},
                        {"^[\\d]{8} [\\d]{1,2}时[\\d]{1,2}分", "yyyyMMdd HH时mm分"},
                        {"^[\\d]{8} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyyyMMdd HH/mm/ss"},
                        {"^[\\d]{8} [\\d]{1,2}/[\\d]{1,2}", "yyyyMMdd HH/mm"},
                        {"^[\\d]{14}", "yyyyMMddHHmmss"},
                        {"^[\\d]{12}", "yyyyMMddHHmm"},
                        //yyMM
                        {"^[\\d]{4}", "yyMM"},
                        {"^[\\d]{6}", "yyMMdd"},
                        {"^[\\d]{6} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyMMdd HH:mm:ss"},
                        {"^[\\d]{6} [\\d]{1,2}:[\\d]{1,2}", "yyMMdd HH:mm"},
                        {"^[\\d]{6} [\\d]{1,2}:[\\d]{6}", "yyMMdd HHmmss"},
                        {"^[\\d]{6} [\\d]{1,2}:[\\d]{4}", "yyMMdd HHmm"},
                        {"^[\\d]{6} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyMMdd HH.mm.ss"},
                        {"^[\\d]{6} [\\d]{1,2}\\.[\\d]{1,2}", "yyMMdd HH.mm"},
                        {"^[\\d]{6} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyMMdd HH时mm分ss秒"},
                        {"^[\\d]{6} [\\d]{1,2}时[\\d]{1,2}分", "yyMMdd HH时mm分"},
                        {"^[\\d]{6} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyMMdd HH/mm/ss"},
                        {"^[\\d]{6} [\\d]{1,2}/[\\d]{1,2}", "yyMMdd HH/mm"},
                        {"^[\\d]{12}", "yyMMddHHmmss"},
                        {"^[\\d]{10}", "yyMMddHHmm"},
                        //yyyy/MM
                        {"^[\\d]{4}/[\\d]{1,2}", "yyyy/MM"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2}", "yyyy/MM/dd"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyyy/MM/dd HH:mm:ss"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yyyy/MM/dd HH:mm"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{6}", "yyyy/MM/dd HHmmss"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{4}", "yyyy/MM/dd HHmm"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyyy/MM/dd HH/mm/ss"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yyyy/MM/dd HH/mm"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyy/MM/dd HH.mm.ss"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yyyy/MM/dd HH.mm"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyy/MM/dd HH时mm分ss秒"},
                        {"^[\\d]{4}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yyyy/MM/dd HH时mm分"},
                        //yy/MM
                        {"^[\\d]{2}/[\\d]{1,2}", "yy/MM"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2}", "yy/MM/dd"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yy/MM/dd HH:mm:ss"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yy/MM/dd HH:mm"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yy/MM/dd HH/mm/ss"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yy/MM/dd HH/mm"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yy/MM/dd HH.mm.ss"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yy/MM/dd HH.mm"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yy/MM/dd HH时mm分ss秒"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yy/MM/dd HH时mm分"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{6}", "yy/MM/dd HHmmss"},
                        {"^[\\d]{2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{4}", "yy/MM/dd HHmm"},
                        //MM/dd
                        {"^[\\d]{1,2}/[\\d]{1,2}", "MM/dd"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "MM/dd HH:mm:ss"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "MM/dd HH:mm"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "MM/dd HH/mm/ss"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "MM/dd HH/mm"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "MM/dd HH.mm.ss"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "MM/dd HH.mm"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "MM/dd HH时mm分ss秒"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "MM/dd HH时mm分"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{6}", "MM/dd HHmmss"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{4}", "MM/dd HHmm"},
                        //yyyy.MM
                        {"^[\\d]{4}\\.[\\d]{1,2}", "yyyy.MM"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyy.MM.dd"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyyy.MM.dd HH:mm:ss"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yyyy.MM.dd HH:mm"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyyy.MM.dd HH/mm/ss"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yyyy.MM.dd HH/mm"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyy.MM.dd HH.mm.ss"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yyyy.MM.dd HH.mm"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyy.MM.dd HH时mm分ss秒"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yyyy.MM.dd HH时mm分"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{6}", "yyyy.MM.dd HHmmss"},
                        {"^[\\d]{4}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{4}", "yyyy.MM.dd HHmm"},
                        //yy.MM
                        {"^[\\d]{2}\\.[\\d]{1,2}", "yy.MM"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yy.MM.dd"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yy.MM.dd HH:mm:ss"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "yy.MM.dd HH:mm"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yy.MM.dd HH/mm/ss"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "yy.MM.dd HH/mm"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yy.MM.dd HH.mm.ss"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "yy.MM.dd HH.mm"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yy.MM.dd HH时mm分ss秒"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "yy.MM.dd HH时mm分"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{6}", "yy.MM.dd HHmmss"},
                        {"^[\\d]{2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{4}", "yy.MM.dd HHmm"},
                        //MM.dd
                        {"^[\\d]{1,2}\\.[\\d]{1,2}", "MM.dd"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "MM.dd HH:mm:ss"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}:[\\d]{1,2}", "MM.dd HH:mm"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "MM.dd HH/mm/ss"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "MM.dd HH/mm"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "MM.dd HH.mm.ss"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}\\.[\\d]{1,2}", "MM.dd HH.mm"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "MM.dd HH时mm分ss秒"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}时[\\d]{1,2}分", "MM.dd HH时mm分"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{6}", "MM.dd HHmmss"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{4}", "MM.dd HHmm"},
                        //yyyy年MM月
                        {"^[\\d]{4}年[\\d]{1,2}月", "yyyy年MM月"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日", "yyyy年MM月dd日"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yyyy年MM月dd日 HH:mm:ss"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}", "yyyy年MM月dd日 HH:mm"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yyyy年MM月dd日 HH/mm/ss"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}", "yyyy年MM月dd日 HH/mm"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyy年MM月dd日 HH时mm分ss秒"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分", "yyyy年MM月dd日 HH时mm分"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yyyy年MM月dd日 HH.mm.ss"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}", "yyyy年MM月dd日 HH.mm"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{6}", "yyyy年MM月dd日 HHmmss"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{4}", "yyyy年MM月dd日 HHmm"},

                        //yy年MM月
                        {"^[\\d]{2}年[\\d]{1,2}月", "yy年MM月"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日", "yy年MM月dd日"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "yy年MM月dd日 HH:mm:ss"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}", "yy年MM月dd日 HH:mm"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "yy年MM月dd日 HH/mm/ss"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}", "yy年MM月dd日 HH/mm"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yy年MM月dd日 HH时mm分ss秒"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分", "yy年MM月dd日 HH时mm分"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "yy年MM月dd日 HH.mm.ss"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}", "yy年MM月dd日 HH.mm"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{6}", "yy年MM月dd日 HHmmss"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日 [\\d]{4}", "yy年MM月dd日 HHmm"},
                        //MM月dd日
                        {"^[\\d]{1,2}月[\\d]{1,2}日", "MM月dd日"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2}", "MM月dd日 HH:mm:ss"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}:[\\d]{1,2}", "MM月dd日 HH:mm"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2}", "MM月dd日 HH/mm/ss"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}/[\\d]{1,2}", "MM月dd日 HH/mm"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "MM月dd日 HH时mm分ss秒"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}时[\\d]{1,2}分", "MM月dd日 HH时mm分"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2}", "MM月dd日 HH.mm.ss"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{1,2}\\.[\\d]{1,2}", "MM月dd日 HH.mm"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{6}", "MM月dd日 HHmmss"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日 [\\d]{4}", "MM月dd日 HHmm"},
                        //MM/dd
                        {"^[\\d]{1,2}/[\\d]{1,2}", "MM/dd"},
                        {"^[\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH:mm:ss MM/dd"},
                        {"^[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH:mm MM/dd"},
                        {"^[\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH/mm/ss MM/dd"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH/mm MM/dd"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH.mm.ss MM/dd"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}", "HH.mm MM/dd"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒 [\\d]{1,2}/[\\d]{1,2}", "HH时mm分ss秒 MM/dd"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分 [\\d]{1,2}/[\\d]{1,2}", "HH时mm分 MM/dd"},

                        //MM/dd/yyyy
                        {"^[\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "MM/dd/yyyy"},
                        {"^[\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH:mm:ss MM/dd/yyyy"},
                        {"^[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH:mm MM/dd/yyyy"},
                        {"^[\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH/mm/ss MM/dd/yyyy"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH/mm MM/dd/yyyy"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH.mm.ss MM/dd/yyyy"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH.mm MM/dd/yyyy"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒 [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH时mm分ss秒 MM/dd/yyyy"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分 [\\d]{1,2}/[\\d]{1,2}/[\\d]{4}", "HH时mm分 MM/dd/yyyy"},
                        //MM/dd/yy
                        {"^[\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "MM/dd/yyyy"},
                        {"^[\\d]{1,2}:[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH:mm:ss MM/dd/yy"},
                        {"^[\\d]{1,2}:[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH:mm MM/dd/yy"},
                        {"^[\\d]{1,2}/[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH/mm/ss MM/dd/yy"},
                        {"^[\\d]{1,2}/[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH/mm MM/dd/yy"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH.mm.ss MM/dd/yy"},
                        {"^[\\d]{1,2}\\.[\\d]{1,2} [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH.mm MM/dd/yy"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒 [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH时mm分ss秒 MM/dd/yy"},
                        {"^[\\d]{1,2}时[\\d]{1,2}分 [\\d]{1,2}/[\\d]{1,2}/[\\d]{2}", "HH时mm分 MM/dd/yy"},


                        //yyyy年MM月dd日HH时mm分ss秒
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yyyy年MM月dd日HH时mm分ss秒"},
                        {"^[\\d]{4}年[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分", "yyyy年MM月dd日HH时mm分"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "yy年MM月dd日HH时mm分ss秒"},
                        {"^[\\d]{2}年[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分", "yy年MM月dd日HH时mm分"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分[\\d]{1,2}秒", "MM月dd日HH时mm分ss秒"},
                        {"^[\\d]{1,2}月[\\d]{1,2}日[\\d]{1,2}时[\\d]{1,2}分", "MM月dd日HH时mm分"},


                };

                boolean flag = false;

                if (!StringUtils.isEmpty(string) && !StringUtils.isEmpty(partten)) {
                    for (int i = 0; i < strings.length; i++) {
                        Pattern pattern = Pattern.compile(strings[i][0]);
                        Matcher matcher = pattern.matcher(string);
                        flag = matcher.matches();
                        if (flag) {
                            String date = matcher.group(0);
                            if (strings[i][1].contains("y")) {
                                return get_format_date_check(date, strings[i][1], partten, locale);
                            }
                            return get_true_year_check(date, strings[i][1], partten, locale);
                        }
                    }

                }
            } catch (Exception e) {
                log.info(e.getMessage());
            }


            return "";
        }


        public boolean find(String reg, String string) {
            try {
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(string);
                return matcher.find();
            } catch (Exception e) {
                log.info(e.getMessage());
            }

            return false;

        }

        public boolean matches(String reg, String string) {
            try {
                Pattern pattern = Pattern.compile(reg);
                Matcher matcher = pattern.matcher(string);
                return matcher.matches();
            } catch (Exception e) {
                log.info(e.getMessage());
            }

            return false;

        }


        /**
         * 判断日期前后
         *
         * @param theData
         * @param thePattern
         * @param other
         * @param otherPattern
         * @return
         */
        public boolean afterDate(String theData, String thePattern, String other, String otherPattern) {
            try {
                return to_timestamp(thePattern, theData, new Timestamp(System.currentTimeMillis())).
                        after(to_timestamp(otherPattern, other, new Timestamp(System.currentTimeMillis())));
            } catch (Exception e) {
                log.error("token:{},err:{}", crawl_token, e);
            }
            return false;
        }


        /**
         * 判断日期前后
         *
         * @param theData
         * @param thePattern
         * @param other
         * @param otherPattern
         * @return
         */
        public boolean equalsDate(String theData, String thePattern, String other, String otherPattern) {
            try {
                return to_timestamp(thePattern, theData, new Timestamp(System.currentTimeMillis())).
                        equals(to_timestamp(otherPattern, other, new Timestamp(System.currentTimeMillis())));
            } catch (Exception e) {
                log.error("token:{},err:{}", crawl_token, e);
            }
            return false;
        }





        public Map<String, String> getCreditCardMapKeyByCardNum(Map<String, Map<String, String>> mapKey, String cardNum) {
            Map<String, String> stringMap = new HashMap<>();
            if (mapKey.get(cardNum) == null) {
                mapKey.put(cardNum, stringMap);
            } else {
                stringMap = mapKey.get(cardNum);
            }

            return stringMap;
        }



        public String getDistanceDate(String string, String pattern, Integer date) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(pattern);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sourceFormat.parse(string));
                calendar.add(Calendar.DATE, date);
                return sourceFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "获取距离当前月 失败".concat(string);
                log.debug(message, e);
            }

            return "";
        }


        public String getDistanceYear(String string, String pattern, Integer year) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(pattern);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sourceFormat.parse(string));
                calendar.add(Calendar.YEAR, year);
                return sourceFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "获取距离当前月 失败".concat(string);
                log.debug(message, e);
            }

            return "";
        }

        public String getDistanceMonth(String string, String pattern, Integer month) {
            try {
                SimpleDateFormat sourceFormat = new SimpleDateFormat(pattern);

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(sourceFormat.parse(string));
                calendar.add(Calendar.MONTH, month);
                return sourceFormat.format(calendar.getTime());
            } catch (Exception e) {
                String message = "获取距离当前月 失败".concat(string);
                log.debug(message, e);
            }

            return "";
        }

        /**
         * 根据正则表达式，提取特定字符
         *
         * @param reg
         * @param string
         * @return
         */
        public String getStringByReg(String reg, String string) {
            if (StringUtils.isEmpty(string)) {
                return string;
            }

            String regString = "";
            try {
                for (String regStr : string.split("")) {
                    if (matches(reg, regStr)) {
                        regString += regStr;
                    }
                }

                return regString;
            } catch (Exception e) {
                log.error("获取字符串失败:{}", e);
            }

            return string;
        }


        /**
         * @param miniTime    较小年月
         * @param miniPattern
         * @param maxTime     较大年月
         * @param maxPattern
         * @return
         */
        public Integer get_compare_month(String miniTime, String miniPattern, String maxTime, String maxPattern) {
            if (StringUtils.isEmpty(miniTime) || StringUtils.isEmpty(maxTime)) {
                return null;
            }
            try {

                SimpleDateFormat otherFormat = new SimpleDateFormat(miniPattern);
                Calendar otherCalendar = Calendar.getInstance();
                otherCalendar.setTime(otherFormat.parse(miniTime));


                SimpleDateFormat format = new SimpleDateFormat(maxPattern);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(format.parse(maxTime));


                return (calendar.get(Calendar.YEAR) - otherCalendar.get(Calendar.YEAR)) * 12 + (calendar.get(Calendar.MONTH) - otherCalendar.get(Calendar.MONTH));
            } catch (Exception e) {
                String message = "获取相差月失败";
                log.debug(message, e);
            }

            return null;

        }


        public static void main(String[] args) {
            Tools tools = new Tools("", "");
//        tools.get_format_date("2019年09月03日", "yyyy年MM月dd日", "yyyy-MM-dd",Locale.US);

            tools.get_end_date("20190624000000", "yyyy年MM月dd日", "yyyy-MM");

//        String string=tools.getStringByReg("[\\u4e00-\\u9fa5]","2年fdf后8");
//
//
//        tools.get_compare_month("2018-12-01","yyyy-MM-dd","2019-03-02","yyyy-MM-dd");
//
//        get_opentime(null,"yyyy-MM-dd");
        }

}
