package com.ky.rdm.ezbwork.util;

import net.lingala.zip4j.exception.ZipException;
import org.apache.xmlbeans.impl.xb.xsdschema.impl.FacetImpl;

import java.io.File;
import java.util.List;

/**
 * 类描述：
 *
 * @author 吴松   song_wu
 * @date 2018/11/30 15:42
 * @updateRemark 修改备注：
 */
public class DataStorage {

    /**
     * @param temp 用于存放临时文件
     * @return
     */
    public boolean executeDataStorage(String temp) {
        boolean flage = true;
        if (temp == null || "".equals(temp)) temp = "c:/sqlTemporary/";
        //确保临时文件操作点存在
        File tempFile = new File(temp);
        if (!tempFile.exists() && !tempFile.isDirectory()) {
            tempFile.mkdirs();
        }
        //建立 下载存点  和sql解压存点
        String downLoad = tempFile.getName() + File.separator + "downLoadFile" + File.separator;
        String decSql = tempFile.getName() + File.separator + "decSqlFile" + File.separator;
        //下载存放点 不存在就建立
        File downLoadFile = new File(downLoad);
        if (!downLoadFile.exists() && !downLoadFile.isDirectory()) {
            downLoadFile.mkdirs();
        }

        //解压sql 存放点 不存在就建立
        File decSqlFile = new File(decSql);
        if (!decSqlFile.exists() && !decSqlFile.isDirectory()) {
            decSqlFile.mkdirs();
        }

        //下载文件
        if (flage && !downFile(downLoadFile.getPath())) {
            System.out.println("下载数据失败");
            flage= false;
        }
        //解压文件
        if (flage && !unzip(downLoadFile.getPath(), decSqlFile.getPath())) {
            System.out.println("压缩包没有数据！");
            flage= false;
        }

        //执行sql
        if (flage && !executionSql(decSqlFile.getPath())) {
            System.out.println("执行sql存在问题！");
            flage= false;
        }
        //删除临时文件
        ExcelExportUtil.delAllFile(temp); // 删除完里面所有内容

        return flage;
    }

    /**
     * @param localPath 下载文件存放位置
     * @return
     */
    public boolean downFile(String localPath) {

        String url = "192.168.18.11";
        int port = 21;
        String username = "administrator";
        String password = "Kydsj6089";
        String remotePath = "/test";
        String fileName = null;

        return FtpOperating.downFile(url, port, username, password, remotePath, fileName, localPath);
    }

    /**
     * @param zip  解压文件存放位置
     * @param dest 解压后存放位置
     * @return
     */
    public boolean unzip(String zip, String dest) {
        String passwd = "12345";
        try {
            File[] files = CompressUtil.unzip(zip, dest, passwd);
            if (files == null || files.length < 1) {
                return false;
            }
            for (File file : files) {
                System.out.println(file.getName());
            }
        } catch (ZipException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean executionSql(String source) {
        String driver = "com.mysql.jdbc.Driver";
        String url = "jdbc:mysql://192.168.18.27:3306/p2p_db?useUnicode=true&amp;characterEncoding=utf8";
        String user = "root";
        String passwd = "kydata";
        File sourcePage = new File("source");
        if (sourcePage.isDirectory()) {
            for (File file : sourcePage.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".sql")) {
                    ImportDataBySqlFile.sqlCarriedOut(file, driver, url, user, passwd);
                } else if (file.isDirectory()) {
                    for (String fn : file.list()) {
                        executionSql(fn);
                    }
                }
            }
        }
        return true;
    }
}
