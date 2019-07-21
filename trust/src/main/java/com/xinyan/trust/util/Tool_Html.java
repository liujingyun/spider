package com.xinyan.trust.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName Tool_Html
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/19 13:48
 * @Version V1.0
 **/
@Slf4j
public class Tool_Html {
    private String website = "";
    private String crawl_token = "";

    private static String ARGS_SEPARATOR = " ";
    private CommandLineParser parser = new GnuParser();
    public final Options options = new Options();

    private static final Option option_select = new Option("select", "select", true, "根据 select 查找节点");
    private static final Option option_id = new Option("id", "id", true, "根据 id 查找节点");
    private static final Option option_tag = new Option("tag", "tag", true, "根据 tag 查找节点");
    private static final Option option_class = new Option("class", "class", true, "根据 class 查找节点");

    private static final Option option_next = new Option("next", "next", false, "根据 next 查找节点");
    private static final Option option_pre = new Option("pre", "pre", false, "根据 pre 查找节点");
    private static final Option option_nextsibling = new Option("nextsibling", "nextsibling", false, "根据 next 查找节点");
    private static final Option option_presibling = new Option("presibling", "presibling", false, "根据 pre 查找节点");
    private static final Option option_parent = new Option("parent", "parent", false, "根据 parent 查找节点");
    private static final Option option_last = new Option("last", "last", false, "根据 last 查找节点");
    private static final Option option_first = new Option("first", "first", false, "根据 first 查找节点");
    private static final Option option_index = new Option("index", "index", true, "根据 index 查找节点");

    private static final Option option_text = new Option("text", "text", false, "根据 text 查找值");
    private static final Option option_owntext = new Option("owntext", "owntext", false, "根据 text 查找值");
    private static final Option option_attr = new Option("attr", "attr", true, "根据 attr 查找值");
    private static final Option option_html = new Option("html", "html", false, "根据html 查找值");
    private static final Option option_sibling = new Option("sibling", "sibling", false, "根据 sib 查找节点");
    private static final Option option_children = new Option("children", "children", false, "根据 children 查找节点");

    public Tool_Html(String website, String crawl_token) {
        this.website = website;
        this.crawl_token = crawl_token;

        options.addOption(option_select);
        options.addOption(option_id);
        options.addOption(option_tag);
        options.addOption(option_class);
        options.addOption(option_nextsibling);
        options.addOption(option_presibling);

        options.addOption(option_next);
        options.addOption(option_pre);
        options.addOption(option_parent);
        options.addOption(option_last);
        options.addOption(option_first);

        options.addOption(option_index);
        options.addOption(option_text);
        options.addOption(option_attr);
        options.addOption(option_owntext);
        options.addOption(option_html);
        options.addOption(option_sibling);
        options.addOption(option_children);
    }

    // ///////////////////////////////////////////////////////////////公有方法/////////////////////////////////////////////////////////////////////////////

    /**
     * 获取安全方法链的 elements
     */
    public Elements elements(String attrbute, Element doc, String aql) {
        log.trace("#获取 html 节点组 safety ".concat(aql));
        Elements elements = new Elements();
        String page = doc == null ? "" : doc.baseUri();
        try {
            Object tmp_result = analysis(doc, aql);
            if (tmp_result != null) {
                if (tmp_result instanceof Elements) {
                    elements = (Elements) tmp_result;
                } else if (tmp_result instanceof Element) {
                    elements.add((Element) tmp_result);
                }
            }

        } catch (Exception e) {
            //Tool_Collect.collect(website, crawl_token, page, Level.WARN.name(), "解析 html 节点数组 ".concat(attrbute).concat("错误:").concat(aql), e);
            log.debug("解析 html 节点数组 ".concat(attrbute).concat("错误:").concat(aql), e);
        }
        return elements;
    }

    public Elements elements2(String attrbute, Element doc, String aql) {
        return elements(attrbute, doc, parse2(aql, false));
    }

    /** 获取安全方法链的值，方法中出现错误,或者返回值为空时返回传入的 value */
    /**
     * 根据jsoup 语句
     *
     * @param attrbute       记录log 日志中的提示信息
     * @param doc            要解析的文档
     * @param aql            解析文档的 jsoup 分析语句
     * @param default_result 如果根据 分析语句没有获得结果所返回的默认值
     * @return
     */
    public String value(String attrbute, Element doc, String aql, String default_result) {
        log.trace("#获取 html 节点 safety ".concat(aql));
        Object tmp_result = null;
        String page = doc == null ? "" : doc.baseUri();
        try {
            tmp_result = analysis(doc, aql);
            if (tmp_result == null) {
                //Tool_Collect.collect(website, crawl_token, page, Level.WARN.name(), "解析 html 节点 ".concat(attrbute).concat("为空:").concat(aql), null);
                log.debug("解析 html 节点 ".concat(attrbute).concat("为空:").concat(aql));
            }
        } catch (Exception e) {
            //Tool_Collect.collect(website, crawl_token, page, Level.WARN.name(), "解析 html 节点 ".concat(attrbute).concat("错误:").concat(aql), e);
            log.debug("解析 html 节点 ".concat(attrbute).concat("错误:").concat(aql), e);
        }
        default_result = (tmp_result == null || tmp_result.toString().isEmpty()) ? default_result : StringUtils.trim(tmp_result.toString()).replaceAll("\"", "");
        return default_result;
    }

