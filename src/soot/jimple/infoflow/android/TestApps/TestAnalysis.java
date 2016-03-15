package soot.jimple.infoflow.android.TestApps;

import soot.jimple.infoflow.android.data.AndroidMethod;

import java.io.IOException;

/**
 * Created by Xie on 2016/1/17.
 */
public class TestAnalysis {

    public static void main(String[] args) {
        AndroidMethod androidMethod;
        args = new String[]{"infoleak", "D:\\sdk\\platforms"};
        try {
            Test.main(args);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
