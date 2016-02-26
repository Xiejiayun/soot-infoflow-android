package nju.software.extractor;

import nju.software.model.Path;
import nju.software.parsers.ExitPointParser;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 出口点方法提取器
 * Created by lab on 16-2-25.
 */
public class ExitPointExtractor {

    /*
 从入口点到沉淀点的所有的路径
  */
    private List<Path> paths;

    private ExitPointExtractor exitPointExtractor = new ExitPointExtractor();

    public ExitPointExtractor v() {
        return exitPointExtractor;
    }


    public static List<AndroidMethod> generateAllExitMethods() {
        List<AndroidMethod> ExitMethods = new ArrayList<>();
        ExitMethods = ExitPointParser.getExitMethods();
        return ExitMethods;
    }

    /**
     * 从文件中获取所有沉淀点的集合（从Exits文件中读取的）
     *
     * @return 沉淀点的集合
     */
    public static Set<AndroidMethod> generateAllExitMethodsSets() {
        Set<AndroidMethod> ExitMethods = new HashSet<>();
        List<AndroidMethod> ExitAndroidMethods = ExitPointParser.getExitMethods();
        for (AndroidMethod androidMethod : ExitAndroidMethods) {
            ExitMethods.add(androidMethod);
        }
        return ExitMethods;
    }

}
