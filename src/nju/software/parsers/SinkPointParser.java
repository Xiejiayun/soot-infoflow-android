package nju.software.parsers;

import nju.software.constants.FilePathConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xie on 2016/1/27.
 */
public class SinkPointParser {

    private static Logger logger = LoggerFactory.getLogger(SinkPointParser.class);

    private static SinkPointParser sinkPointParser = new SinkPointParser();

    private static List<AndroidMethod> sinkMethods = new ArrayList<>();


    public static SinkPointParser v() {
        return sinkPointParser;
    }

    public static void main(String[] args) {
        SinkPointParser.v().init();
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        long start = System.nanoTime();
        logger.info("Start init in SinkPoint");
        sinkMethods = FileParser.readFile(FilePathConstant.SINK_FILE_PATH);
        logger.info("Finished init in SinkPoint with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    public static List<AndroidMethod> getSinkMethods() {
        if (sinkMethods == null || sinkMethods.size() == 0)
            SinkPointParser.v().init();
        return sinkMethods;
    }

    public static void setSinkMethods(List<AndroidMethod> sinkMethods) {
        SinkPointParser.sinkMethods = sinkMethods;
    }
}
