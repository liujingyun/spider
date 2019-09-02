package com.xinyan.trust.service.impl;

import com.xinyan.trust.service.ICompareService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName ContainsServiceImpl
 * @Description
 * @Author jingyun_liu
 * @Date 2019/8/2 10:38
 * @Version V1.0
 **/
@Service
public class ContainsServiceImpl implements ICompareService {



    @Override
    public boolean getResultBoolean(Map<Integer,List<String>> map,String code) {
        if(StringUtils.isBlank(code) || map.isEmpty()){
            return true;
        }
        Map<Integer,List<String>> mapList = new TreeMap<>(map);
        //对于integer进行排序
        Set<Integer> treeSet = mapList.keySet();
        //根据优先级
        for(Integer key : treeSet){
            boolean keyBoolean = key >= 0 ;
            List<String> list = map.get(key);
            for(String str: list){
                if(code.contains(str)){
                    System.out.println("命中了"+str+"这是个"+(keyBoolean?"正确碰撞":"错误碰撞"));
                    return keyBoolean;
                }
            }
        }
        return false;
    }
}
