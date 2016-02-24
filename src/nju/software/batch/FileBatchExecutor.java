package nju.software.batch;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Xie on 2016/1/20.
 */
public class FileBatchExecutor {
    /*
    批处理的apk文件对象
     */
    String apks[] = null;

    public void getAllApkFiles(String directory) {
        File dir = new File(directory);
        if (dir.isDirectory()) {
            apks = dir.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    if (name.endsWith(".apk"))
                        return true;
                    else
                        return false;
                }
            });
        }
    }
}
