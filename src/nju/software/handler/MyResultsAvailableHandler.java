package nju.software.handler;

/**
 * Created by lab on 16-2-25.
 */

import nju.software.enums.InfoflowEnum;
import nju.software.parsers.PermissionPointParser;
import nju.software.util.FileUtils;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * 这个类很有用，最后是通过它里面的onResultsAvailable方法查找出从source到sink的路径
 */
public class MyResultsAvailableHandler implements
        ResultsAvailableHandler {

    /**
     * 写缓冲
     */
    private BufferedWriter wr;
    /**
     * 信息流枚举，默认从源点到沉淀点
     */
    private InfoflowEnum infoflowEnum = InfoflowEnum.SOURCETOSINK;
    /**
     * 入口点SootMethod和权限列表映射关系
     */
    private Map<String, Set<String>> map = new HashMap<>();
    /**
     * 分析的apk名称
     */
    private String apkName;
    /**
     * 文件分隔符
     */
    private String fileSpliter = "/";

    public MyResultsAvailableHandler() {
        new MyResultsAvailableHandler(null, InfoflowEnum.SOURCETOSINK);
    }

    public MyResultsAvailableHandler(String apkName) {
        new MyResultsAvailableHandler(apkName, InfoflowEnum.SOURCETOSINK);
    }

    /**
     * 构造器
     *
     * @param apkName      分析的apk名称
     * @param infoflowEnum 信息流枚举
     */
    public MyResultsAvailableHandler(String apkName, InfoflowEnum infoflowEnum) {
        if (apkName != null && apkName != "") {
            if (apkName.endsWith(".apk"))
                apkName = FileUtils.getFileName(apkName);
            this.apkName = apkName;
            String outputFileName = apkName + "/res/raw/" + infoflowEnum.getType() + ".txt";

            try {
                File dir = new File(apkName + "/res/raw");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                System.out.println(dir.exists());
                File file = new File(outputFileName);
                if(!file.exists()) {
                    file.createNewFile();
                }
                System.out.println(file.exists());
                this.wr = new BufferedWriter(
                        new FileWriter(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.wr = null;
        }
    }

    private MyResultsAvailableHandler(BufferedWriter wr) {
        this.wr = wr;
    }

    /**
     * 回调方法
     *
     * @param cfg     程序的控制流图
     * @param results 计算出的结果
     */
    @Override
    public void onResultsAvailable(IInfoflowCFG cfg, InfoflowResults results) {
        if (results == null) {
            print("No results found.");
        } else {
            //如果是从入口点到源点的情况，那么我们通过汇总所有入口点的权限值
            if (getInfoflowEnum() == InfoflowEnum.ENTRYTOSOURCE) {
                PermissionPointParser.v().init();
            }
            //针对每个计算出的沉淀点分析出其源头
            for (ResultSinkInfo sink : results.getResults().keySet()) {
                print("Found a flow to sink " + sink + ", from the following sources:");
                write("Sink:\n" + sink + "\nSources:");
                for (ResultSourceInfo source : results.getResults().get(sink)) {
                    print("" + source.getSource() + " (in "
                            + cfg.getMethodOf(source.getSource()).getSignature() + ")");
                    write("" + source.getSource());
                    if (source.getPath() != null)
                        print("\t\ton Path " + Arrays.toString(source.getPath()));

                    //如果是从入口点到源点的情况，那么我们通过汇总所有入口点的权限值
                    if (getInfoflowEnum() == InfoflowEnum.ENTRYTOSOURCE) {
                        Stmt entryStmt = source.getSource();
                        String entryMethod = "";
                        if (entryStmt.containsInvokeExpr()) {
                            entryMethod = entryStmt.getInvokeExpr().getMethodRef().getSignature();
                        } else {
                            entryMethod = cfg.getMethodOf(source.getSource()).getSignature();
                        }
                        Stmt sourceStmt = sink.getSink();
                        if (sourceStmt.containsInvokeExpr()
                                && sourceStmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            String permissionStmt = sourceStmt.getInvokeExpr().getMethodRef().getSignature();
                            if (PermissionPointParser.methodPermissionMap.keySet().contains(permissionStmt)) {
                                String permission = PermissionPointParser.methodPermissionMap.get(permissionStmt);
                                Set<String> permissions = map.get(entryMethod) == null
                                        ? new HashSet<String>() : map.get(entryMethod);
                                permissions.add(permission);
                                map.put(entryMethod, permissions);
                            }
                        }

                        String file = apkName + fileSpliter + "ENTRYPERMISSIONS.txt";
                        try (BufferedWriter br = new BufferedWriter(
                                new FileWriter(
                                        new File(file)))) {
                            for (String sootMethod : map.keySet()) {

                                System.out.println(sootMethod.toString());
                                br.write(sootMethod.toString() + "\n");
                                Set<String> permissions = map.get(sootMethod);
                                for (String permission : permissions) {
                                    System.out.println(permission);
                                    br.write(permission + "\n");
                                }
                            }
                            br.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }
                }
            }

        }
    }

    /**
     * 用于打印到标准输出
     *
     * @param string
     */
    private void print(String string) {
        System.out.println(string);
    }

    /**
     * 用来写入文件中
     *
     * @param string
     */
    private void write(String string) {
        try {
            if (wr != null) {
                wr.write(string + "\n");
                wr.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public InfoflowEnum getInfoflowEnum() {
        return infoflowEnum;
    }

    public void setInfoflowEnum(InfoflowEnum infoflowEnum) {
        this.infoflowEnum = infoflowEnum;
    }
}