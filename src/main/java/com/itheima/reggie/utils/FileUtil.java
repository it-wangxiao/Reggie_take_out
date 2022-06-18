package com.itheima.reggie.utils;

import java.io.File;
import java.util.UUID;

/**
 * @Author Vsunks.v
 * @Blog blog.sunxiaowei.net
 * @Description:
 */
public class FileUtil {

    /*
        upload
            36字符串  ----  4aeb1da864af42a58bd7aeee7e3ac63a
            4
             a
              e
                4aeb1da864af42a58bd7aeee7e3ac63a
     */

    /**
     * 生成包含三级目录和随机文件名
     *
     * @return 数组，元素分别是：文件路径、文件名
     */
    public static String[] getFileNameAndPath() {
        // 生成一个唯一的文件名
        String fineName = UUID.randomUUID().toString();
        fineName = fineName.replaceAll("-", "");

        // 获取前三个字符作为目录名
        char c1 = fineName.charAt(0);
        char c2 = fineName.charAt(1);
        char c3 = fineName.charAt(2);

        StringBuilder filePath = new StringBuilder().append(c1).append("/").append(c2).append("/").append(c3).append("/");

        return new String[]{filePath.toString(), fineName};
    }

    /**
     * 生成包含三级目录的随机文件名
     *
     * @return 文件名，包含三级目录
     */
    public static String getFileNameWithPath() {
        // 生成一个唯一的文件名
        String fineName = UUID.randomUUID().toString();
        fineName = fineName.replaceAll("-", "");

        // 获取前三个字符作为目录名
        char c1 = fineName.charAt(0);
        char c2 = fineName.charAt(1);
        char c3 = fineName.charAt(2);

        StringBuilder filePath = new StringBuilder().append(c1).append("/").append(c2).append("/").append(c3).append("/");

        return filePath.append(fineName).toString();
    }

    /**
     * 判断三级目录是否存在
     *
     * @param fileNameWithPath
     * @param uploadRealPath
     */
    public static void makeDirs(String fileNameWithPath, String uploadRealPath) {
        //截取目录，判断是否存在，不存在就创建
        String filePath = fileNameWithPath.substring(0, fileNameWithPath.lastIndexOf("/"));

        // 如果不存在，就创建
        if (!new File(uploadRealPath, filePath).exists()) {
            new File(uploadRealPath, filePath).mkdirs();
        }
    }

}