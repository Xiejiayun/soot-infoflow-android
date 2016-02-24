package nju.software.extractor;

import nju.software.config.AndroidBootConfig;
import nju.software.config.AndroidSootConfig;
import nju.software.constants.AndroidEntryPointConstants;
import nju.software.parsers.PermissionPointParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

import java.io.IOException;
import java.util.*;

/**
 * 入口点提取，用于提取某个app里面的所有的入口点
 * Created by Xie on 2016/1/19.
 */
public class EntryPointExtractor {

    private static Logger logger = LoggerFactory.getLogger(EntryPointExtractor.class);

    private static EntryPointExtractor entryPointExtractor = new EntryPointExtractor();

    private static ProcessManifest processManifest;

    private static Set<AndroidMethod> permissionMethods = new HashSet<>();

    //指向需要分析的APK的地址
    private static String APK_FILE_PATH = null;

    public EntryPointExtractor() {
    }

    public static EntryPointExtractor v() {
        return entryPointExtractor;
    }

    public static void main(String[] args) {
        String apkFilePath = "SendSMS.apk";
        EntryPointExtractor.v().init(apkFilePath);
        List list = EntryPointExtractor.v().getAllEntryPointMethods(apkFilePath);
        List list2 = EntryPointExtractor.v().getAllLifeCycleMethods(apkFilePath);
        List list3 = EntryPointExtractor.v().getAllLifeCycleCallUnits(apkFilePath);
        Map map = EntryPointExtractor.v().getReachableAndroidMethodMapping(apkFilePath);
        System.out.println("Done");

    }

