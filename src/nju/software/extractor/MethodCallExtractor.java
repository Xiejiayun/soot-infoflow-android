package nju.software.extractor;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.util.Chain;

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
            Chain<Unit> units = method.getActiveBody().getUnits();
            for (Unit unit : units)
                if (((Stmt) unit).containsInvokeExpr()) {
                    if (callSites == null)
                        callSites = new ArrayList<>();
                    callSites.add(unit);
                }
        }
        return callSites;
    }
}
