package nju.software.manager;

import soot.jimple.infoflow.AbstractInfoflow;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.source.ISourceSinkManager;

/**
 * 用来管理apk文件的调用关系图
 * Created by Xie on 2016/1/19.
 */
public class CallGraphManager extends AbstractInfoflow {

    /**
     * 构造系统调用图
     */
    public void defaultConstructCallGraph() {
        constructCallgraph();
    }

    @Override
    public void computeInfoflow(String appPath, String libPath, IEntryPointCreator entryPointCreator, ISourceSinkManager sourcesSinks) {

    }

    @Override
    public void computeInfoflow(String appPath, String libPath, String entryPoint, ISourceSinkManager sourcesSinks) {

    }

    @Override
    public InfoflowResults getResults() {
        return null;
    }

    @Override
    public boolean isResultAvailable() {
        return false;
    }
}