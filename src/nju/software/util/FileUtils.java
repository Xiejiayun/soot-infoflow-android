package nju.software.util;

import java.io.*;

/**
 * Created by Xie on 2016/1/20.
 */
public class FileUtils {

    private static String spliter = "/";

    /**
     * 获取一个目录中的所有apk文件
     *
     * @param directory 目录
     * @return 目录中的apk文件
     */
    public static String[] getAllApkFilePaths(String directory) {
        String apks[] = null;
        File dir = new File(directory);
        if (dir.isDirectory()) {
            apks = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".apk"))
                        return true;
                    else
                        return false;
                }
            });
        }
        for (int i = 0; i < apks.length; i++) {
            apks[i] = directory + spliter + apks[i];
        }
        return apks;
    }

    public static String getFileNameAndMkdir(String filePath) {
        String dir = filePath.substring(0, filePath.length()-4);
        File file = new File(dir);
        if (!file.exists())
            file.mkdir();
        return dir;
    }

    public static String getFileName(String filePath) {
        String dir = filePath.substring(0, filePath.length()-4);
        File file = new File(dir);
        if (!file.exists())
            file.mkdir();
        return dir;
    }


    /**
     * 将分析的结果写到文件中去
     * @param filename
     * @param printWriter
     */
    public void writeToFile(String filename, PrintWriter printWriter) {
        File file = new File(filename);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        printWriter.print("");
    }

}
