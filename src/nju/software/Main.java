package nju.software;

import nju.software.constants.SettingConstant;
import nju.software.manager.TotalReportManager;

import java.io.IOException;

/**
 * 我们创建该项目的目标就是提供更精细的控制，对于应用间可能存在的攻击进行防范。主要做法就是对应用app做特殊增强机制。
 */
public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
//            TotalReportManager.v().runAnalysis("apks\\autoaway.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
        TotalReportManager.v().runAnalysis("InterAppCommunication/sms.apk", SettingConstant.ANDROID_DEFALUT_JAR_PATH);
    }
}