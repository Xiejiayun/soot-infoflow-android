package nju.software.manager;

import nju.software.constants.SettingConstant;
import nju.software.util.FileUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 用来生成所有的信息流
 * <p/>
 * Created by Xie on 2016/2/29.
 */
public class TotalReportManager {
    private static TotalReportManager totalReportManager = new TotalReportManager();
    EntryExitManager entryExitManager = EntryExitManager.v();
    EntrySinkManager entrySinkManager = EntrySinkManager.v();
    EntrySourceManager entrySourceManager = EntrySourceManager.v();
    SourceExitManager sourceExitManager = SourceExitManager.v();
    SourceSinkManager sourceSinkManager = SourceSinkManager.v();


    public static void main(String[] args) {
        TotalReportManager.v().runAnalysis("InterAppCommunication/SendSMS.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
    }

    public static TotalReportManager v() {
        return totalReportManager;
    }

    public void runAnalysis(final String apkDir) {
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
            System.gc();
        }
    }

    public void runAnalysis(final String apkFilePath, String androidJarPath) {
        BufferedWriter bw;
        File statistic;
        try {
            statistic = new File("statistics.txt");
            if (!statistic.exists())
                statistic.createNewFile();
            bw = new BufferedWriter(new FileWriter(new File("statistics")));

            long start = System.nanoTime();
            entrySourceManager.init(apkFilePath);
            entrySourceManager.runAnalysis(apkFilePath, androidJarPath);
            entrySinkManager.init(apkFilePath);
            entryExitManager.runAnalysis(apkFilePath, androidJarPath);
            entrySinkManager.init(apkFilePath);
            entrySinkManager.runAnalysis(apkFilePath, androidJarPath);
            sourceExitManager.init(apkFilePath);
            sourceExitManager.runAnalysis(apkFilePath, androidJarPath);
            sourceSinkManager.init(apkFilePath);
            sourceSinkManager.runAnalysis(apkFilePath, androidJarPath);
            System.out.println("Generating relation files for " + apkFilePath + " costs " + (double) (System.nanoTime() - start) / 1E9 + "seconds");
            bw.write("Generating relation files for " + apkFilePath + " costs " + (double) (System.nanoTime() - start) / 1E9 + "seconds");
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
