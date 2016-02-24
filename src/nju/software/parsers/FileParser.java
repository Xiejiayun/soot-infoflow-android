package nju.software.parsers;

import soot.jimple.infoflow.android.data.AndroidMethod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Xie on 2016/1/27.
 */
public class FileParser {

    //对文件进行读取，根据特定的模式匹配出符合要求的Android方法
    public static List<AndroidMethod> readFile(String fileName) {
        //特定的模式
        Pattern p = Pattern.compile("^<(.+):\\s(.*)\\s(.*)\\((.*)\\)>\\s+->\\s+(.*)$");
        List<AndroidMethod> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("%"))
                    continue;
                Matcher m = p.matcher(line);
                AndroidMethod method = MethodParser.parseMethod(m, true, null);
                list.add(method);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return list;
    }
}
