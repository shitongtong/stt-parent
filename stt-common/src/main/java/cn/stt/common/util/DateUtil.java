package cn.stt.common.util;

import org.springframework.util.Assert;

import java.util.Date;

/**
 * 日期工具类
 *
 * @author shitt7
 */
public class DateUtil {

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 比较两个日期大小
     *
     * @param sourceDate
     * @param destDate
     * @return 1:sourceDate>destDate; -1:sourceDate<destDate; 0:sourceDate=destDate
     */
    public static int compareDate(Date sourceDate, Date destDate) {
        Assert.notNull(sourceDate, "sourceDate is null");
        Assert.notNull(destDate, "destDate is null");
        long t1 = sourceDate.getTime();
        long t2 = destDate.getTime();
        if (t1 > t2) {
            return 1;
        } else if (t1 < t2) {
            return -1;
        } else {
            return 0;
        }
    }

}