    /**
     * 针对某个APK初始化Android分析环境
     *
     * @param apkFilePath
     */
    public static void init(String apkFilePath) {
        long start = System.nanoTime();
        EntryPointExtractor.v().initProcessManifest(apkFilePath);
        AndroidSootConfig.setApkFilePath(apkFilePath);
        AndroidSootConfig.initSoot();
        AndroidBootConfig.initMappingFiles();
        logger.info("Init in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    /**
     * 初始化Manifest文件处理器
     *
     * @param apkFilePath
     */
    private void initProcessManifest(String apkFilePath) {
        try {
            processManifest = new ProcessManifest(apkFilePath);
        } catch (IOException e) {
             e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有的入口点的类
     *
     * @param apkFilePath
     * @return
     */
    public Set<String> getAllEntryPointClasses(String apkFilePath) {
        initProcessManifest(apkFilePath);
        Set<String> entryPoints = processManifest.getEntryPointClasses();
        return entryPoints;
    }

    /**
     * 获取所有入口点类里面的所有的入口类的方法。
     *
     * @param apkFilePath
     * @return
     */
    public List<SootMethod> getAllEntryPointMethods(String apkFilePath) {
        long start = System.nanoTime();
        List<SootMethod> androidMethods = new ArrayList<>();
        Set<String> classes = EntryPointExtractor.v().processManifest.getEntryPointClasses();
        for (String clazz : classes) {
            Scene.v().loadClassAndSupport(clazz);
            SootClass sootClass = Scene.v().getSootClass(clazz);
            List<SootMethod> sootMethods = sootClass.getMethods();
            androidMethods.addAll(sootMethods);
        }
        logger.info("GetAllEntryPointClassMethods in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return androidMethods;
    }

    /**
     * 获取某个EntryPoint类的方法
     *
     * @param sootClazz
     * @return
     */
    public List<SootMethod> getSootMethodsInEntryPoint(String sootClazz) {
        long start = System.nanoTime();
        List<SootMethod> androidMethods = new ArrayList<>();
        Scene.v().loadClassAndSupport(sootClazz);
        SootClass sootClass = Scene.v().getSootClass(sootClazz);
        List<SootMethod> sootMethods = sootClass.getMethods();
        androidMethods.addAll(sootMethods);
        logger.info("GetSootMethodsInEntryPoint in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return androidMethods;
    }

    public Map<String, List<AndroidMethod>> getReachableAndroidMethodMapping(String apkFileLocaction) {
        Map<String, List<AndroidMethod>> resultMapping = new HashMap<>();
        Set<String> entryClasses = getAllEntryPointClasses(apkFileLocaction);
        for (String entryClass : entryClasses) {
            List<MethodOrMethodContext> methodOrMethodContexts = getMethodOrMethodContextInEntryPoint(entryClass);
            List<AndroidMethod> androidMethods = calcuPermission(methodOrMethodContexts);
            resultMapping.put(entryClass, androidMethods);
        }
        return resultMapping;
    }

    /**
     * 获取某个EntryPoint类的方法
     *
     * @param sootClazz
     * @return
     */
    public List<MethodOrMethodContext> getMethodOrMethodContextInEntryPoint(String sootClazz) {
        long start = System.nanoTime();
        List<MethodOrMethodContext> androidMethods = new ArrayList<>();
        Scene.v().loadClassAndSupport(sootClazz);
        SootClass sootClass = Scene.v().getSootClass(sootClazz);
        List<SootMethod> sootMethods = sootClass.getMethods();
        androidMethods.addAll(sootMethods);
        logger.info("GetSootMethodsInEntryPoint in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return androidMethods;
    }

    /**
     * 用来计算所有可达的（能够从入口点到达的）方法上面的权限使用情况
     *
     * @param methodOrMethodContexts
     * @return List<AndroidMethod>
     */
    public List<AndroidMethod> calcuPermission(List<MethodOrMethodContext> methodOrMethodContexts) {
        List<AndroidMethod> androidMethods = new ArrayList<>();
        Scene scene = Scene.v();
        CHATransformer.v().transform();
        ReachableMethods rm = new ReachableMethods(Scene.v().getCallGraph(), methodOrMethodContexts);
        rm.update();
        Iterator<MethodOrMethodContext> reachableMethods = rm.listener();
        while (reachableMethods.hasNext()) {
            SootMethod method = reachableMethods.next().method();
            AndroidMethod androidMethod = calcuPermission(method);
            androidMethods.add(androidMethod);
        }
        return androidMethods;
    }

    /**
     * 用来计算每个Android里面的权限使用情况，并添加相应的权限使用点
     *
     * @param sootMethod
     * @return AndroidMethod
     */
    private AndroidMethod calcuPermission(SootMethod sootMethod) {
        AndroidMethod androidMethod = new AndroidMethod(sootMethod);
        // Do not analyze system classes
        if (sootMethod.getDeclaringClass().getName().startsWith("android.")
                || sootMethod.getDeclaringClass().getName().startsWith("java."))
            return androidMethod;
        if (!sootMethod.isConcrete())
            return androidMethod;

        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(sootMethod.retrieveActiveBody());
        SmartLocalDefs smd = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));

        Set<SootClass> callbackClasses = new HashSet<SootClass>();
        for (Unit u : sootMethod.retrieveActiveBody().getUnits()) {
            Stmt stmt = (Stmt) u;
            // Callback registrations are always instance invoke expressions
            if (stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                if (PermissionPointParser.methodPermissionMap.keySet().contains(stmt.getInvokeExpr().toString()))
                    androidMethod.addPermission(PermissionPointParser.methodPermissionMap.get(""));
                InstanceInvokeExpr iinv = (InstanceInvokeExpr) stmt.getInvokeExpr();
                System.out.println();
            }
        }
        return androidMethod;
    }

    /**
     * 获取所有的生命周期入口方法
     *
     * @param apkPath
     * @return
     */
    public List<SootMethod> getAllLifeCycleMethods(String apkPath) {
        long start = System.nanoTime();
        List<SootMethod> entryPointMethods = new ArrayList<>();
        List<SootMethod> allMethods = getAllEntryPointMethods(apkPath);
        for (SootMethod method : allMethods) {
            if (isLifeCycleMethod(method.getDeclaringClass(), method)) {
                entryPointMethods.add(method);
            }
        }
        logger.info("GetAllEntryPointMethods in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return entryPointMethods;
    }

    public List<Unit> getAllLifeCycleCallUnits(String apkFilePath) {
        List<Unit> callUnits = new ArrayList<>();
        long start = System.nanoTime();
        List<SootMethod> lifeCycleMethods = getAllLifeCycleMethods(apkFilePath);
        for (SootMethod sootMethod : lifeCycleMethods) {
            callUnits.addAll(MethodCallExtractor.getCallUnitsInMethod(sootMethod));
        }
        logger.info("getAllLifeCycleCallUnits in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return callUnits;
    }

    /**
     * 循环判断是否为四大组件之一的生命周期方法
     *
     * @param sootClass
     * @param sootMethod
     * @return
     */
    private boolean isLifeCycleMethod(SootClass sootClass, SootMethod sootMethod) {
        if (sootClass.getName().equals("java.lang.Object"))
            return false;
        if (AndroidEntryPointConstants.isLifecycleClass(sootClass.getName())) {
            if (AndroidEntryPointConstants.isLifecycleMethod(sootClass.getName(), sootMethod.toString())) {
                return true;
            } else {
                return false;
            }
        }
        return isLifeCycleMethod(sootClass.getSuperclass(), sootMethod);
    }

}