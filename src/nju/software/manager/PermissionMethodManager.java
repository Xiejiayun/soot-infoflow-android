package nju.software.manager;

import soot.jimple.infoflow.android.data.AndroidMethod;
import soot.jimple.infoflow.source.DefaultSourceSinkManager;
import soot.jimple.infoflow.source.ISourceSinkManager;

import java.util.HashSet;
import java.util.Set;

/**
 * 主要用作方法级别的权限映射，将方法上标记出相应的权限
 * Created by Xie on 2016/1/19.
 */
public class PermissionMethodManager {

    public Set<AndroidMethod> getAllSinkPermissionMethod(String apkFileLocation) {
        ISourceSinkManager iSourceSinkManager = new DefaultSourceSinkManager(new HashSet<String>(),new HashSet<String>());
        return null;
    }



}
