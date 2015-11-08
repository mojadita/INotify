/*
 * $Id$
 * Author : Luis Colorado <lc@luiscoloradosistemas.com>
 * Date : 8 de nov. de 2015
 * Project : INotify
 * Package : es.lcssl.linux.mainclass
 * Filename : TestINotify.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.U.
 * All rights reserved. This file must be considered
 * Confidential.
 */

package es.lcssl.linux.mainclass;

import java.io.File;

import es.lcssl.linux.inotify.INotify;
import es.lcssl.linux.inotify.INotifyListener;

/**
 * @author Luis Colorado &lt;lc@luiscoloradosistemas.com&gt;
 *         Date: 8 de nov. de 2015
 */
public class TestINotify {
    
    /**
     * <code>void</code> method main for (TODO: <-- fill data here)
     * 
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        INotify<TestINotify> monitor = new INotify<TestINotify>();
        monitor.addListener(
                new File("/tmp"),
                new INotifyListener<TestINotify>() {
                    
                    @Override
                    public void processEvent(File f, int flags, String name,
                            TestINotify pd) {
                        // TODO Auto-generated method stub
                        System.out.println(String.format(
                                "[%#08x]: %s // %s", flags, f, name));
                    }
                },
                INotify.IN_CREATE | INotify.IN_DELETE | INotify.IN_CLOSE_WRITE,
                null);
        monitor.run();
    }
    /*
     * TODO: fill this as appropiate.
     */
}

/* $Id$ */
