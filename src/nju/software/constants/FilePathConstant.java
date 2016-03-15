package nju.software.constants;

/**
 * 文件路径常量
 *
 * 入口点：进入一个app的生命周期方法，例如onCreate方法等
 *
 * 出口点：离开一个app去到另外一个app的方法，例如startActivity方法等
 *
 * 源点：
 *
 * 沉淀点
 * Created by Xie on 2016/1/27.
 */
public class FilePathConstant {

    /*
    权限文件路径
     */
    public static final String PERMISSION_FILE_PATH = "config\\permission.txt";

    /*
    源文件路径
     */
    public static final String SOURCE_FILE_PATH = "config\\sources.txt";

    /*
    沉淀点文件路径
     */
    public static final String SINK_FILE_PATH = "config\\sinks.txt";

    /*
    出口点文件路径
     */
    public static final String Exit_FILE_PATH = "config\\exitpoint.txt";

    /*
    入口点文件路径
    */
    public static final String Entry_FILE_PATH = "config\\entrypoint.txt";
}
