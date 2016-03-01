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


    public EntryPointExtractor() {
    }

    public static EntryPointExtractor v() {
        return entryPointExtractor;
    }

    public static void main(String[] args) {
        String apkFilePath = "InterAppCommunication/SendSMS.apk";
//        String [] apks = FileBatchExecutor.getAllApkFiles("apks");
//        for (String apkFilePath : apks) {
            EntryPointExtractor.v().init(apkFilePath);
            List list = EntryPointExtractor.v().getAllEntryPointMethods(apkFilePath);
            List list2 = EntryPointExtractor.v().getAllLifeCycleMethods(apkFilePath);
            List list3 = EntryPointExtractor.v().getAllLifeCycleCallUnits(apkFilePath);
            List list4 = EntryPointExtractor.v().getAllEntryPointMethodsSignatures(apkFilePath);
            List list5 = EntryPointExtractor.v().getAllLifeCycleAndroidMethods(apkFilePath);
            Map map = EntryPointExtractor.v().getReachableAndroidMethodMapping(apkFilePath);
            System.out.println("Inner Done");
//        }
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
     * 获取所有入口点类里面的所有的入口类的方法
     *
     * @param apkFilePath apk文件路径
     * @return 所有的入口点方法
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
     * 获取所有入口点方法的签名
     *
     * @param apkFilePath apk文件路径
     * @return 所有入口点方法的签名
     */
    public List<String> getAllEntryPointMethodsSignatures(String apkFilePath) {
        List<String> signatures = new ArrayList<>();
        List<SootMethod> entryPointMethods = getAllEntryPointMethods(apkFilePath);
        for (SootMethod sootMethod : entryPointMethods) {
            String signature = sootMethod.getSignature();
            signatures.add(signature);
        }
        return signatures;
    }

    /**
     * 根据类的名称获取某个EntryPoint类的方法
     *
     * @param sootClazz 类的名称
     * @return 获取的EntryPoint中类的方法
     */
    public List<SootMethod> getSootMethodsInEntryPoint(String sootClazz) {
        long start = System.nanoTime();
        List<SootMethod> sootMethods = new ArrayList<>();
        Scene.v().loadClassAndSupport(sootClazz);
        SootClass sootClass = Scene.v().getSootClass(sootClazz);
        sootMethods = sootClass.getMethods();
        logger.info("GetSootMethodsInEntryPoint in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return sootMethods;
    }

    /**
     * 获取入口类和可达的Android方法的映射关系
     *
     * @param apkFilePath apk文件路径
     * @return 所有入口类和其所包含的Android方法的映射关系
     */
    public Map<String, List<AndroidMethod>> getReachableAndroidMethodMapping(String apkFilePath) {
        Map<String, List<AndroidMethod>> resultMapping = new HashMap<>();
        Set<String> entryClasses = getAllEntryPointClasses(apkFilePath);
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
     * @param sootClazz 根据SootClass获得其中的方法上下文
     * @return SootClass类中的方法上下文
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

        for (Unit u : sootMethod.retrieveActiveBody().getUnits()) {
            Stmt stmt = (Stmt) u;
            // Callback registrations are always instance invoke expressions

            if (stmt.containsInvokeExpr() && stmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                String permission = stmt.getInvokeExpr().getMethodRef().getSignature();
                if (PermissionPointParser.methodPermissionMap.keySet().contains(stmt.getInvokeExpr().toString()))
                    androidMethod.addPermission(PermissionPointParser.methodPermissionMap.get(stmt.getInvokeExpr().toString()));
            }
        }
        return androidMethod;
    }

    /**
     * 获取所有的生命周期入口方法
     *
     * @param apkFilePath
     * @return
     */
    public List<SootMethod> getAllLifeCycleMethods(String apkFilePath) {
        long start = System.nanoTime();
        List<SootMethod> entryPointMethods = new ArrayList<>();
        List<SootMethod> allMethods = getAllEntryPointMethods(apkFilePath);
        for (SootMethod method : allMethods) {
            if (isLifeCycleMethod(method.getDeclaringClass(), method)) {
                entryPointMethods.add(method);
            }
        }
        logger.info("GetAllEntryPointMethods in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return entryPointMethods;
    }

    /**
     * 获取所有生命周期方法里面的Android方法
     *
     * @param apkFilePath apk文件路径
     * @return Android方法列表
     */
    public List<AndroidMethod> getAllLifeCycleAndroidMethods(String apkFilePath) {
        List<AndroidMethod> androidMethods = new ArrayList<>();
        List<SootMethod> sootMethods = getAllLifeCycleMethods(apkFilePath);
        for (SootMethod sootMethod : sootMethods) {
            AndroidMethod androidMethod = calcuPermission(sootMethod);
            androidMethods.add(androidMethod);
        }
        return androidMethods;
    }

    public Set<AndroidMethod> getAllLifeCycleAndroidMethodsSets(String apkFilePath) {
        Set<AndroidMethod> androidMethods = new HashSet<>();
        List<SootMethod> sootMethods = getAllLifeCycleMethods(apkFilePath);
        for (SootMethod sootMethod : sootMethods) {
            AndroidMethod androidMethod = calcuPermission(sootMethod);
            androidMethods.add(androidMethod);
        }
        return androidMethods;
    }

    /**
     * 根据apk文件路径获取所有生命周期方法里面的签名
     *
     * @param apkFilePath apk文件路径
     * @return 所有生命周期方法的签名
     */
    public List<String> getAllLifeCycleMethodsSignatures(String apkFilePath) {
        long start = System.nanoTime();
        List<String> signatures = new ArrayList<>();
        List<SootMethod> allLifeCycleMethods = getAllLifeCycleMethods(apkFilePath);
        for (SootMethod lifeCycleMethod : allLifeCycleMethods) {
            String signature = lifeCycleMethod.getSignature();
            signatures.add(signature);
        }
        logger.info("getAllLifeCycleMethodsSignatures in EntryPointExtractor costs " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        return signatures;
    }


    /**
     * 获取所有生命周期的调用单元
     *
     * @param apkFilePath apk文件路径
     * @return 所有生命周期方法里面的调用单元
     */
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
     * @param sootClass  类
     * @param sootMethod 方法
     * @return 是否为生命周期方法
     */
    private boolean isLifeCycleMethod(SootClass sootClass, SootMethod sootMethod) {
        if (sootClass == null || sootMethod == null)
            return false;
        if (sootClass.getName() == null)
            return false;
        if (sootClass.getName().equals("java.lang.Object"))
            return false;
        if (AndroidEntryPointConstants.isLifecycleClass(sootClass.getName())) {
            if (AndroidEntryPointConstants.isLifecycleMethod(
                    sootClass.getName(), sootMethod.toString())) {
                return true;
            } else {
                return false;
            }
        }
        return isLifeCycleMethod(sootClass.getSuperclass(), sootMethod);
    }

}