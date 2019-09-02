package com.xinyan.trust.service;

import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author jingyun_liu
 * @Date 2019/8/2 10:35
 * @Version V1.0
 **/
public interface ICompareService {

    boolean getResultBoolean( Map<Integer,List<String>> map ,String code);
}
