package nju.software.manager;

import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.results.InfoflowResults;

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
    protected InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
    protected Set<String> entrypoints = null;
    protected Set<String> callbackClasses = null;

    public abstract InfoflowResults runAnalysis(final String fileName, final String androidJar);
}
