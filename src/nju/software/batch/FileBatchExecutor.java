package nju.software.batch;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by Xie on 2016/1/20.
 */
public class FileBatchExecutor {

    /**
     * 获取一个目录中的所有apk文件
     * @param directory
     * @return
     */
    public String[] getAllApkFiles(String directory) {
        String apks[] = null;
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
        return apks;
    }
}
