package nju.software.infoflow;

import soot.jimple.infoflow.AbstractInfoflow;
import soot.jimple.infoflow.entryPointCreators.IEntryPointCreator;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.source.ISourceSinkManager;

/**
 * Created by Xie on 2016/2/22.
 */
public class SourceSinkInfoflow extends AbstractInfoflow {

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
