package nju.software;

import nju.software.config.AndroidSootConfig;
import nju.software.constants.SettingConstant;
import nju.software.extractor.EntryPointExtractor;
import nju.software.extractor.SinkPointExtractor;
import nju.software.handler.MyResultsAvailableHandler;
import nju.software.manager.ApplicationManager;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main {
    private static Main main = new Main();

    public static Main v() {
        return main;
    }

    private final Map<String, Set<SootMethodAndClass>> callbackMethods =
            new HashMap<String, Set<SootMethodAndClass>>(10000);
    private InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
    private Set<String> entrypoints = null;
    private Set<String> callbackClasses = null;

    public void init(String apkFileLocation) {
        this.entrypoints = EntryPointExtractor.v().getAllEntryPointClasses(apkFileLocation);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String apkFileLocation = "SendSMS.apk";
        SetupApplication setupApplication = new SetupApplication(SettingConstant.ANDROID_DEFALUT_JAR_PATH, apkFileLocation);


        AndroidSootConfig.setApkFilePath(apkFileLocation);
        AndroidSootConfig.initSoot();
        Main.v().init(apkFileLocation);
        Main.v().runAnalysis(apkFileLocation, SettingConstant.ANDROID_DEFALUT_JAR_PATH);
//        AndroidEntryPointCreator creator = v().createEntryPointCreator();
//        SootMethod mainMethod = creator.createDummyMain();
        System.out.println("Done in main class main method");
    }

    public InfoflowResults runAnalysis(final String fileName, final String androidJar) {
        try {
            final long start = System.nanoTime();
            final ApplicationManager app = new ApplicationManager(androidJar, fileName);
            // Set configuration object
            app.setConfig(new InfoflowAndroidConfiguration());

            final ITaintPropagationWrapper taintWrapper;
            final EasyTaintWrapper easyTaintWrapper;
            if (new File("../soot-infoflow/EasyTaintWrapperSource.txt").exists())
                easyTaintWrapper = new EasyTaintWrapper("../soot-infoflow/EasyTaintWrapperSource.txt");
            else
                easyTaintWrapper = new EasyTaintWrapper("EasyTaintWrapperSource.txt");
            easyTaintWrapper.setAggressiveMode(false);
            taintWrapper = easyTaintWrapper;
            app.setTaintWrapper(taintWrapper);
            //以生命周期入口点作为源头
            Set<AndroidMethod> sources = EntryPointExtractor.v().getAllLifeCycleAndroidMethodsSets(fileName);
            //以sinks文件中的数据作为沉淀点
            Set<AndroidMethod> sinks = SinkPointExtractor.generateAllSinkMethodsSets();
            app.calculateSourcesSinksEntrypoints(sources, sinks);

            app.printEntrypoints();
            app.printSinks();
            app.printSources();

            System.out.println("运行数据流分析...");
            final InfoflowResults res = app.runInfoflow(new MyResultsAvailableHandler());
            System.out.println("分析总共耗时" + (System.nanoTime() - start) / 1E9 + " seconds");

            if (config.getLogSourcesAndSinks()) {
                if (!app.getCollectedSources().isEmpty()) {
                    System.out.println("收集到源点:");
                    for (Stmt s : app.getCollectedSources())
                        System.out.println("\t" + s);
                }
                if (!app.getCollectedSinks().isEmpty()) {
                    System.out.println("收集到沉淀点:");
                    for (Stmt s : app.getCollectedSinks())
                        System.out.println("\t" + s);
                }
            }
            return res;
        } catch (IOException ex) {
            System.err.println("Could not read file: " + ex.getMessage());
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    public static void createMainMethod(AndroidEntryPointCreator entryPointCreator) {
        // Always update the entry point creator to reflect the newest set
        // of callback methods
        SootMethod entryPoint = entryPointCreator.createDummyMain();
        Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
        if (Scene.v().containsClass(entryPoint.getDeclaringClass().getName()))
            Scene.v().removeClass(entryPoint.getDeclaringClass());
        Scene.v().addClass(entryPoint.getDeclaringClass());
    }

    public AndroidEntryPointCreator createEntryPointCreator() {
        AndroidEntryPointCreator entryPointCreator = new AndroidEntryPointCreator(new ArrayList<String>(
                entrypoints));
        Map<String, List<String>> callbackMethodSigs = new HashMap<String, List<String>>();
        for (String className : callbackMethods.keySet()) {
            List<String> methodSigs = new ArrayList<String>();
            callbackMethodSigs.put(className, methodSigs);
            for (SootMethodAndClass am : callbackMethods.get(className))
                methodSigs.add(am.getSignature());
        }
        entryPointCreator.setCallbackFunctions(callbackMethodSigs);
        return entryPointCreator;
    }
}