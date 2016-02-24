package nju.software.test;

import nju.software.extractor.EntryPointExtractor;
import soot.Scene;
import soot.SootClass;
import soot.util.Chain;
import soot.util.HashChain;

import java.util.Iterator;
import java.util.Set;

/**
 * Created by Xie on 2016/1/20.
 */
public class TestFindSink {

    public static void main(String[] args) {
//    	Collection<SootMethod> entryPoints,
        Set<String> set = EntryPointExtractor.v().getAllEntryPointClasses("Echoer.apk");
        Chain<SootClass> applicationClasses = new HashChain<SootClass>();
        Chain<SootClass> classes = Scene.v().getClasses();
        for (Iterator<SootClass> iter = classes.snapshotIterator(); iter.hasNext(); ) {
            SootClass sc = iter.next();
            String name = sc.getName();
            applicationClasses.add(sc);
        }

        System.out.println("Application classes:");
        for (SootClass sc : applicationClasses) {
            System.out.println(sc.getName());
        }
    }
}