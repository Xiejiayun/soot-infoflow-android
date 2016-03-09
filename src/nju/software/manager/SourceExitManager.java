package nju.software.manager;

import nju.software.constants.SettingConstant;
import nju.software.enums.InfoflowEnum;
import nju.software.extractor.ExitPointExtractor;
import nju.software.extractor.SourcePointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
import nju.software.util.FileUtils;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.results.InfoflowResults;

import java.io.IOException;
import java.util.Set;

/**
 * 用来获取从通用的源头到出口点的沉淀点的路径（一般出口点定义为app间通信的机制）
 *
 * Created by lab on 16-2-25.
 */
public class SourceExitManager extends AbstractInfoflowManager{
    private static SourceExitManager sourceExitManager = new SourceExitManager();

    public static SourceExitManager v() {
        return sourceExitManager;
    }


    public static void main(String[] args) throws IOException, InterruptedException {
//        String apkFileLocation = "SendSMS.apk";
//        SourceExitManager.v().init(apkFileLocation);
//        SourceExitManager.v().runAnalysis(apkFileLocation, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
//        SourceExitManager.v().runAnalysis("apks/dumbphoneassistant.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        SourceExitManager.v().runAnalysis("InterAppCommunication\\SendSMS.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
    }

    /**
     * 根据给定的目录找出该目录下面所有的apk文件，不包括子目录的查找
     *
     * @param apkDir apk文件目录
     */
    public void runAnalysis(final String apkDir) {
        for (String apkFilePath : FileUtils.getAllApkFilePaths(apkDir)) {
            SourceExitManager.v().runAnalysis(apkFilePath, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        }
    }

    public InfoflowResults runAnalysis(final String fileName, final String androidJar) {
        try {
            final long start = System.nanoTime();
            SourceExitManager.v().init(fileName);
            app = new ApplicationManager(androidJar, fileName);
            config.setPathBuilder(DefaultPathBuilderFactory.PathBuilder.ContextInsensitiveSourceFinder);
            // Set configuration object
            app.setConfig(config);
            app.setTaintWrapper(taintWrapper);
            //以生命周期入口点作为源头
            Set<AndroidMethod> sources = SourcePointExtractor.generateAllSourceMethodsSets();
            //以sinks文件中的数据作为沉淀点
            Set<AndroidMethod> exits = ExitPointExtractor.generateAllExitMethodsSets();
            app.calculateSourcesSinksEntrypoints(sources, exits);

            System.out.println("运行数据流分析...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler(fileName, InfoflowEnum.SOURCETOEXIT));
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
            System.out.println("收集到出口点:");
            for (Stmt s : app.getCollectedSinks())
                System.out.println("\t" + s);
        }
        System.out.println("EntrySinkManager分析完成");
    }
}