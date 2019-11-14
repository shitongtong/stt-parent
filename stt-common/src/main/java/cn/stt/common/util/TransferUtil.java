package cn.stt.common.util;


import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TransferUtil {

    /***
     * 对象转换
     *
     * @param list
     * @param clazz
     * @return
     */
    public static <S, T> List<T> transfer(List<S> list, Class<T> clazz) {
        List<T> targetList = new ArrayList<T>();
        T instance = null;
        try {
            for (S s : list) {
                instance = clazz.newInstance();
                BeanUtils.copyProperties(s, instance);
                targetList.add(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("对象转换错误：" + e.getMessage());
        }

        return targetList;
    }

    /***
     * @param s     源目标对象
     * @param clazz 目标类
     * @return
     */
    public static <S, T> T transfer(S s, Class<T> clazz) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            BeanUtils.copyProperties(s, instance);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("对象转换错误：" + e.getMessage());
        }

        return instance;
    }

    /**
     * 将对象转为map
     *
     * @param obj
     * @return
     */
    public static Map<String, Object> objectToMap(Object obj) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 将map转为指定对象
     *
     * @param map
     * @param clz
     * @return
     */
    public static Object mapToObject(Map<String, Object> map, Class<?> clz) {
        Object obj = null;
        try {
            obj = clz.newInstance();
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                int mod = field.getModifiers();
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                    continue;
                }
                field.setAccessible(true);
                field.set(obj, map.get(field.getName()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
