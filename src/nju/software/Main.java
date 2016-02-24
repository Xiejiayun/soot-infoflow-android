package nju.software;

import nju.software.config.AndroidSootConfig;
import nju.software.constants.SettingConstant;
import nju.software.extractor.EntryPointExtractor;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointCreator;

import java.io.IOException;
import java.util.*;

public class Main {

    private static  Main main = new Main();

    public static  Main v() {
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
        AndroidEntryPointCreator creator = v().createEntryPointCreator();
        SootMethod mainMethod = creator.createDummyMain();
        System.out.println("Done in main class main method");
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
                this.entrypoints));
        Map<String, List<String>> callbackMethodSigs = new HashMap<String, List<String>>();
        for (String className : this.callbackMethods.keySet()) {
            List<String> methodSigs = new ArrayList<String>();
            callbackMethodSigs.put(className, methodSigs);
            for (SootMethodAndClass am : this.callbackMethods.get(className))
                methodSigs.add(am.getSignature());
        }
        entryPointCreator.setCallbackFunctions(callbackMethodSigs);
        return entryPointCreator;
    }
}