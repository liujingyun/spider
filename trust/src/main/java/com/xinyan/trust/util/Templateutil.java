package com.xinyan.trust.util;

import com.google.gson.JsonObject;
import com.xinyan.trust.entity.FieldMeaning;
import org.bson.types.ObjectId;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 * @ClassName Templateutil 根据FieldMeaning注解 获取对应字段意思 一个返回json的数据字典的工具类
 * @Description
 * @Author jingyun_liu
 * @Date 2019/8/29 17:00
 * @Version V1.0
 **/
public class Templateutil {

    public static String getData(Object object){
        JsonObject jsonObject = new JsonObject();

        Class aClass = object.getClass();
        try {
            jsonObject.add(aClass.getName(), getDataTemple(aClass));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    public static JsonObject getDataTemple(Class aClass) throws ClassNotFoundException {
        JsonObject jsonObject = new JsonObject();
        Field[] fields = aClass.getDeclaredFields();
        for (Field field1 : fields) {
            Type type = field1.getGenericType();
            //这块可能需要跟不同bean类进行调整
            if (type.equals(ObjectId.class) || type.equals(String.class) || type.equals(Integer.class) || type.equals(BigDecimal.class)) {
                String value = field1.getAnnotation(FieldMeaning.class).value();
                jsonObject.addProperty(field1.getName(), value);
            } else {
                if(type instanceof ParameterizedType){
                    ParameterizedType listGenericType = (ParameterizedType) type;
                    Type[] listActualTypeArguments = listGenericType.getActualTypeArguments();
                    for(Type type1 : listActualTypeArguments){
                        jsonObject.add(field1.getName(), getDataTemple(Class.forName(type1.getTypeName())));
                    }
                } else {
                    jsonObject.add(field1.getName(), getDataTemple(Class.forName(type.getTypeName())));
                }
            }
        }
        return jsonObject;
    }
}
