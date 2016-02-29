package nju.software.manager;

import nju.software.enums.InfoflowEnum;
import nju.software.extractor.PermissionMethodExtractor;
import nju.software.util.FileUtils;
import nju.software.constants.SettingConstant;
import nju.software.extractor.EntryPointExtractor;
import nju.software.extractor.SourcePointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;

import java.io.IOException;
import java.util.Set;

/**
 * 这个方法主要用来构建从入口点到源点的路径
 *
 * Created by lab on 16-2-26.
 */
public class EntrySourceManager extends AbstractInfoflowManager{
    private static EntrySourceManager entrySourceManager = new EntrySourceManager();

    public static EntrySourceManager v() {
        return entrySourceManager;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        EntrySourceManager.v().runAnalysis("apks");
    }

    /**
     * 根据给定的目录找出该目录下面所有的apk文件，不包括子目录的查找
     *
     * @param apkDir apk文件目录
     */
    public void runAnalysis(final String apkDir) {
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            EntrySourceManager.v().runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        }
    }

    public InfoflowResults runAnalysis(final String fileName, final String androidJar) {
        try {
            final long start = System.nanoTime();
            EntrySourceManager.v().init(fileName);
            final ApplicationManager app = new ApplicationManager(androidJar, fileName);
            // Set configuration object
            app.setConfig(new InfoflowAndroidConfiguration());
            app.setTaintWrapper(taintWrapper);
            //以生命周期入口点作为源头
            Set<AndroidMethod> entries = EntryPointExtractor.v().getAllLifeCycleAndroidMethodsSets(fileName);
            //以sources文件中的数据作为源点
            Set<AndroidMethod> sources = PermissionMethodExtractor.getAndroidMethods();
            app.calculateSourcesSinksEntrypoints(entries, sources);


            System.out.println("运行数据流分析...");
            MyResultsAvailableHandler myResultsAvailableHandler = new MyResultsAvailableHandler(fileName, InfoflowEnum.ENTRYTOSOURCE);
            myResultsAvailableHandler.setInfoflowEnum(InfoflowEnum.ENTRYTOSOURCE);
            final InfoflowResults res = app.runInfoflow(myResultsAvailableHandler);
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
            System.out.println("收集到源点:");
            for (Stmt s : app.getCollectedSources())
                System.out.println("\t" + s);
        }
        if (app != null
                &&app.getCollectedSinks() != null
                && !app.getCollectedSinks().isEmpty()) {
            System.out.println("收集到沉淀点:");
            for (Stmt s : app.getCollectedSinks())
                System.out.println("\t" + s);
        }
        System.out.println("EntrySinkManager分析完成");
    }
}
