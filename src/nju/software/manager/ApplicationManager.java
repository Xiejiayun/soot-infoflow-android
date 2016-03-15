package nju.software.manager;

/**
 * Created by Xie on 2016/1/20.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.Stmt;
import soot.jimple.infoflow.Infoflow;
import soot.jimple.infoflow.android.AnalyzeJimpleClass;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.android.data.parsers.PermissionMethodParser;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.infoflow.android.resources.ARSCFileParser;
import soot.jimple.infoflow.android.resources.ARSCFileParser.AbstractResource;
import soot.jimple.infoflow.android.resources.ARSCFileParser.StringResource;
import soot.jimple.infoflow.android.resources.LayoutControl;
import soot.jimple.infoflow.android.resources.LayoutFileParser;
import soot.jimple.infoflow.android.source.AccessPathBasedSourceSinkManager;
import soot.jimple.infoflow.cfg.BiDirICFGFactory;
import soot.jimple.infoflow.config.IInfoflowConfig;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.jimple.infoflow.data.pathBuilders.DefaultPathBuilderFactory;
import soot.jimple.infoflow.entryPointCreators.AndroidEntryPointCreator;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.ipc.IIPCManager;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.source.data.ISourceSinkDefinitionProvider;
import soot.jimple.infoflow.source.data.SourceSinkDefinition;
import soot.jimple.infoflow.taintWrappers.ITaintPropagationWrapper;
import soot.options.Options;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

public class ApplicationManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, Set<SootMethodAndClass>> callbackMethods =
            new HashMap<>(10000);
    private final String androidJar;
    private final boolean forceAndroidJar;
    private final String apkFileLocation;
    private final String additionalClasspath;
    private ISourceSinkDefinitionProvider sourceSinkProvider;
    private InfoflowAndroidConfiguration config = new InfoflowAndroidConfiguration();
    private Set<String> entrypoints = null;
    private Set<String> callbackClasses = null;
    private List<ARSCFileParser.ResPackage> resourcePackages = null;
    private String appPackageName = "";
    private ITaintPropagationWrapper taintWrapper;

    private AccessPathBasedSourceSinkManager sourceSinkManager = null;
    private AndroidEntryPointCreator entryPointCreator = null;

    private IInfoflowConfig sootConfig = null;
    private BiDirICFGFactory cfgFactory = null;

    private IIPCManager ipcManager = null;

    private long maxMemoryConsumption = -1;

    private Set<Stmt> collectedSources = null;
    private Set<Stmt> collectedSinks = null;

    public ApplicationManager(String androidJar, String apkFileLocation) {
        this(androidJar, apkFileLocation, "", null);
    }

    public ApplicationManager(String androidJar, String apkFileLocation,
                              IIPCManager ipcManager) {
        this(androidJar, apkFileLocation, "", ipcManager);
    }

    public ApplicationManager(String androidJar, String apkFileLocation,
                              String additionalClasspath,
                              IIPCManager ipcManager) {
        File f = new File(androidJar);
        this.forceAndroidJar = f.isFile();
        this.androidJar = androidJar;
        this.apkFileLocation = apkFileLocation;
        this.ipcManager = ipcManager;
        this.additionalClasspath = additionalClasspath;
    }

    public Set<SourceSinkDefinition> getSinks() {
        return this.sourceSinkProvider == null ? null
                : this.sourceSinkProvider.getSinks();
    }

    public Set<Stmt> getCollectedSinks() {
        return collectedSinks;
    }

    public void printSinks() {
        if (this.sourceSinkProvider == null) {
            System.err.println("Sinks not calculated yet");
            return;
        }
        System.out.println("Sinks:");
        for (SourceSinkDefinition am : getSinks()) {
            System.out.println(am.toString());
        }
        System.out.println("End of Sinks");
    }


    public Set<SourceSinkDefinition> getSources() {
        return this.sourceSinkProvider == null ? null
                : this.sourceSinkProvider.getSources();
    }


    public Set<Stmt> getCollectedSources() {
        return collectedSources;
    }


    public void printSources() {
        if (this.sourceSinkProvider == null) {
            System.err.println("Sources not calculated yet");
            return;
        }
        System.out.println("Sources:");
        for (SourceSinkDefinition am : getSources()) {
            System.out.println(am.toString());
        }
        System.out.println("End of Sources");
    }


    public Set<String> getEntrypointClasses() {
        return entrypoints;
    }


    public void printEntrypoints() {
        if (entrypoints == null)
            System.out.println("Entry points not initialized");
        else {
            System.out.println("Classes containing entry points:");
            for (String className : entrypoints)
                System.out.println("\t" + className);
            System.out.println("End of Entrypoints");
        }
    }

    public Set<String> getCallbackClasses() {
        return callbackClasses;
    }


    public void setCallbackClasses(Set<String> callbackClasses) {
        this.callbackClasses = callbackClasses;
    }


    public ITaintPropagationWrapper getTaintWrapper() {
        return this.taintWrapper;
    }


    public void setTaintWrapper(ITaintPropagationWrapper taintWrapper) {
        this.taintWrapper = taintWrapper;
    }


    public void calculateSourcesSinksEntrypoints(Set<AndroidMethod> sources,
                                                 Set<AndroidMethod> sinks) throws IOException {

        final Set<SourceSinkDefinition> sourceDefs = new HashSet<>(sources.size());
        final Set<SourceSinkDefinition> sinkDefs = new HashSet<>(sinks.size());
        for (AndroidMethod am : sources)
            sourceDefs.add(new SourceSinkDefinition(am));
        for (AndroidMethod am : sinks)
            sinkDefs.add(new SourceSinkDefinition(am));
        ISourceSinkDefinitionProvider parser = new ISourceSinkDefinitionProvider() {

            @Override
            public Set<SourceSinkDefinition> getSources() {
                return sourceDefs;
            }

            @Override
            public Set<SourceSinkDefinition> getSinks() {
                return sinkDefs;
            }

            @Override
            public Set<SourceSinkDefinition> getAllMethods() {
                Set<SourceSinkDefinition> sourcesSinks = new HashSet<>(sourceDefs.size()
                        + sinkDefs.size());
                sourcesSinks.addAll(sourceDefs);
                sourcesSinks.addAll(sinkDefs);
                return sourcesSinks;
            }

        };
        calculateSourcesSinksEntrypoints(parser);
    }

    /**
     * 从txt文件中计算出相应的源头和沉淀点
     *
     * @param sourceSinkFile 源点和沉淀点文件(txt文件)
     * @throws IOException
     */
    public void calculateSourcesSinksEntrypoints(String sourceSinkFile)
            throws IOException {
        //获取PermissionMethodParser
        ISourceSinkDefinitionProvider parser = PermissionMethodParser.fromFile(sourceSinkFile);
        calculateSourcesSinksEntrypoints(parser);
    }

    /**
     * 从ISourceSinkDefinitionProvider中计算出相应的源头和沉淀点
     *
     * @param sourcesAndSinks
     * @throws IOException
     */
    public void calculateSourcesSinksEntrypoints(ISourceSinkDefinitionProvider sourcesAndSinks)
            throws IOException {
        // To look for callbacks, we need to start somewhere. We use the Android
        // lifecycle methods for this purpose.
        sourceSinkProvider = sourcesAndSinks;
        try {
            ProcessManifest processMan = new ProcessManifest(apkFileLocation);
            appPackageName = processMan.getPackageName();
            entrypoints = processMan.getEntryPointClasses();
        } catch (Exception e) {
            e.printStackTrace();
        }


        // Parse the resource file
        long beforeARSC = System.nanoTime();
        ARSCFileParser resParser = new ARSCFileParser();
        resParser.parse(apkFileLocation);
        logger.info("ARSC file parsing took " + (System.nanoTime() - beforeARSC) / 1E9 + " seconds");
        resourcePackages = resParser.getPackages();

        // Add the callback methods
        LayoutFileParser lfp = null;
        if (config.getEnableCallbacks()) {
            lfp = new LayoutFileParser(appPackageName, resParser);
            calculateCallbackMethods(resParser, lfp);

            // Some informational output
            System.out.println("Found " + lfp.getUserControls() + " layout controls");
        }

        System.out.println("Entry point calculation done.");

        // Clean up everything we no longer need
        soot.G.reset();

        // Create the SourceSinkManager
        {
            Set<SootMethodAndClass> callbacks = new HashSet<>();
            for (Set<SootMethodAndClass> methods : callbackMethods.values())
                callbacks.addAll(methods);

            sourceSinkManager = new AccessPathBasedSourceSinkManager(
                    sourceSinkProvider.getSources(),
                    sourceSinkProvider.getSinks(),
                    callbacks,
                    config.getLayoutMatchingMode(),
                    lfp == null ? null : lfp.getUserControlsByID());

            sourceSinkManager.setAppPackageName(appPackageName);
            sourceSinkManager.setResourcePackages(resourcePackages);
            sourceSinkManager.setEnableCallbackSources(config.getEnableCallbackSources());
        }

        entryPointCreator = createEntryPointCreator();
    }

    /**
     * Adds a method to the set of callback method
     *
     * @param layoutClass    The layout class for which to register the callback
     * @param callbackMethod The callback method to register
     */
    private void addCallbackMethod(String layoutClass, AndroidMethod callbackMethod) {
        Set<SootMethodAndClass> methods = callbackMethods.get(layoutClass);
        if (methods == null) {
            methods = new HashSet<SootMethodAndClass>();
            callbackMethods.put(layoutClass, methods);
        }
        methods.add(new AndroidMethod(callbackMethod));
    }

    /**
     * 计算出XML文件里面的回调方法
     *
     * @param resParser 二进制资源文件解析器
     * @param lfp       布局文件解析器
     * @throws IOException
     */
    private void calculateCallbackMethods(ARSCFileParser resParser, LayoutFileParser lfp) throws IOException {
        AnalyzeJimpleClass jimpleClass = null;

        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;

            // Create the new iteration of the main method
            soot.G.reset();
            initializeSoot();
            createMainMethod();

            if (jimpleClass == null) {
                // Collect the callback interfaces implemented in the app's
                // source code
                if (callbackClasses == null) {
                    jimpleClass = new AnalyzeJimpleClass(config, entrypoints);
                } else {
                    jimpleClass = new AnalyzeJimpleClass(config, entrypoints, callbackClasses);
                }
                jimpleClass.collectCallbackMethods();

                // Find the user-defined sources in the layout XML files. This
                // only needs to be done once, but is a Soot phase.
                lfp.parseLayoutFile(apkFileLocation, entrypoints);
            } else
                jimpleClass.collectCallbackMethodsIncremental();

            // Run the soot-based operations
            PackManager.v().getPack("wjpp").apply();
            PackManager.v().getPack("cg").apply();
            PackManager.v().getPack("wjtp").apply();

            // Collect the results of the soot-based phases
            for (Entry<String, Set<SootMethodAndClass>> entry : jimpleClass.getCallbackMethods().entrySet()) {
                if (callbackMethods.containsKey(entry.getKey())) {
                    if (callbackMethods.get(entry.getKey()).addAll(entry.getValue()))
                        hasChanged = true;
                } else {
                    callbackMethods.put(entry.getKey(), new HashSet<>(entry.getValue()));
                    hasChanged = true;
                }
            }

            if (entrypoints.addAll(jimpleClass.getDynamicManifestComponents()))
                hasChanged = true;
        }

        // Collect the XML-based callback methods
        for (Entry<String, Set<Integer>> lcentry : jimpleClass.getLayoutClasses().entrySet()) {
            final SootClass callbackClass = Scene.v().getSootClass(lcentry.getKey());

            for (Integer classId : lcentry.getValue()) {
                AbstractResource resource = resParser.findResource(classId);
                if (resource instanceof StringResource) {
                    final String layoutFileName = ((StringResource) resource).getValue();

                    // Add the callback methods for the given class
                    Set<String> callbackMethods = lfp.getCallbackMethods().get(layoutFileName);
                    if (callbackMethods != null) {
                        for (String methodName : callbackMethods) {
                            final String subSig = "void " + methodName + "(android.view.View)";

                            // The callback may be declared directly in the
                            // class
                            // or in one of the superclasses
                            SootClass currentClass = callbackClass;
                            while (true) {
                                SootMethod callbackMethod = currentClass.getMethodUnsafe(subSig);
                                if (callbackMethod != null) {
                                    addCallbackMethod(callbackClass.getName(), new AndroidMethod(callbackMethod));
                                    break;
                                }
                                if (!currentClass.hasSuperclass()) {
                                    System.err.println("Callback method " + methodName + " not found in class "
                                            + callbackClass.getName());
                                    break;
                                }
                                currentClass = currentClass.getSuperclass();
                            }
                        }
                    }

                    // For user-defined views, we need to emulate their
                    // callbacks
                    Set<LayoutControl> controls = lfp.getUserControls().get(layoutFileName);
                    if (controls != null)
                        for (LayoutControl lc : controls)
                            registerCallbackMethodsForView(callbackClass, lc);
                } else
                    System.err.println("Unexpected resource type for layout class");
            }
        }

        // Add the callback methods as sources and sinks
        {
            Set<SootMethodAndClass> callbacksPlain = new HashSet<SootMethodAndClass>();
            for (Set<SootMethodAndClass> set : callbackMethods.values())
                callbacksPlain.addAll(set);
            System.out.println("Found " + callbacksPlain.size() + " callback methods for "
                    + callbackMethods.size() + " components");
        }
    }

    /**
     * Registers the callback methods in the given layout control so that they
     * are included in the dummy main method
     *
     * @param callbackClass The class with which to associate the layout
     *                      callbacks
     * @param lc            The layout control whose callbacks are to be associated with
     *                      the given class
     */
    private void registerCallbackMethodsForView(SootClass callbackClass, LayoutControl lc) {
        // Ignore system classes
        if (callbackClass.getName().startsWith("android.") ||
                lc.getViewClass().getName().startsWith("android."))
            return;
        // 判断当前class是否为view
        {
            SootClass sc = lc.getViewClass();
            boolean isView = false;
            while (sc.hasSuperclass()) {
                if (sc.getName().equals("android.view.View")) {
                    isView = true;
                    break;
                }
                sc = sc.getSuperclass();
            }
            if (!isView)
                return;
        }

        // There are also some classes that implement interesting callback
        // methods.
        // We model this as follows: Whenever the user overwrites a method in an
        // Android OS class, we treat it as a potential callback.
        SootClass sc = lc.getViewClass();
        Set<String> systemMethods = new HashSet<String>(10000);
        for (SootClass parentClass : Scene.v().getActiveHierarchy().getSuperclassesOf(sc)) {
            if (parentClass.getName().startsWith("android."))
                for (SootMethod sm : parentClass.getMethods())
                    if (!sm.isConstructor())
                        systemMethods.add(sm.getSubSignature());
        }

        // Scan for methods that overwrite parent class methods
        for (SootMethod sm : sc.getMethods())
            if (!sm.isConstructor())
                if (systemMethods.contains(sm.getSubSignature()))
                    // This is a real callback method
                    addCallbackMethod(callbackClass.getName(), new AndroidMethod(sm));
    }

    /**
     * 基于当前回调函数，创建main方法，并注入到Soot Scene里面去
     */
    private void createMainMethod() {
        // Always update the entry point creator to reflect the newest set
        // of callback methods
        SootMethod entryPoint = createEntryPointCreator().createDummyMain();
        Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
        if (Scene.v().containsClass(entryPoint.getDeclaringClass().getName()))
            Scene.v().removeClass(entryPoint.getDeclaringClass());
        Scene.v().addClass(entryPoint.getDeclaringClass());
    }

    /**
     * Gets the source/sink manager constructed for FlowDroid. Make sure to call calculateSourcesSinksEntryPoints()
     * first, or you will get a null result.
     *
     * @return FlowDroid's source/sink manager
     */
    public AccessPathBasedSourceSinkManager getSourceSinkManager() {
        return sourceSinkManager;
    }

    /**
     * Builds the classpath for this analysis
     *
     * @return The classpath to be used for the taint analysis
     */
    private String getClasspath() {
        String classpath = forceAndroidJar ? androidJar
                : Scene.v().getAndroidJarPath(androidJar, apkFileLocation);
        if (additionalClasspath != null && !additionalClasspath.isEmpty())
            classpath += File.pathSeparator + additionalClasspath;
        return classpath;
    }

    /**
     * Initializes soot for running the soot-based phases of the application metadata analysis
     */
    private void initializeSoot() {
        Options.v().set_no_bodies_for_excluded(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        Options.v().set_whole_program(true);
        Options.v().set_process_dir(Collections.singletonList(apkFileLocation));
        Options.v().set_soot_classpath(getClasspath());
        if (forceAndroidJar)
            Options.v().set_force_android_jar(androidJar);
        else
            Options.v().set_android_jars(androidJar);
        Options.v().set_src_prec(Options.src_prec_apk_class_jimple);
        Options.v().set_keep_line_number(false);
        Options.v().set_keep_offset(false);
        Main.v().autoSetOptions();

        // Configure the callgraph algorithm
        switch (config.getCallgraphAlgorithm()) {
            case AutomaticSelection:
                Options.v().setPhaseOption("cg.spark", "on");
                break;
            case RTA:
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "rta:true");
                break;
            case VTA:
                Options.v().setPhaseOption("cg.spark", "on");
                Options.v().setPhaseOption("cg.spark", "vta:true");
                break;
            default:
                throw new RuntimeException("Invalid callgraph algorithm");
        }

        // Load whetever we need
        Scene.v().loadNecessaryClasses();
    }

    /**
     * 运行数据流分析.
     *
     * @return 数据流分析结果
     */
    public InfoflowResults runInfoflow() {
        return runInfoflow(null);
    }

    /**
     * 运行数据流分析.确保首先收集sources, sinks, 和entry points的集合.
     *
     * @param onResultsAvailable 当数据流结果出来之后运行该回调函数
     * @return 数据流分析结果
     */
    public InfoflowResults runInfoflow(ResultsAvailableHandler onResultsAvailable) {
        if (sourceSinkProvider == null)
            throw new RuntimeException("Sources and/or sinks not calculated yet");

        System.out.println("Running data flow analysis on " + apkFileLocation + " with " + getSources().size()
                + " sources and " + getSinks().size() + " sinks...");
        Infoflow info;
//        //设置路径
//        config.setPathBuilder(DefaultPathBuilderFactory.PathBuilder.ContextInsensitiveSourceFinder);
        if (cfgFactory == null)
            info = new Infoflow(androidJar, forceAndroidJar, null,
                    new DefaultPathBuilderFactory(config.getPathBuilder(),
                            config.getComputeResultPaths()));
        else
            info = new Infoflow(androidJar, forceAndroidJar, cfgFactory,
                    new DefaultPathBuilderFactory(config.getPathBuilder(),
                            config.getComputeResultPaths()));

        final String path;
        if (forceAndroidJar)
            path = androidJar;
        else
            path = Scene.v().getAndroidJarPath(androidJar, apkFileLocation);

        info.setTaintWrapper(taintWrapper);
        if (onResultsAvailable != null)
            info.addResultsAvailableHandler(onResultsAvailable);

        System.out.println("Starting infoflow computation...");
        info.setConfig(config);
        info.setSootConfig(sootConfig);

        if (null != ipcManager) {
            info.setIPCManager(ipcManager);
        }

        info.computeInfoflow(apkFileLocation, path, entryPointCreator, sourceSinkManager);
        maxMemoryConsumption = info.getMaxMemoryConsumption();
//        collectedSources = info.getCollectedSources();
//        collectedSinks = info.getCollectedSinks();

        return info.getResults();
    }

    /**
     * 创建入口点Creator
     */
    private AndroidEntryPointCreator createEntryPointCreator() {
        AndroidEntryPointCreator entryPointCreator = new AndroidEntryPointCreator(new ArrayList<String>(
                entrypoints));
        Map<String, List<String>> callbackMethodSigs = new HashMap<String, List<String>>();
        for (String className : callbackMethods.keySet()) {
            List<String> methodSigs = new ArrayList<>();
            callbackMethodSigs.put(className, methodSigs);

            for (SootMethodAndClass am : callbackMethods.get(className))
                methodSigs.add(am.getSignature());
        }
        entryPointCreator.setCallbackFunctions(callbackMethodSigs);
        return entryPointCreator;
    }

    public AndroidEntryPointCreator getEntryPointCreator() {
        return entryPointCreator;
    }

    public IInfoflowConfig getSootConfig() {
        return this.sootConfig;
    }

    public void setSootConfig(IInfoflowConfig config) {
        this.sootConfig = config;
    }

    public void setIcfgFactory(BiDirICFGFactory factory) {
        this.cfgFactory = factory;
    }

    public long getMaxMemoryConsumption() {
        return this.maxMemoryConsumption;
    }

    public InfoflowAndroidConfiguration getConfig() {
        return this.config;
    }

    public void setConfig(InfoflowAndroidConfiguration config) {
        this.config = config;
    }

}
