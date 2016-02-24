package nju.software.comparator;

import soot.jimple.infoflow.android.data.AndroidMethod;

import java.util.Comparator;
import java.util.LinkedList;

/**
 * 用来比较两个方法是否相同，这里我们不考虑其中所包含的权限
 * Created by Xie on 2016/1/20.
 */
public class AndroidMethodComparator implements Comparator {

    public static void main(String[] args) {
        LinkedList<String> linkedList = new LinkedList<>();
        linkedList.add("A");
        linkedList.add("B");
        LinkedList<String> linkedList1 = new LinkedList<>();
        linkedList.add("C");
        AndroidMethod method1 = new AndroidMethod("hello", linkedList, "void", "nju.software.HelloWorld");
        AndroidMethod method2 = new AndroidMethod("hello", linkedList1, "void", "nju.software.HelloWorld");
        System.out.println(new AndroidMethodComparator().compare(method1, method2));

    }

    public static int compare(AndroidMethod method1, AndroidMethod method2) {
        return ComparatorHolder.comparator.compare(method1, method2);
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 == null)
            return -1;
        if (o2 == null)
            return 1;
        AndroidMethod m1 = (AndroidMethod) o1;
        AndroidMethod m2 = (AndroidMethod) o2;
        if (m1.getClassName().compareTo(m2.getClassName()) < 0)
            return -1;
        if (m1.getClassName().compareTo(m2.getClassName()) > 0)
            return 1;
        if (m1.getMethodName().compareTo(m2.getMethodName()) < 0)
            return -1;
        if (m1.getMethodName().compareTo(m2.getMethodName()) > 0)
            return 1;
        if (m1.getParameters().toString().compareTo(m2.getParameters().toString()) < 0)
            return -1;
        if (m1.getParameters().toString().compareTo(m2.getParameters().toString()) > 0)
            return 1;
        return 0;
    }

    public static class ComparatorHolder {
        public static AndroidMethodComparator comparator = new AndroidMethodComparator();
    }
}
