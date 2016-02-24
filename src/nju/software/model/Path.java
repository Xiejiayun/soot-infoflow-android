package nju.software.model;

import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.List;
import java.util.Set;

/**
 * 该路径模型用于映射从公开的入口点到某个沉淀点经过的边的集合
 * Created by Xie on 2016/1/19.
 */
public class Path {

    List<Edge> edges;
    /*
    用来判断是否存在闭环
     */
    Set<AndroidMethod> methodSet;

    private AndroidMethod entry;
    private AndroidMethod sink;

    public Path(AndroidMethod sink, AndroidMethod entry) {
        this.sink = sink;
        this.entry = entry;
    }

    public Path(AndroidMethod entry, AndroidMethod sink, List<Edge> edges) {
        this.entry = entry;
        this.sink = sink;
        this.edges = edges;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public Set<AndroidMethod> getMethodSet() {
        return methodSet;
    }

    public void setMethodSet(Set<AndroidMethod> methodSet) {
        this.methodSet = methodSet;
    }

    public AndroidMethod getEntry() {
        return entry;
    }

    public void setEntry(AndroidMethod entry) {
        this.entry = entry;
    }

    public AndroidMethod getSink() {
        return sink;
    }

    public void setSink(AndroidMethod sink) {
        this.sink = sink;
    }

    /**
     * 覆盖原有的equals方法，使用两个方法点进行匹配
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Path path = (Path) o;

        if (!entry.equals(path.entry)) return false;
        return sink.equals(path.sink);

    }

    @Override
    public int hashCode() {
        int result = entry.hashCode();
        result = 31 * result + sink.hashCode();
        return result;
    }
}
