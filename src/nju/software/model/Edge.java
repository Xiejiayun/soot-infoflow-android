package nju.software.model;

import soot.jimple.infoflow.android.data.AndroidMethod;

/**
 * 方法的调用边，这是一个单向的
 * Created by Xie on 2016/1/19.
 */
public class Edge {
    /*
    方法的调用者
     */
    private AndroidMethod caller;
    /*
    方法的被调用者
     */
    private AndroidMethod callee;

    public Edge() {
    }

    public Edge(AndroidMethod caller, AndroidMethod callee) {
        this.caller = caller;
        this.callee = callee;
    }
}
