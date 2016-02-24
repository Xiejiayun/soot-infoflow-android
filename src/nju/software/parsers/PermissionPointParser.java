package nju.software.parsers;

import nju.software.constants.FilePathConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xie on 2016/1/27.
 */
public class PermissionPointParser {

    private static Logger logger = LoggerFactory.getLogger(PermissionPointParser.class);

    private static PermissionPointParser permissionPointParser = new PermissionPointParser();

    private static Pattern p = Pattern.compile("^<(.+):\\s(.*)\\s(.*)\\((.*)\\)>$");
    /*
    权限方法集合
     */
    public static Set<AndroidMethod> permissionMethodSet = new HashSet<AndroidMethod>();

    public static Map<String, String> methodPermissionMap = new HashMap<>();
    /*
    权限方法Map，主要由于一个权限可以对应多个方法，所以需要转换为Map
     */
    private static Map<String, List> permissionMethodMap = new HashMap<String, List>();

    public static PermissionPointParser v() {
        return permissionPointParser;
    }

    public static void main(String[] args) {
        PermissionPointParser.v().init();
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        long start = System.nanoTime();
        logger.info("Start init read file in SinkPoint");
        readFile(FilePathConstant.PERMISSION_FILE_PATH);
        logger.info("Finished read file in SinkPoint with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
        parse();
        logger.info("Finished init parse in SinkPoint with " + (double) (System.nanoTime() - start) / 1E9 + " seconds");
    }

    /**
     * 提取某个文件中的<权限-方法>映射关系,并将关系保存至Map中
     *
     * @param fileName
     */
    private void readFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String permission = "";
                if (line.startsWith("Permission"))
                    permission = line.split(":")[1];
                line = br.readLine();
                int count = Integer.parseInt(line.split(" ")[0]);
                List<String> methods = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    line = br.readLine();
                    methods.add(line);
                    methodPermissionMap.put(line, permission);
                }
                permissionMethodMap.put(permission, methods);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在获取Map的基础上对Map进行解析，提取出所有的AndroidMethod对象
     */
    private void parse() {
        for (String permission : permissionMethodMap.keySet()) {
            List<String> methods = permissionMethodMap.get(permission);
            Iterator iterator = methods.iterator();
            while (iterator.hasNext()) {
                String line = (String) iterator.next();
                if (!line.isEmpty()) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        AndroidMethod mNoRet = MethodParser.parseMethod(m, true, permission);
                        permissionMethodSet.add(mNoRet);
                    }
                }
            }
        }
    }


}
