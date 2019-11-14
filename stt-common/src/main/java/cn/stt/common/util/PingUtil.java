package cn.stt.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PingUtil
 * @Description TODO
 * @Author shitt7
 * @Date 2018/12/18 14:11
 * @Version 1.0
 */
public class PingUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(PingUtil.class);
    /**
     * 默认执行时间（单位：s）
     */
    private static final int TIME_OUT = 3000;
    /**
     * 默认执行次数
     */
    private static final int TIMES = 1;

    /**
     * 使用jdk的api，部署需root用户
     *
     * @param ip
     * @return
     */
    public static boolean isPing(String ip) {
        boolean status = false;
        if (ip != null) {
            try {
                status = InetAddress.getByName(ip).isReachable(TIME_OUT);
            } catch (UnknownHostException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
        return status;
    }

    /**
     * 使用命令行方式执行ping命令，默认ping一次，3s超时
     *
     * @param ip
     * @return
     */
    public static boolean pingByCmd(String ip) {
        return pingByCmd(ip, TIMES, TIME_OUT);
    }

    /**
     * 执行ping命令
     *
     * @param ip
     * @param pingTimes 执行次数
     * @param timeOut   执行超时时间
     * @return ping测结果 true:成功; false:失败
     */
    public static boolean pingByCmd(String ip, int pingTimes, int timeOut) {
        try {
            String cmd = "";
            if (OSUtil.isWindows()) {
                cmd = "ping " + ip + " -n " + pingTimes + " -w " + timeOut;
            } else {
                cmd = "ping " + ip + " -c " + pingTimes + " -w " + timeOut / 1000;
            }
            CmdExecResult result = CmdExecuteUtil.exec(cmd, false);
            if (result.isExecStatus()) {
                int checkResult = getCheckResult(result.getSuccessInfo());
                if (1 == checkResult) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return false;
    }

    //若line含有=18ms TTL=16字样,说明已经ping通,返回1,否則返回0.
    /*private static int getCheckResult(String line) {
        // System.out.println("控制台输出的结果为:"+line);
        Pattern pattern = Pattern.compile("(\\d+ms)(\\s+)(TTL=\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            return 1;
        }
        return 0;
    }*/

    private static int getCheckResult(String line) {
        if (OSUtil.isWindows()) {
            if (line.contains("TTL=")) {
                return 1;
            }
        } else {
            if (line.contains("ttl=")) {
                return 1;
            }
        }
        return 0;
    }

    /**
     * 读取文件获取要ping测的ip列表
     *
     * @param filePath
     * @return
     * @throws Exception
     */
    public static List<String> getFindIps(String filePath) throws Exception {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new Exception(filePath + " is not exists");
        }
        List<String> ips = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        while ((line = br.readLine()) != null) {
            ips.add(line);
        }
        br.close();
        return ips;
    }
}
