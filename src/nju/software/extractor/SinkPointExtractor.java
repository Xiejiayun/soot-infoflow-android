package nju.software.extractor;

import nju.software.comparator.AndroidMethodComparator;
import nju.software.model.Path;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Xie on 2016/1/19.
 */
public class SinkPointExtractor {
    /*
    从入口点到沉淀点的所有的路径
     */
    private List<Path> paths;

    /**
     * 扫描整个app，将其中包含具体的沉淀点的方法提取出一个列表
     */
    public List<AndroidMethod> generateAllSinkMethods(String apkFilePath) {
        List<AndroidMethod> androidMethods = new ArrayList<>();

        return androidMethods;
    }

    /**
     * 生成从入口点到沉淀点的路径
     */
    public Path generatePathFromEntryToSink() {
        Path path = null;

        return null;
    }

    /**
     * 判断从一个入口点到一个出口点的路径是否存在
     */
    public boolean isPathExist(String apkFileLocation, AndroidMethod entry, AndroidMethod sink) {
        List<Path> paths = new LinkedList<Path>();
        for (Path path : paths) {
            AndroidMethod entryMethod = path.getEntry();
            AndroidMethod sinkMethod = path.getSink();
            if (AndroidMethodComparator.compare(entryMethod, entry) == 0
                    && AndroidMethodComparator.compare(sinkMethod, sink) == 0)
                return true;
        }
        return false;
    }

    /**
     * 获取某个app中所有从入口点到沉淀点的路径
     *
     * @param apkFileLocation
     * @return
     */
    public List<Path> generateAllPaths(String apkFileLocation) {
        List<Path> paths = new LinkedList<Path>();

        return paths;
    }
}