package com.ky.rdm.ezbwork.util;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.*;

/**
 * 类描述：
 *
 * @author 吴松   song_wu
 * @date 2018/11/30 10:50
 * @updateRemark 修改备注：
 */
public class FtpOperating {

    /**
     * 本地字符编码
     */
    private static String LOCAL_CHARSET = "GBK";

    /**
     * FTP协议里面，规定文件名编码为iso-8859-1
     */
    private static String SERVER_CHARSET = "ISO-8859-1";

    /**
     * Description: 向FTP服务器上传文件
     *
     * @param url      FTP服务器hostname
     * @param port     FTP服务器端口
     * @param username FTP登录账号
     * @param password FTP登录密码
     * @param path     FTP服务器保存目录
     * @param fileName 上传到FTP服务器上的文件名
     * @param input    输入流
     * @return 成功返回true，否则返回false
     * @Version1.0 Jul 27, 2008 4:31:09 PM by 崔红保（cuihongbao@d-heaven.com）创建
     */
    public static boolean uploadFile(String url, int port, String username, String password, String path, String fileName, InputStream input) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            // 2.连接服务器
            ftp.connect(url, port);//连接FTP服务器

            //3.判断登陆是否成功
            reply = ftp.getReplyCode();
            if (FTPReply.isPositiveCompletion(reply)) {
                //如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
                if (ftp.login(username, password)) {
                    if (FTPReply.isPositiveCompletion(ftp.sendCommand(
                            "OPTS UTF8", "ON"))) {
                        // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                        LOCAL_CHARSET = "UTF-8";
                    }
                    ftp.setControlEncoding(LOCAL_CHARSET);
                    ftp.enterLocalPassiveMode();// 设置被动模式
                } else {
                    System.out.println("Connet ftpServer error! Please check user or password");
                    ftp.disconnect();
                    return success;
                }
            }

            // 4.指定写入的目录
            ftp.changeWorkingDirectory(path);
            // 5.写操作
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.storeFile(new String(fileName.getBytes(LOCAL_CHARSET), SERVER_CHARSET), input);
            input.close();
            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }

    /**
     * Description: 从FTP服务器下载文件
     *
     * @param url        FTP服务器hostname
     * @param port       FTP服务器端口
     * @param username   FTP登录账号
     * @param password   FTP登录密码
     * @param remotePath FTP服务器上的相对路径
     * @param fileName   要下载的文件名
     * @param localPath  下载后保存到本地的路径
     * @return
     * @Version1.0 Jul 27, 2008 5:32:36 PM by 崔红保（cuihongbao@d-heaven.com）创建
     */
    public static boolean downFile(String url, int port, String username, String password, String remotePath, String fileName, String localPath) {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        try {
            int reply;
            //1.连接服务器
            ftp.connect(url, port);
            //2.登录服务器 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);
            //3.判断登陆是否成功
            reply = ftp.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }
            if (FTPReply.isPositiveCompletion(ftp.sendCommand("OPTS UTF8", "ON"))) {
                // 开启服务器对UTF-8的支持，如果服务器支持就用UTF-8编码，否则就使用本地编码（GBK）.
                LOCAL_CHARSET = "UTF-8";
            }
            //4.指定要下载的目录
            ftp.changeWorkingDirectory(remotePath);//转移到FTP服务器目录
            //5.遍历下载的目录
            FTPFile[] fs = ftp.listFiles();
            for (FTPFile ff : fs) {
                //解决中文乱码问题，两次解码
                byte[] bytes = ff.getName().getBytes(SERVER_CHARSET);
                String fn = new String(bytes, LOCAL_CHARSET);
                if (fileName == null  || "".equals(fileName) || fn.equals(fileName)) {
                    //6.写操作，将其写入到本地文件中
                    File localFile = new File(localPath + fn);
                    OutputStream is = new FileOutputStream(localFile);
                    ftp.retrieveFile(ff.getName(), is);
                    is.close();
                }
            }

            ftp.logout();
            success = true;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                }
            }
        }
        return success;
    }


}
