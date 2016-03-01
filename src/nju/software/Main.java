package nju.software;

import nju.software.extractor.EntryPointExtractor;
import nju.software.manager.TotalReportManager;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.data.SootMethodAndClass;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 我们创建该项目的目标就是提供更精细的控制，对于应用间可能存在的攻击进行防范。主要做法就是对应用app做特殊增强机制。
 */
public class Main {


    public static void main(String[] args) throws IOException, InterruptedException {
//            TotalReportManager.v().runAnalysis("apks\\autoaway.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
            TotalReportManager.v().runAnalysis("3");
    }

}