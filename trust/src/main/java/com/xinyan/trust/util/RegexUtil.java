package com.xinyan.trust.util;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class RegexUtil {
    /**
     * 中文字母加上逗号随意组合,如:电信1区,电信2区
     */
    public static final String MULTI_WORD_CN = "([\\u4e00-\\u9fa5\\w]+,*)+";
    

    private RegexUtil() {
        throw new IllegalAccessError("Utility class");
    }

    public static boolean isMatch(String regex, String input) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        return matcher.find();
    }

    public static String getValue(String regex, String text) {
        return getValue(regex, text, 0);
    }

    /**
     * @param regex
     * @param text
     * @param group
     * @return
     */
    public static String getValue(String regex, String text, int group) {
        try {
            Matcher matcher = Pattern.compile(regex).matcher(text);
            if (matcher.find()) {
                return matcher.group(group)==null?"":matcher.group(group);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "";
        }
        return "";
    }

    /**
     * 返回
     *
     * @param regex
     * @param text
     * @param group
     * @return
     */
    public static String[] getValue(String regex, String text, int... group) {
        String[] val = new String[group.length];
        try {
            Matcher matcher = Pattern.compile(regex).matcher(text);
            if (matcher.find()) {
                for (int i = 0; i < group.length; i++) {
                    val[i] = matcher.group(group[i]);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return val;
    }

    // 默认是第1组的所有数据
    public static List<String> getMatches(String regex, String text) {
        return getMatches(regex, text, 1);
    }

    public static List<String> getMatches(String regex, String text, int group) {
        List<String> matches = new ArrayList<String>();
        Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            matches.add(matcher.group(group));
        }
        return matches;
    }

}
