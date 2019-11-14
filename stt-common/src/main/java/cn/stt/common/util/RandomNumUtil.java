package cn.stt.common.util;

import java.util.Random;

/**
 * @ClassName RandomNumUtil
 * @Description TODO
 * @Author shitt7
 * @Date 2019/5/30 18:55
 * @Version 1.0
 */
public class RandomNumUtil {

    /**
     * 8位随机数，用于用户Id
     *
     * @return
     */
    public static String randomNum8() {
        return randomNum(8);
    }

    /**
     * 4位随机数，用于短信验证码
     *
     * @return
     */
    public static String randomNum4() {
        return randomNum(4);
    }

    private static String randomNum(int length) {
        //定义变长字符串
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        //随机生成数字，并添加到字符串
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 获取随机4位数
     *
     * @return
     */
    @Deprecated
    public static String getFourRandom() {
        Random rad = new Random();
        String result = rad.nextInt(10000) + "";
        if (result.length() != 4) {
            return getFourRandom();
        }
        return result;
    }

    /*public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            //System.out.println(randomNum4());
            System.out.println(getFourRandom());
        }
    }*/
}
