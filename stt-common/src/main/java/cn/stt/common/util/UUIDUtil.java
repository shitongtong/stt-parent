package cn.stt.common.util;

import java.util.UUID;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/3/24.
 */
public class UUIDUtil {

    /**
     * 不带"-"分割的
     *
     * @return
     */
    public static String randomUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

}
