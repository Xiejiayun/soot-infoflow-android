package soot.jimple.infoflow.android.TestApps;

/**
 * Created by Xie on 2016/1/19.
 */
public class EnvTest {

    public static void main(String[] args) {
        String androidJars = System.getenv("ANDROID_JARS");
        System.out.println(androidJars);

    }
}
