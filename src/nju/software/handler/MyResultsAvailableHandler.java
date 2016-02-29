package nju.software.handler;

/**
 * Created by lab on 16-2-25.
 */

import nju.software.enums.InfoflowEnum;
import nju.software.parsers.PermissionPointParser;
import nju.software.util.FileUtils;
import soot.SootMethod;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.handlers.ResultsAvailableHandler;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.jimple.infoflow.solver.cfg.IInfoflowCFG;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 这个类很有用，最后是通过它里面的onResultsAvailable方法查找出从source到sink的路径
 */
public class MyResultsAvailableHandler implements
        ResultsAvailableHandler {

    private BufferedWriter wr;
    private InfoflowEnum infoflowEnum = InfoflowEnum.SOURCETOSINK;
    private Set<AndroidMethod> entries = new HashSet<>();

    private String apkName;
    private String fileSpliter = "/";

    public MyResultsAvailableHandler() {
        new MyResultsAvailableHandler(null, InfoflowEnum.SOURCETOSINK);
    }

    public MyResultsAvailableHandler(String apkName) {
        new MyResultsAvailableHandler(apkName, InfoflowEnum.SOURCETOSINK);
    }

    public MyResultsAvailableHandler(String apkName, InfoflowEnum infoflowEnum) {
        if (apkName != null && apkName != "") {
            if (apkName.endsWith(".apk"))
                apkName = FileUtils.getFileName(apkName);
            String outputFileName = apkName + fileSpliter + infoflowEnum+".txt";
            try {
                this.wr = new BufferedWriter(
                        new FileWriter(
                                new File(outputFileName)));
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

    @Override
    public void onResultsAvailable(
            IInfoflowCFG cfg, InfoflowResults results) {
        if (results == null) {
            print("No results found.");
        } else {
            for (ResultSinkInfo sink : results.getResults().keySet()) {
                print(" Found a flow to sink " + sink + ", from the following sources:");
                for (ResultSourceInfo source : results.getResults().get(sink)) {
                    print("\t- " + source.getSource() + " (in "
                            + cfg.getMethodOf(source.getSource()).getSignature() + ")");
                    if (source.getPath() != null)
                        print("\t\ton Path " + Arrays.toString(source.getPath()));
                }
            }
            //TODO 存在一个问题就是貌似从constructor中调用的并不能够很好的处理
            //这边可以根据sink的类型找出其调用的permission图，计算入口点的AndroidMethod
            if (getInfoflowEnum() == InfoflowEnum.ENTRYTOSOURCE) { //当且仅当source为entrypoint，sink为sources的时候calculateAndroidMethod为真
                PermissionPointParser.v().init();
                for (ResultSinkInfo sink : results.getResults().keySet()) {
                    for (ResultSourceInfo source : results.getResults().get(sink)) {
                        Stmt entryStmt = source.getSource();
                        Stmt sourceStmt = sink.getSink();
                        SootMethod entryMethod = cfg.getMethodOf(source.getSource());
                        SootMethod sourceMethod = cfg.getMethodOf(sink.getSink());
                        AndroidMethod entry = new AndroidMethod(entryMethod);
                        if (sourceStmt.containsInvokeExpr() && sourceStmt.getInvokeExpr() instanceof InstanceInvokeExpr) {
                            if (PermissionPointParser.methodPermissionMap.keySet().contains(sourceStmt.getInvokeExpr().toString())) {
                                entry.addPermission(PermissionPointParser.methodPermissionMap.get(sourceStmt.getInvokeExpr().toString()));
                            }
                        }
                        entries.add(entry);
                        if (source.getPath() != null)
                            print("\t\ton Path " + Arrays.toString(source.getPath()));
                    }
                }
            }
        }
    }

    private void print(String string) {
        try {
            System.out.println(string);
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