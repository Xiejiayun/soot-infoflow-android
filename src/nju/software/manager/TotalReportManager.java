package nju.software.manager;

import nju.software.constants.SettingConstant;
import nju.software.util.FileUtils;

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
//        TotalReportManager.v().runAnalysis("apks\\autoaway.apk",SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        TotalReportManager.v().runAnalysis("infoleak");
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
        entryExitManager.init(apkFilePath);
        entrySourceManager.runAnalysis(apkFilePath, androidJarPath);
        entryExitManager.runAnalysis(apkFilePath, androidJarPath);
        entrySinkManager.runAnalysis(apkFilePath, androidJarPath);
        sourceExitManager.runAnalysis(apkFilePath, androidJarPath);
        sourceSinkManager.runAnalysis(apkFilePath, androidJarPath);
    }
}
