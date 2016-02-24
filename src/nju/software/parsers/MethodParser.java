package nju.software.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Matcher;

/**
 * Created by Xie on 2016/1/27.
 */
public class MethodParser {

    private static Logger logger = LoggerFactory.getLogger(MethodParser.class);

    public static AndroidMethod parseMethod(Matcher m, boolean hasReturnType, String permission) {
        assert m.group(1) != null && m.group(2) != null && m.group(3) != null && m.group(4) != null;
        m.matches();
        byte groupIndex = 1;
        logger.info(m.toString());
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
