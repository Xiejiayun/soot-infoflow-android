package nju.software.config;

import nju.software.constants.SettingConstant;
import soot.G;
import soot.Scene;
import soot.options.Options;

import java.util.Collections;

/**
 * 初始化Soot，方便对Android文件进行分析
 * Created by Xie on 2016/1/25.
 */
public class AndroidSootConfig {

    //指向需要分析的APK的地址
    private static String APK_FILE_PATH = null;

    public AndroidSootConfig() {
    }

    public AndroidSootConfig(String APK_FILE_PATH) {
        this.APK_FILE_PATH = APK_FILE_PATH;
    }

    /**
     * 初始化Soot的方法
     */
    public static void initSoot() {
        G.reset();
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_process_dir((Collections.singletonList(APK_FILE_PATH)));
        Options.v().set_android_jars(SettingConstant.ANDROID_JAR_PATH);
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_J);
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().ignore_resolution_errors();
        Options.v().setPhaseOption("cg.spark", "on");
        Options.v().setPhaseOption("cg.spark", "rta:true");
        Options.v().setPhaseOption("cg.spark", "string-constants:true");
        Options.v().set_keep_line_number(true);
        Scene.v().loadNecessaryClasses();
    }

    public static String getApkFilePath() {
        return APK_FILE_PATH;
    }

    public static void setApkFilePath(String apkFilePath) {
        APK_FILE_PATH = apkFilePath;
    }
}

//    public static void main(String[] args) {
//        AndroidSootConfig asc = new AndroidSootConfig("Echoer.apk");
//        AndroidSootConfig.initSoot();
//        Scene c = Scene.v();
//        CHATransformer.v().transform();
//
//        SparkTransformer.v().transform();
//        PackManager.v();
//        c = Scene.v();
//        return;
//    }