    /**
     * 获取安全方法链的值，方法中出现错误,或者返回值为空时返回传入的 value
     */
    public String value_with_error(String attrbute, Element doc, String aql, String default_result) {
        log.trace("#获取 html 节点 safety ".concat(aql));
        Object tmp_result = null;
        String page = doc == null ? "" : doc.baseUri();
        try {
            tmp_result = analysis(doc, aql);
            if (tmp_result == null) {
                log.debug("解析 html 节点 ".concat(attrbute).concat("为空:").concat(aql));
            }
        } catch (Exception e) {
            log.debug("解析 html 节点 ".concat(attrbute).concat("错误:").concat(aql), e);
        }
        default_result = (tmp_result == null) ? default_result : StringUtils.trim(tmp_result.toString()).replaceAll("\"", "");
        return default_result;
    }


    /**
     * 如果在value方法中直接使用match匹配,可以直接获取匹配的值
     * String name = html.match_value("解析姓名", doc, "-select b:matches(姓名:(.*?),身份证:(\\d+)) -index 0 -text", null, 1);
     * String id = html.match_value("解析身份证号", doc, "-select b:matches(姓名:(.*?),身份证:(\\d+)) -index 0 -text", null, 2);
     *
     * @param attrbute       日志
     * @param doc            文档
     * @param aql            表达式
     * @param default_result 返回的默认值
     * @param group          正则匹配括号的组
     * @return 匹配结果
     */
    public String match_value(String attrbute, Element doc, String aql, String default_result, int group) {
        String value = value(attrbute, doc, aql, null);
        if (value != null) {

            Matcher matcher = Pattern.compile(":matches(Own)?\\((.*?)\\) -").matcher(aql);
            if (matcher.find()) {
                String regex = matcher.group(2);
                Matcher needM = Pattern.compile(regex).matcher(value);
                if (needM.find()) {
                    return needM.group(group);
                }
            }
        }

        return default_result;
    }

    /**
     * value 方法二次开发
     * 增加功能:
     * <pre>
     * -match abc = -select :matchesOwn(^\s*abc\s*$) -index
     * -parent i = -parent -parent ... -parent
     * -next i= -next -next ... -next
     * -first div = -select div -index 0
     * </pre>
     */
    public String value2(String attrbute, Element doc, String aql, String default_result) {
        return value(attrbute, doc, parse2(aql), default_result);
    }

    public String parse2(String aql) {
        return parse2(aql, true);
    }

    public String parse2(String aql, boolean needText) {
        //非空处理
        if (aql == null){
            return null;
        } else {
            aql = aql.trim();
        }

        //处理省略的 -text
        if (!aql.endsWith("-text") && aql.lastIndexOf("-attr") == -1 && needText){
            aql = aql + " -text";
        }

        //处理 -match
        Matcher mMatch = pMatch.matcher(aql);
        aql = mMatch.replaceAll("-select :matchesOwn(^\\\\s*$1\\\\s*\\$) -index 0");

        //处理 -parent x
        Matcher parentMatcher = parentMatch.matcher(aql);
        while (parentMatcher.find()) {
            String all = parentMatcher.group();
            String times = parentMatcher.group(1);
            int i = Integer.parseInt(times);
            String repeat = StringUtils.repeat("-parent ", i);
            repeat = repeat.substring(0, repeat.length() - 1);
            aql = aql.replace(all, repeat);
        }
        //处理 -next x
        Matcher nextMatcher = nextMatch.matcher(aql);
        while (nextMatcher.find()) {
            String all = nextMatcher.group();
            String times = nextMatcher.group(1);
            int i = Integer.parseInt(times);
            String repeat = StringUtils.repeat("-next ", i);
            repeat = repeat.substring(0, repeat.length() - 1);
            aql = aql.replace(all, repeat);
        }

        //处理 -first xxx
        Matcher firstMatcher = firstMatch.matcher(aql);
        aql = firstMatcher.replaceAll("-select $1 -index 0");

        //处理 -i xxx
        Matcher indexMatcher = indexMatch.matcher(aql);
        while (indexMatcher.find()) {
            String all = indexMatcher.group();
            String index = indexMatcher.group(1);
            String selector = indexMatcher.group(2);
            aql = aql.replace(all, String.format("-select %s -index %s", selector, index));
        }

        System.out.println("extend aql|" + aql + "|");

        return aql;
    }

