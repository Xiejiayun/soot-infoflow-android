package nju.software.parsers;

import nju.software.constants.FilePathConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xie on 2016/3/14.
 */
public class EntryPointParser {
    private static Logger logger = LoggerFactory.getLogger(EntryPointParser.class);

    private static EntryPointParser entryPointParser = new EntryPointParser();

    private static List<AndroidMethod> entryMethods = new ArrayList<>();


    public static EntryPointParser v() {
        return entryPointParser;
    }

    public static void main(String[] args) {
        EntryPointParser.v().init();
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        long start = System.nanoTime();
        logger.info("Start init in EntryPoint");
        entryMethods = FileParser.readFile(FilePathConstant.Entry_FILE_PATH);
        logger.info("Finished init in EntryPoint with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    public static List<AndroidMethod> getEntryMethods() {
        if (entryMethods == null || entryMethods.size() == 0)
            EntryPointParser.v().init();
        return entryMethods;
    }

    public static void setEntryMethods(List<AndroidMethod> entryMethods) {
        EntryPointParser.entryMethods = entryMethods;
    }

}
