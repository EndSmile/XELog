package com.ldy.xelog_read.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by ldy on 2017/3/23.
 */

public class FileUtils {

    public static String readFile(String path) {
        StringBuilder content = new StringBuilder();
        try {
            InputStream instream = new FileInputStream(path);//读取输入流
            InputStreamReader inputreader = new InputStreamReader(instream);//设置流读取方式
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            while ((line = buffreader.readLine()) != null) {
                content.append(line).append("\n");//读取的文件内容
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return content.toString();
    }
}
