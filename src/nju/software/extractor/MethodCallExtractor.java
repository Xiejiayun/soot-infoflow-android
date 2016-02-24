package nju.software.extractor;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xie on 2016/1/23.
 */
public class MethodCallExtractor {

    /**
     * 获取方法内部的调用
     * @param method
     * @return
     */
    public static List<Unit> getCallUnitsInMethod(SootMethod method) {
        List<Unit> callSites = new ArrayList<>();
        if (method.hasActiveBody()) {
            for (Unit u : method.getActiveBody().getUnits())
                if (((Stmt) u).containsInvokeExpr()) {
                    if (callSites == null)
                        callSites = new ArrayList<Unit>();
                    callSites.add(u);
                }
        }
        return callSites;
    }
}
