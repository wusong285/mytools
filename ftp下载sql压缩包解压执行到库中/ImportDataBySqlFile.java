package com.ky.rdm.ezbwork.util;

import java.io.*;
import org.apache.tools.ant.*;
import org.apache.tools.ant.taskdefs.*;
import org.apache.tools.ant.types.*;

import java.io.File;

/**
 * 类描述：
 *
 * @author 吴松   song_wu
 * @date 2018/11/30 9:47
 * @updateRemark 修改备注：
 */
public class ImportDataBySqlFile {



    public static  void sqlCarriedOut(File source) {
        File src =source;
        SQLExec sqlExec = new SQLExec();
        //设置数据库参数
        sqlExec.setDriver("com.mysql.jdbc.Driver");
        sqlExec.setUrl("jdbc:mysql://192.168.18.27:3306/p2p_db?useUnicode=true&amp;characterEncoding=utf8");
        sqlExec.setUserid("root");
        sqlExec.setPassword("kydata");
        //要执行的脚本

        sqlExec.setSrc(src);
        //有出错的语句该如何处理
        sqlExec.setOnerror((SQLExec.OnError)(EnumeratedAttribute.getInstance(
                SQLExec.OnError.class, "abort")));
        sqlExec.setPrint(true); //设置是否输出
        //输出到文件 sql.out 中；不设置该属性，默认输出到控制台
        //sqlExec.setOutput(new File("sql.out"));
        sqlExec.setProject(new Project()); // 要指定这个属性，不然会出错
        sqlExec.execute();
    }
}
