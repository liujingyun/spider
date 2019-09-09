package com.xinyan.trust;

import com.google.gson.JsonObject;
import com.xinyan.trust.entity.FieldMeaning;
import com.xinyan.trust.propessor.WeiBoHotProcessor;
import com.xinyan.trust.service.ICompareService;
import com.xinyan.trust.service.impl.ContainsServiceImpl;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import us.codecraft.webmagic.Spider;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName Test
 * @Description
 * @Author jingyun_liu
 * @Date 2019/7/24 14:45
 * @Version V1.0
 **/
@SpringBootTest
public class Test {
    @Autowired
    ICompareService containsService;
    @org.junit.Test
    public void test1(){
        Map<Integer,List<String>> map = new HashMap<>();
        List<String> list1 = new ArrayList<>();
        list1.add("套餐");
        list1.add("包");
        list1.add("卡");
        map.put(1,list1);
        List<String> list = new ArrayList<>();
        list.add("语音");
        list.add("通话");
        list.add("短信");
        map.put(-1,list);

        containsService = new ContainsServiceImpl();
        containsService.getResultBoolean(map,"语音包卡套餐");
    }
    @org.junit.Test
    public void test2(){
        Map<Integer,String> map = new TreeMap<>();
        String def = "def";
        for(int i = 0; i<= 1000; i++){
            map.put(new Random().nextInt(10000)+1,def);
        }

        System.out.println(map);
    }
}
