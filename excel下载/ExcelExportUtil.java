package com.ky.rdm.ezbwork.util;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 类描述：
 *
 * @author 吴松   song_wu
 * @date 2018/11/24 14:02
 * @updateRemark 修改备注：
 */
public class ExcelExportUtil {

    /**
     * @param exportModule    模板位置
     * @param exportTemporary 临时文件位置
     * @param exportZip       生成zip位置
     * @param zipPackageName  生成zip名称
     * @param dataList        生成数据
     * @param dataPage        拆分文件数量
     * @return
     * @throws InvalidFormatException
     */
    public String exportDetail(String exportModule, String exportTemporary, String exportZip, String zipPackageName,
                               List<Map<String, Object>> dataList, Integer dataPage) throws InvalidFormatException {
        // 模板在项目存放位置
        String fileRootPath = exportModule;
        String filePath = exportTemporary;
        String filePathZip = exportZip;
        // 模板文件名称
        String fileName = "";
        // 将excel导出的文件位置

        // 得到此路径下文件
        File fileDir = new File(filePath);
        //创建文件夹
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        // 用于存放生成的excel文件名称
        List<String> fileNames = new ArrayList<String>();
        // 导出Excel文件路径
        String fullFilePath = "";
        //输入流
        InputStream in = null;
        //输出流
        FileOutputStream os = null;
        //循环导出excel到临时文件夹中
        int var = dataList.size() / dataPage;
        Integer loopValue = 0;
        if (dataList.size() % dataPage > 0) var++;
        for (int i = 0; i < var; i++) {
            // 往excel填入内容
            Integer start = loopValue;
            Integer end = loopValue + dataPage;
            if (end > dataList.size()) end = dataList.size();
            List<Map<String, Object>> beanList = dataList.subList(start, end);
            loopValue += dataPage;
            //每次导出的excel的文件名
            String singleFileName = "登记信息" + (i + 1) + ".xls";
            if (beanList != null) {
                try {
                    //XLSTransformer生成excel文件
                    XLSTransformer transformer = new XLSTransformer();
                    in = new FileInputStream(new File(fileRootPath + File.separator + fileName));
                    HSSFWorkbook workbook;
                    // 设置sheet页名称
                    String sheetName = "详细";
                    Map<String, Object> res = new HashMap<>();
                    res.put("result", beanList);
                    workbook = (HSSFWorkbook) transformer.transformXLS(in, res);
                    // 设置sheet页名称
                    workbook.setSheetName(0, sheetName);
                    // 导出excel的全路径
                    fullFilePath = filePath + File.separator + singleFileName;
                    fileNames.add(fullFilePath);
                    os = new FileOutputStream(fullFilePath);
                    // 写文件
                    workbook.write(os);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //清空流缓冲区数据
                    try {
                        os.flush();
                        //关闭流
                        os.close();
                        in.close();
                        os = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        //导出压缩文件的全路径
        String zipFilePath = filePathZip + zipPackageName + ".zip";
        //导出zip
        File zip = new File(zipFilePath);
        //将excel文件生成压缩文件
        File srcfile[] = new File[fileNames.size()];
        for (int j = 0, n1 = fileNames.size(); j < n1; j++) {
            srcfile[j] = new File(fileNames.get(j));
        }
        ZipFiles(srcfile, zip);
        return zipFilePath;
    }


    //压缩文件
    public void ZipFiles(File[] srcfile, File zipfile) {
        byte[] buf = new byte[1024];
        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
                    zipfile));
            for (int i = 0; i < srcfile.length; i++) {
                FileInputStream in = new FileInputStream(srcfile[i]);
                out.putNextEntry(new ZipEntry(srcfile[i].getName()));
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.closeEntry();
                in.close();
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param exportModule
     * @param exportTemporary
     * @param excelName
     * @param dataList
     */
    public String exportToExcel(String exportModule, String exportTemporary,String excelName, List<Map<String, Object>> dataList){
        // 模板在项目存放位置
        String fileRootPath = exportModule;
        String filePath = exportTemporary;
        // 模板文件名称
        String fileName = "";
        // 将excel导出的文件位置

        // 得到此路径下文件
        File fileDir = new File(filePath);
        //创建文件夹
        if (!fileDir.exists() && !fileDir.isDirectory()) {
            fileDir.mkdirs();
        }
        // 导出Excel文件路径
        String fullFilePath = "";
        //输入流
        InputStream in = null;
        //输出流
        FileOutputStream os = null;
        //循环导出excel到临时文件夹中

        // 往excel填入内容
        //每次导出的excel的文件名
        String singleFileName = excelName + ".xls";
        if (dataList != null) {
            try {
                //XLSTransformer生成excel文件
                XLSTransformer transformer = new XLSTransformer();
                in = new FileInputStream(new File(fileRootPath + File.separator + fileName));
                HSSFWorkbook workbook;
                // 设置sheet页名称
                String sheetName = "详细";
                Map<String, Object> res = new HashMap<>();
                res.put("result", dataList);
                workbook = (HSSFWorkbook) transformer.transformXLS(in, res);
                // 设置sheet页名称
                workbook.setSheetName(0, sheetName);
                // 导出excel的全路径
                fullFilePath = filePath + singleFileName;
                os = new FileOutputStream(fullFilePath);
                // 写文件
                workbook.write(os);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    //清空流缓冲区数据
                    os.flush();
                    os.close();
                    in.close();
                    os = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fullFilePath;
    }

    /**
     *
     * @param excelNames
     * @param exportZip
     * @param zipPackageName
     */
    public void exportToZip(List<String> excelNames, String exportZip, String zipPackageName){
        //导出压缩文件的全路径
        String zipFilePath = exportZip + zipPackageName + ".zip";
        //导出zip
        File zip = new File(zipFilePath);
        //将excel文件生成压缩文件
        File srcfile[] = new File[excelNames.size()];
        for (int j = 0, n1 = excelNames.size(); j < n1; j++) {
            srcfile[j] = new File(excelNames.get(j));
        }
        ZipFiles(srcfile, zip);
    }

    /***
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                flag = true;
            }
        }
        return flag;
    }

    public static void main(String[] args) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i=0;i<102;i++){
            Map<String, Object> map = new HashMap<>();
            map.put("CASE_NO","CASE_NO"+i);
            map.put("PT_ID","PT_ID"+i);
            map.put("PT_NAME","PT_NAME"+i);
            map.put("USER_NAME","USER_NAME"+i);
            map.put("CARDTYPE","CARDTYPE"+i);
            map.put("IDCARD","IDCARD"+i);
            map.put("PHONE_NO",	"PHONE_NO"+i);
            map.put("JG",	"JG"+i);
            map.put("RES","RES"+i);
            map.put("AMOUNT",	i);
            map.put("RETURN_AMOUNT", i);
            map.put("WITHDRAW",i);
            dataList.add(map);
        }
    }
}
