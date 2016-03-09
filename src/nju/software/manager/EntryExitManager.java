package nju.software.manager;

import nju.software.constants.SettingConstant;
import nju.software.enums.InfoflowEnum;
import nju.software.extractor.EntryPointExtractor;
import nju.software.extractor.ExitPointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
import nju.software.util.FileUtils;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;
import java.util.Set;

/**
 * Created by lab on 16-2-26.
 */
public class EntryExitManager extends AbstractInfoflowManager{
    private static EntryExitManager entryExitManager = new EntryExitManager();

    public static EntryExitManager v() {
        return entryExitManager;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
        String apkFileLocation = "apks";
        EntryExitManager.v().runAnalysis(apkFileLocation);
//        String apkFileLocation = "apks\\autoaway.apk";
//        EntryExitManager.v().init(apkFileLocation);
//        EntryExitManager.v().runAnalysis(apkFileLocation, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
    }

    /**
     * 根据给定的目录找出该目录下面所有的apk文件，不包括子目录的查找
     *
     * @param apkDir apk文件目录
     */
    public void runAnalysis(final String apkDir) {
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            EntryExitManager.v().init(apkFilePath);
            EntryExitManager.v().runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        }
    }

    public InfoflowResults runAnalysis(final String fileName, final String androidJar) {
        try {
            final long start = System.nanoTime();
            app = new ApplicationManager(androidJar, fileName);
            // Set configuration object
            app.setConfig(new InfoflowAndroidConfiguration());
            app.setTaintWrapper(taintWrapper);
            //以生命周期入口点作为源头
            Set<AndroidMethod> entries = EntryPointExtractor.v().getAllLifeCycleAndroidMethodsSets(fileName);
            //以exits文件中的数据作为出口点
            Set<AndroidMethod> exits = ExitPointExtractor.generateAllExitMethodsSets();
            app.calculateSourcesSinksEntrypoints(entries, exits);

            System.out.println("运行数据流分析...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler(fileName, InfoflowEnum.ENTRYTOEXIT));
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
                &&app.getCollectedSources() != null
                &&!app.getCollectedSources().isEmpty()) {
            System.out.println("收集到入口点:");
            for (Stmt s : app.getCollectedSources())
                System.out.println("\t" + s);
        }
        if (app != null
                &&app.getCollectedSinks() != null
                && !app.getCollectedSinks().isEmpty()) {
            System.out.println("收集到出口点:");
            for (Stmt s : app.getCollectedSinks())
                System.out.println("\t" + s);
        }
        System.out.println("EntrySinkManager分析完成");
    }
}
