package nju.software.manager;

import nju.software.enums.InfoflowEnum;
import nju.software.util.FileUtils;
import nju.software.constants.SettingConstant;
import nju.software.extractor.EntryPointExtractor;
import nju.software.extractor.SinkPointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;
import java.util.Set;

/**
 * 用来获取从入口点到沉淀点的路径（也就是app间通信接收的路径）
 * <p/>
 * Created by lab on 16-2-25.
 */
public class EntrySinkManager extends AbstractInfoflowManager {

    private static EntrySinkManager entrySinkManager = new EntrySinkManager();

    public static EntrySinkManager v() {
        return entrySinkManager;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        String apkFilePath = "SendSMS.apk";
        String apkDir = "apks";
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            EntrySinkManager.v().init(apkFilePath);
            EntrySinkManager.v().runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        }
    }

    /**
     * 根据给定的目录找出该目录下面所有的apk文件，不包括子目录的查找
     *
     * @param apkDir apk文件目录
     */
    public void runAnalysis(final String apkDir) {
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            EntrySinkManager.v().init(apkFilePath);
            EntrySinkManager.v().runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        }
    }

    public InfoflowResults runAnalysis(final String apkFilePath, final String androidJar) {
        try {
            final long start = System.nanoTime();
            app = new ApplicationManager(androidJar, apkFilePath);
            // Set configuration object
            app.setConfig(new InfoflowAndroidConfiguration());
            app.setTaintWrapper(taintWrapper);
            //以生命周期入口点作为源头
            Set<AndroidMethod> sources = EntryPointExtractor.v().getAllLifeCycleAndroidMethodsSets(apkFilePath);
            //以sinks文件中的数据作为沉淀点
            Set<AndroidMethod> sinks = SinkPointExtractor.generateAllSinkMethodsSets();
            app.calculateSourcesSinksEntrypoints(sources, sinks);
//            printResources();
            System.out.println("运行数据流分析...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler(apkFilePath, InfoflowEnum.ENTRYTOSINK));
            System.out.println("分析总共耗时" + (System.nanoTime() - start) / 1E9 + " seconds");
            printResult(app);
            return res;
        } catch (IOException ex) {
            System.err.println("Could not read file: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public void printResult(ApplicationManager app) {
        if (app != null
                && app.getCollectedSources() != null
                && !app.getCollectedSources().isEmpty()) {
            System.out.println("收集到入口点:");
            for (Stmt s : app.getCollectedSources())
                System.out.println("\t" + s);
        }
        if (app != null
                && app.getCollectedSinks() != null
                && !app.getCollectedSinks().isEmpty()) {
            System.out.println("收集到沉淀点:");
            for (Stmt s : app.getCollectedSinks())
                System.out.println("\t" + s);
        }
    }
}