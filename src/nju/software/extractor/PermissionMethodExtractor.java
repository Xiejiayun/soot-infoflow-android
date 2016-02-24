package nju.software.extractor;

import soot.jimple.infoflow.android.data.AndroidMethod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 权限方法的提取，用于提取所有带有某种权限的方法
 * Created by Xie on 2016/1/19.
 */
public class PermissionMethodExtractor {

    private static PermissionMethodExtractor permissionMethodExtractor = new PermissionMethodExtractor();
    /*
    权限方法集合
     */
    private static Set<AndroidMethod> permissionMethodSet = new HashSet<AndroidMethod>();
    /*
    权限方法Map，主要由于一个权限可以对应多个方法，所以需要转换为Map
     */
    private static Map<String, List> permissionMethodMap = new HashMap<String, List>();

    public PermissionMethodExtractor() {
    }

    public static PermissionMethodExtractor v() {
        return permissionMethodExtractor;
    }

    public static void main(String[] args) {
        PermissionMethodExtractor extractor = new PermissionMethodExtractor();
        extractor.readFile("permission.txt");
        extractor.parse();
        System.out.println(permissionMethodMap);
    }

    public Set<AndroidMethod> getAndroidMethods() {
        Set<AndroidMethod> androidMethods = new HashSet<AndroidMethod>();
        return androidMethods;
    }

    public void mapping(String mappingFileLocation) {
    }

    /**
     * 初始化方法权限提取器，主要使用默认的文件进行提取
     */
    public void init() {
        readFile("permission.txt");
        parse();
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
        Pattern p = Pattern.compile("^<(.+):\\s(.*)\\s(.*)\\((.*)\\)>$");
        for (String permission : permissionMethodMap.keySet()) {
            List<String> methods = permissionMethodMap.get(permission);
            Iterator iterator = methods.iterator();
            while (iterator.hasNext()) {
                String line = (String) iterator.next();
                if (!line.isEmpty()) {
                    Matcher m = p.matcher(line);
                    if (m.find()) {
                        AndroidMethod mNoRet = this.parseMethod(m, true, permission);
                        permissionMethodSet.add(mNoRet);
                    }
                }
            }
        }


    }

    private AndroidMethod parseMethod(Matcher m, boolean hasReturnType, String permission) {
        assert m.group(1) != null && m.group(2) != null && m.group(3) != null && m.group(4) != null;
        byte groupIndex = 1;
        String className = m.group(groupIndex++).trim();
        String returnType = "";
        if (hasReturnType) {
            returnType = m.group(groupIndex++).trim();
        }
        String methodName = m.group(groupIndex++).trim();
        ArrayList methodParameters = new ArrayList();
        String params = m.group(groupIndex++).trim();
        if (!params.isEmpty()) {
            String[] classData = params.split(",");
            int permData = classData.length;
            for (int permissions = 0; permissions < permData; ++permissions) {
                String perm = classData[permissions];
                methodParameters.add(perm.trim());
            }
        }
        HashSet permissions = new HashSet();
        permissions.add(permission);
        AndroidMethod singleMethod = new AndroidMethod(methodName, methodParameters, returnType, className, permissions);
        return singleMethod;
    }
}