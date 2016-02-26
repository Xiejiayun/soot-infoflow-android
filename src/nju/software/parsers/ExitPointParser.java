package nju.software.parsers;

import nju.software.constants.FilePathConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * 出口点解析器
 *
 * Created by lab on 16-2-26.
 */
public class ExitPointParser {
    private static Logger logger = LoggerFactory.getLogger(ExitPointParser.class);

    private static ExitPointParser exitPointParser = new ExitPointParser();

    private static List<AndroidMethod> exitMethods = new ArrayList<>();


    public static ExitPointParser v() {
        return exitPointParser;
    }

    public static void main(String[] args) {
        exitPointParser.v().init();
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        long start = System.nanoTime();
        logger.info("Start init in ExitPoint");
        exitMethods = FileParser.readFile(FilePathConstant.Exit_FILE_PATH);
        logger.info("Finished init in ExitPoint with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    public static List<AndroidMethod> getExitMethods() {
        if (exitMethods == null || exitMethods.size() == 0)
            exitPointParser.v().init();
        return exitMethods;
    }

    public static void setExitMethods(List<AndroidMethod> ExitMethods) {
        exitPointParser.exitMethods = ExitMethods;
    }

}
