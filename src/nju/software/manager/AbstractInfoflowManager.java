package nju.software.manager;

import nju.software.config.AndroidSootConfig;
import nju.software.extractor.EntryPointExtractor;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 提供抽象的分析方法
 * Created by lab on 16-2-25.
 */
public abstract class AbstractInfoflowManager {

    protected final Map<String, Set<SootMethodAndClass>> callbackMethods =
            new HashMap<>(10000);
    //Android Infoflow配置
    protected InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
    //所有的入口点
    protected Set<String> entrypoints = null;
    //污点标记器
    protected ITaintPropagationWrapper taintWrapper;
    protected EasyTaintWrapper easyTaintWrapper;
    //应用管理器
    protected ApplicationManager app;

    //初始化taintWrapper,entryPoints和Soot配置
    public void init(String apkFileLocation) {
        try {
            if (new File("../soot-infoflow/EasyTaintWrapperSource.txt").exists())
                easyTaintWrapper = new EasyTaintWrapper("../soot-infoflow/EasyTaintWrapperSource.txt");
            else
                easyTaintWrapper = new EasyTaintWrapper("EasyTaintWrapperSource.txt");
            easyTaintWrapper.setAggressiveMode(false);
            taintWrapper = easyTaintWrapper;
        } catch (IOException e) {
            e.printStackTrace();
        }
        entrypoints = EntryPointExtractor.v().getAllEntryPointClasses(apkFileLocation);
        AndroidSootConfig.setApkFilePath(apkFileLocation);
        AndroidSootConfig.initSoot();
    }

    public abstract InfoflowResults runAnalysis(final String fileName, final String androidJar);

    public void printResources() {
        //打印信息
        app.printEntrypoints();
        app.printSinks();
        app.printSources();
    }

    public abstract void printResult(ApplicationManager app);

    public Map<String, Set<SootMethodAndClass>> getCallbackMethods() {
        return callbackMethods;
    }

    public InfoflowAndroidConfiguration getConfig() {
        return config;
    }

    public void setConfig(InfoflowAndroidConfiguration config) {
        this.config = config;
    }

    public Set<String> getEntrypoints() {
        return entrypoints;
    }

    public void setEntrypoints(Set<String> entrypoints) {
        this.entrypoints = entrypoints;
    }

    public ITaintPropagationWrapper getTaintWrapper() {
        return taintWrapper;
    }

    public void setTaintWrapper(ITaintPropagationWrapper taintWrapper) {
        this.taintWrapper = taintWrapper;
    }

    public EasyTaintWrapper getEasyTaintWrapper() {
        return easyTaintWrapper;
    }

    public void setEasyTaintWrapper(EasyTaintWrapper easyTaintWrapper) {
        this.easyTaintWrapper = easyTaintWrapper;
    }

    public ApplicationManager getApp() {
        return app;
    }

    public void setApp(ApplicationManager app) {
        this.app = app;
    }
}
