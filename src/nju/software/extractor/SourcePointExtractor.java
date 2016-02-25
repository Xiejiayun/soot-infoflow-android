package nju.software.extractor;

import nju.software.parsers.SourcePointParser;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Xie on 2016/1/19.
 */
public class SourcePointExtractor {

    private SourcePointExtractor sourcePointExtractor = new SourcePointExtractor();

    public SourcePointExtractor v() {
        return sourcePointExtractor;
    }

    /**
     * 扫描整个app，将其中包含具体的沉淀点的方法提取出一个列表
     */
    public List<AndroidMethod> generateAllSourceMethods(String apkFilePath) {
        List<AndroidMethod> androidMethods = new ArrayList<>();

        return androidMethods;
    }

    public static List<AndroidMethod> generateAllSourceMethods() {
        List<AndroidMethod> sourceMethods = new ArrayList<>();
        sourceMethods = SourcePointParser.getSourceMethods();
        return sourceMethods;
    }

    /**
     * 从文件中获取所有源点的集合（从sources文件中读取的）
     *
     * @return 源点的集合
     */
    public static Set<AndroidMethod> generateAllSourceMethodsSets() {
        Set<AndroidMethod> sourceMethods = new HashSet<>();
        List<AndroidMethod> sourceAndroidMethods = SourcePointParser.getSourceMethods();
        for (AndroidMethod androidMethod : sourceAndroidMethods) {
            sourceMethods.add(androidMethod);
        }
        return sourceMethods;
    }

}