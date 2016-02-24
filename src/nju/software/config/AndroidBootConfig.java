package nju.software.config;

import nju.software.parsers.PermissionPointParser;
import nju.software.parsers.SinkPointParser;
import nju.software.parsers.SourcePointParser;

/**
 * 启动初始化所有需要的资源
 * Created by Xie on 2016/2/2.
 */
public class AndroidBootConfig {

    public static String apkFileLocation = null;

    /**
     * 初始化权限点，源点和沉淀点的映射文件
     */
    public static void initMappingFiles() {
        PermissionPointParser.v().init();
        SourcePointParser.v().init();
        SinkPointParser.v().init();
    }
}
