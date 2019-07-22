package com.xinyan.trust.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.net.URL;

/**
 * @ClassName JsUtil
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/19 14:04
 * @Version V1.0
 **/
public class JsUtil {
    /**
     * js 文件操作对象。
     */
    private Invocable inv=null;


    /**
     * 功能：构造函数。(文件路径)
     * @author liujingyun
     * @param jsFilePaths 文件路径下的js文件全路径，可以同时传入很多js路径。
     * @throws ScriptException 读取js文件异常。
     * @throws FileNotFoundException  js文件没有找到。
     */
    public JsUtil(String... jsFilePaths) throws FileNotFoundException, ScriptException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByMimeType("text/javascript");
        for(String filePath : jsFilePaths){
            engine.eval(new FileReader(filePath));
        }
        inv = (Invocable) engine;
    }

    /**
     * 功能：构造函数。(网络地址)
     * @author liujingyun
     * @param jsUrls js文件在网络上的全路径,可以同时传入多个JS的URL。
     * @throws ScriptException 读取js文件异常。
     * @throws IOException  从网路上加载js文件异常。
     */
    public JsUtil(URL... jsUrls) throws ScriptException, IOException {
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByMimeType("text/javascript");
        //从网络读取js文件流
        for(URL url: jsUrls){
            InputStreamReader isr=new InputStreamReader(url.openStream());
            BufferedReader br=new BufferedReader(isr);
            engine.eval(br);
        }
        inv = (Invocable) engine;
    }

    /**
     * 功能：调用js中的顶层程序和函数。
     * @param functionName js顶层程序和函数名。
     * @author liujingyun
     * @return 程序或函数所返回的值
     * @throws NoSuchMethodException 如果不存在具有给定名称或匹配参数类型的方法。
     * @throws ScriptException 如果在调用方法期间发生错误。
     */
    public Object invokeFunction(String functionName,Object... args) throws ScriptException, NoSuchMethodException{
        return inv.invokeFunction(functionName,args);
    }

    public static void main(String[] args) throws FileNotFoundException, ScriptException {
        JsUtil jsUtil = new JsUtil("src/main/resources/static/test.js");
        try {
            Object test = jsUtil.invokeFunction("test",2,3);
            System.out.println(test);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
