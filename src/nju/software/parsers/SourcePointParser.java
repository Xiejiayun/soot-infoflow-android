package nju.software.parsers;

import nju.software.constants.FilePathConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 源点解析器
 *
 * Created by Xie on 2016/1/27.
 */
public class SourcePointParser {

    private static Logger logger = LoggerFactory.getLogger(SourcePointParser.class);

    private static SourcePointParser sourcePointParser = new SourcePointParser();

    private static List<AndroidMethod> sourceMethods = new ArrayList<>();

    public static SourcePointParser v() {
        return sourcePointParser;
    }

    public static void main(String[] args) {
        SourcePointParser.v().init();
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        //已经初始化了，不需要重复初始化
        if (sourceMethods != null && sourceMethods.size() > 0)
            return;
        long start = System.nanoTime();
        logger.info("Start init in SourcePointParser");
        sourceMethods = FileParser.readFile(FilePathConstant.SOURCE_FILE_PATH);
        logger.info("Finished init in SourcePointParser with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    public static List<AndroidMethod> getSourceMethods() {
        if (sourceMethods == null || sourceMethods.size() == 0)
            SourcePointParser.v().init();
        return sourceMethods;
    }
}
