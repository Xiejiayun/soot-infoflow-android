package nju.software.manager;

import nju.software.config.AndroidSootConfig;
import nju.software.constants.SettingConstant;
import nju.software.extractor.EntryPointExtractor;
import nju.software.extractor.ExitPointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
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


    public void init(String apkFileLocation) {
        entrypoints = EntryPointExtractor.v().getAllEntryPointClasses(apkFileLocation);
        AndroidSootConfig.setApkFilePath(apkFileLocation);
        AndroidSootConfig.initSoot();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String apkFileLocation = "SendSMS.apk";
        EntryExitManager.v().init(apkFileLocation);
        EntryExitManager.v().runAnalysis(apkFileLocation, SettingConstant.ANDROID_DEFALUT_JAR_PATH);

    }

    public InfoflowResults runAnalysis(final String fileName, final String androidJar) {
        try {
            final long start = System.nanoTime();
            app = new ApplicationManager(androidJar, fileName);
            // Set configuration object
            app.setConfig(new InfoflowAndroidConfiguration());

            //以生命周期入口点作为源头
            Set<AndroidMethod> entries = EntryPointExtractor.v().getAllLifeCycleAndroidMethodsSets(fileName);
            //以exits文件中的数据作为出口点
            Set<AndroidMethod> exits = ExitPointExtractor.generateAllExitMethodsSets();
            app.calculateSourcesSinksEntrypoints(entries, exits);

            System.out.println("运行数据流分析...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler());
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
