package cn.stt.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * ftp工具
 * 存在上传和下载中文文件失败问题，待解决
 *
 * @author shitt7
 */
public class FtpUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpUtil.class);

    private String hostname;
    private int port;
    private String username;
    private String password;
    private FTPClient ftpClient = null;

    public FtpUtil(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        init();
    }

    private void init() {
        LOGGER.info("connecting...ftp:{}:{}", hostname, port);
        try {
            ftpClient = new FTPClient();
            ftpClient.connect(this.hostname, this.port);
            ftpClient.login(this.username, this.password);
            //设置文件编码格式
            ftpClient.setControlEncoding("UTF-8");
            //ftp通信有两种模式
            //PORT(主动模式)客户端开通一个新端口(>1024)并通过这个端口发送命令或传输数据,期间服务端只使用他开通的一个端口，例如21
            //PASV(被动模式)客户端向服务端发送一个PASV命令，服务端开启一个新端口(>1024),并使用这个端口与客户端的21端口传输数据
            //由于客户端不可控，防火墙等原因，所以需要由服务端开启端口，需要设置被动模式
            ftpClient.enterLocalPassiveMode();
            //设置传输方式为流方式
            ftpClient.setFileTransferMode(FTP.STREAM_TRANSFER_MODE);
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //获取状态码，判断是否连接成功
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                LOGGER.info("connecting...ftp失败:{}:{}", hostname, port);
                throw new RuntimeException("FTP服务器拒绝连接");
            }
            LOGGER.info("connecting...ftp成功:{}:{}", hostname, port);
        } catch (IOException e) {
            LOGGER.info("connecting...ftp异常:{}:{}", hostname, port);
            LOGGER.error("FTP连接异常:", e);
        }

    }

    public void close() {
        try {
            ftpClient.logout();
        } catch (IOException e) {
            LOGGER.error("ftp关闭异常:", e);
        } finally {
            if (ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    LOGGER.error("ftp断开连接异常:", e);
                }
            }
        }
    }

    /**
     * @param uploadPath 上传路径
     * @param file       上传的文件
     * @return
     */
    public boolean fileUpload(String uploadPath, File file) {
        return fileUpload(uploadPath, file.getName(), file);
    }

    /**
     * @param uploadPath 上传路径
     * @param fileName   上传文件名
     * @param file       上传的文件
     * @return
     */
    public boolean fileUpload(String uploadPath, String fileName, File file) {
        try {
            InputStream in = new FileInputStream(file);
            return fileUpload(uploadPath, fileName, in);
        } catch (FileNotFoundException e) {
            LOGGER.error("", e);
        }
        return false;
    }

    /**
     * @param uploadPath 上传路径
     * @param fileName   上传文件名
     * @param input      输入流
     * @return
     */
    public boolean fileUpload(String uploadPath, String fileName, InputStream input) {
        //上传文件
        boolean uploadFlag = false;
        try {
            //判断是否存在目录
            if (!ftpClient.changeWorkingDirectory(uploadPath)) {
                String[] dirs = uploadPath.split("/");
                //创建目录
                for (String dir : dirs) {
                    if (null == dir || "".equals(dir)) {
                        continue;
                    }
                    //判断是否存在目录
                    if (!ftpClient.changeWorkingDirectory(dir)) {
                        //不存在则创建
                        if (!ftpClient.makeDirectory(dir)) {
                            throw new RuntimeException("子目录创建失败");
                        }
                        //进入新创建的目录
                        ftpClient.changeWorkingDirectory(dir);
                    }
                }
            }
            uploadFlag = ftpClient.storeFile(new String(fileName.getBytes("utf-8"), "iso-8859-1"), input);
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        close();
        LOGGER.info("文件{}上传到{}:{}", fileName, uploadPath, uploadFlag);
        return uploadFlag;
    }

    /**
     * @param filePath      远程文件路径
     * @param fileName      远程文件名
     * @param localFilePath 本地文件路径
     * @return
     */
    public boolean downloadFile(String filePath, String fileName, String localFilePath) {
        return downloadFile(filePath, fileName, localFilePath, true);
    }

    /**
     * 下载文件
     *
     * @param filePath
     * @param fileName
     * @param localFilePath
     * @param isClose
     * @return
     */
    public boolean downloadFile(String filePath, String fileName, String localFilePath, boolean isClose) {
        return downloadFile(filePath, fileName, localFilePath, null, isClose);
    }

    /**
     * 下载文件
     *
     * @param filePath
     * @param fileName
     * @param localFilePath
     * @param localFileName
     * @return
     */
    public boolean downloadFile(String filePath, String fileName, String localFilePath, String localFileName) {
        return downloadFile(filePath, fileName, localFilePath, localFileName, true);
    }

    /**
     * @param filePath      远程文件路径
     * @param fileName      远程文件名
     * @param localFilePath 本地文件路径
     * @param localFileName 本地文件名，若为空则为远程文件名
     * @return
     */
    public boolean downloadFile(String filePath, String fileName, String localFilePath, String localFileName, boolean isClose) {
        boolean flag = false;
        try {
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                LOGGER.info("下载文件路径不存在:{}", filePath);
            } else {
                boolean existFlag = false;
                //获取该目录所有文件
                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    //判断是否有目标文件
                    String ftpFileName = file.getName();
                    if (ftpFileName.startsWith(fileName)) {
                        //如果找到，将目标文件复制到本地
                        File localFileDir = new File(localFilePath);
                        if (!localFileDir.exists()) {
                            localFileDir.mkdirs();
                        }
                        File localFile = null;
                        if (StringUtils.isBlank(localFileName)) {
                            localFile = new File(localFilePath, ftpFileName);
                        } else {
                            localFile = new File(localFilePath, localFileName);
                        }
                        OutputStream out = new FileOutputStream(localFile);
                        //编码转换解决文件中文问题
                        flag = ftpClient.retrieveFile(new String(ftpFileName.getBytes("utf-8"), "iso-8859-1"), out);
                        LOGGER.info("ftp文件:{}下载到本地{}:{}", ftpFileName, localFile.getAbsolutePath(), flag);
                        out.close();
                        existFlag = true;
//                        break;
                    }
                }
                if (!existFlag) {
                    LOGGER.info("下载文件不存在:{}", fileName);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        if (isClose) {
            close();
        }
        return flag;
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public boolean deleteFile(String filePath, String fileName) {
        return deleteFile(filePath, fileName, true);
    }

    /**
     * 删除文件
     *
     * @param filePath
     * @param fileName
     * @param isClose  是否关闭ftp
     * @return
     */
    public boolean deleteFile(String filePath, String fileName, boolean isClose) {
        boolean flag = false;
        try {
            if (!ftpClient.changeWorkingDirectory(filePath)) {
                LOGGER.info("删除文件路径不存在:{}", filePath);
            } else {
                boolean existFlag = false;
                //获取该目录所有文件
                FTPFile[] files = ftpClient.listFiles();
                for (FTPFile file : files) {
                    //判断是否有目标文件
                    String ftpFileName = file.getName();
                    if (ftpFileName.startsWith(fileName)) {
                        //如果找到，则删除
                        flag = ftpClient.deleteFile(ftpFileName);
                        LOGGER.info("ftp文件{}删除:{}", ftpFileName, flag);
                        existFlag = true;
//                        break;
                    }
                }
                if (!existFlag) {
                    LOGGER.info("删除文件不存在:{}", fileName);
                }
            }
        } catch (IOException e) {
            LOGGER.error("", e);
        }
        if (isClose) {
            close();
        }
        return flag;
    }


    /*public static void main(String[] args) {
        String hostname = "172.25.242.118";
        int port = 21;
        String username = "ftpuser";
        String password = "ftp@WS3ed";
        FtpUtil ftpUtil = new FtpUtil(hostname, port, username, password);
        //上传1
        *//*InputStream in = new ByteArrayInputStream("哈哈哈testest".getBytes());
        String fileName = "test.TXT";
        boolean b = ftpUtil.fileUpload("/", fileName, in);
        LOGGER.info("上传:" + b);*//*
        //上传2
//        File file = new File("D:\\ftp\\新建文本文档.txt");
        File file = new File("D:\\ftp\\哈哈");
//        File file = new File("D:\\ftp\\7.3、storm项目启动.txt");
        boolean b = ftpUtil.fileUpload("/", file);
        LOGGER.info("上传:" + b);
        //下载
//        boolean b1 = ftpUtil.downloadFile("/", "新建文本文档.txt", "D:\\ftp");
//        LOGGER.info("下载:" + b1);
    }*/

}