    Pattern pMatch = Pattern.compile("-match ([^\\s]+)");
    Pattern parentMatch = Pattern.compile("-parent (\\d+)");
    Pattern nextMatch = Pattern.compile("-next (\\d+)");
    Pattern firstMatch = Pattern.compile("-first ([^-][^\\s]*)");
    Pattern indexMatch = Pattern.compile("-(\\d+) ([^-][^\\s]*)");

    // ///////////////////////////////////////////////////////////////私有方法//////////////////////////////////////////////////////////////////////

    /**
     * 解析 aql
     *
     * @param doc 要解析的文档
     * @param aql 用于解析的jsoup 语句
     * @return 解析结果
     * @throws Exception
     */
    private Object analysis(Element doc, String aql) throws Exception {
        Object result = doc;
        if (aql == null || aql.isEmpty()) {
            return null;
        }
        aql = StringUtils.trim(aql).substring(1);
        String[] split = aql.split(" -");
        for (String string : split) {
            String args_string = "-".concat(string);
            if (result instanceof Element) {
                result = by_element((Element) result, args_string);
            } else if (result instanceof Elements) {
                result = by_elements((Elements) result, args_string);
            } else {
                return null;
            }
        }
        return result;
    }

    /**
     * 根据 Element 获取值
     *
     * @param element
     * @param args_string
     * @return
     * @throws ParseException
     */
    private Object by_element(Element element, String args_string) throws ParseException {
        args_string = StringUtils.trim(args_string);
        CommandLine parse = parser.parse(options, args_string.split(ARGS_SEPARATOR));
        if (parse.hasOption(option_tag.getOpt())) {
            return element.getElementsByTag(parse.getOptionValue(option_tag.getOpt()));
        } else if (parse.hasOption(option_class.getOpt())) {
            return element.getElementsByClass(parse.getOptionValue(option_class.getOpt()));
        } else if (parse.hasOption(option_id.getOpt())) {
            return element.getElementById(parse.getOptionValue(option_id.getOpt()));
        } else if (parse.hasOption(option_select.getOpt())) {
            return element.select(parse.getOptionValue(option_select.getOpt()));
        } else if (parse.hasOption(option_parent.getOpt())) {
            return element.parent();
        } else if (parse.hasOption(option_attr.getOpt())) {
            return element.attr(parse.getOptionValue(option_attr.getOpt()));
        } else if (parse.hasOption(option_next.getOpt())) {
            return element.nextElementSibling();
        } else if (parse.hasOption(option_pre.getOpt())) {
            return element.previousElementSibling();
        } else if (parse.hasOption(option_text.getOpt())) {
            return element.text();
        } else if (parse.hasOption(option_owntext.getOpt())) {
            return element.ownText();
        } else if (parse.hasOption(option_nextsibling.getOpt())) {
            return element.nextSibling();
        } else if (parse.hasOption(option_presibling.getOpt())) {
            return element.previousSibling();
        } else if (parse.hasOption(option_html.getOpt())) {
            return element.html();
        } else if (parse.hasOption(option_sibling.getOpt())) {
            return element.siblingElements();
        } else if (parse.hasOption(option_children.getOpt())) {
            return element.children();
        } else {
            log.debug("#方法不能识别该参数:".concat(args_string));
            return null;
        }
    }

    /**
     * 根据 Elements 获取值
     *
     * @param elements
     * @param args_string
     * @return
     * @throws ParseException
     */
    private Object by_elements(Elements elements, String args_string) throws ParseException {
        args_string = StringUtils.trim(args_string);
        if (elements.isEmpty()) {
            return null;
        }
        CommandLine parse = parser.parse(options, args_string.split(ARGS_SEPARATOR));
        if (parse.hasOption(option_select.getOpt())) {
            return elements.select(parse.getOptionValue(option_select.getOpt()));
        } else if (parse.hasOption(option_first.getOpt())) {
            return elements.first();
        } else if (parse.hasOption(option_last.getOpt())) {
            return elements.last();
        } else if (parse.hasOption(option_index.getOpt())) {
            return elements.get(Integer.parseInt(parse.getOptionValue(option_index.getOpt())));
        } else if (parse.hasOption(option_text.getOpt())) {
            return elements.text();
        } else if (parse.hasOption(option_attr.getOpt())) {
            return elements.attr(parse.getOptionValue(option_attr.getOpt()));
        } else {
            log.debug("#方法不能识别该参数:".concat(args_string));
            return null;
        }
    }
}
