package nju.software.model;

import soot.SootMethod;
import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.data.SootMethodAndClass;

import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 权限方法
 * Created by Xie on 2016/1/19.
 */
public class PermissionMethod extends AndroidMethod {

    private List<Permission> mtthodPermissions = new ArrayList<>();

    public PermissionMethod(String methodName, String returnType, String className) {
        super(methodName, returnType, className);
    }

    public PermissionMethod(String methodName, List<String> parameters, String returnType, String className) {
        super(methodName, parameters, returnType, className);
    }

    public PermissionMethod(String methodName, List<String> parameters, String returnType, String className, Set<String> permissions) {
        super(methodName, parameters, returnType, className, permissions);
    }

    public PermissionMethod(SootMethod sm) {
        super(sm);
    }

    public PermissionMethod(SootMethodAndClass methodAndClass) {
        super(methodAndClass);
    }


}
