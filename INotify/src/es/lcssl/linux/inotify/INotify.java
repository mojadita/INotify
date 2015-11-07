/* $Id$
 * Author: Luis Colorado <lc@luiscoloradosistemas.com>
 * Date: 4 de nov. de 2015
 * Project: INotify
 * Filename: INotify.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.
 *      All rights reserved.  This file must considered
 *      confidential.
 */
package es.lcssl.linux.inotify;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * INotify implements the linux <code>inotify(7)</code> interface.
 */
public class INotify implements Runnable {
    
    public static final int IN_ACCESS           = 0x00000001;
    public static final int IN_MODIFY           = 0x00000002;
    public static final int IN_ATTRIB           = 0x00000004;
    public static final int IN_CLOSE_WRITE      = 0x00000008;
    public static final int IN_CLOSE_NOWRITE    = 0x00000010;
    public static final int IN_OPEN             = 0x00000020;
    public static final int IN_MOVED_FROM       = 0x00000040;
    public static final int IN_MOVED_TO         = 0x00000080;
    public static final int IN_CREATE           = 0x00000100;
    public static final int IN_DELETE           = 0x00000200;
    public static final int IN_DELETE_SELF      = 0x00000400;
    public static final int IN_MOVE_SELF        = 0x00000800;
    public static final int IN_UNMOUNT          = 0x00002000;
    public static final int IN_Q_OVERFLOW       = 0x00004000;
    public static final int IN_IGNORED          = 0x00008000;
    public static final int IN_CLOSE            = (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);
    public static final int IN_MOVE             = (IN_MOVED_FROM | IN_MOVED_TO);
    public static final int IN_ONLYDIR          = 0x01000000;
    public static final int IN_DONT_FOLLOW      = 0x02000000;
    public static final int IN_EXCL_UNLINK      = 0x04000000;
    public static final int IN_MASK_ADD         = 0x20000000;
    public static final int IN_ISDIR            = 0x40000000;
    public static final int IN_ONESHOT          = 0x80000000;
    
    private static final int BUFFERSIZE			= 8192;
        
    static {
        System.loadLibrary("INotify");
        init_class();
    }
    
    /* internal data used in native functions */
    private int fd = -1;
    private byte[] buffer = new byte[BUFFERSIZE];
    private int p1 = 0, p2 = 0; 
    
    protected Map<Integer, List<Subscription>> map = 
            new TreeMap<Integer, List<Subscription>>();
    
    /**
     * Subscription represents one subscription for a registered
     * {@link INotifyListener}.
     */
    public class Subscription {
        int             wd;
        File            file;
        INotifyListener listener;
        
        /**
         * Construct a Subscription object and register it in the map.
         * @param file {@link File} this {@link Subscription} is registered on.
         * @param listener {@link INotifyListener} this {@link Subscription} is registered to.
         * @param mask bitmap of flags to register this subscription on.
         */
        public Subscription(File file, INotifyListener listener, int mask) {
            this.file = file; this.listener = listener;
            int wd = add_watch(file.toString(), mask);
            List<Subscription> l = map.get(file);
            if (l == null) {
                map.put(wd, l = new ArrayList<Subscription>());
            }
            l.add(this);
        }

        /**
         * Cancels a living subscription.
         * @throws AlreadyCancelledException
         */
        public void cancel() throws AlreadyCancelledException {
            List<Subscription> l = map.get(getWd());
            if (l == null || !l.remove(this)) 
                throw new AlreadyCancelledException();
            if (l.size() == 0) {
                map.remove(getWd());
                rm_watch(wd);
            }
        }
        
        /**
         * <code>wd</code> resource.
         * @return the <code>int</code> value of the resource.
         */
        public int getWd() {
            return wd;
        }

        /**
         * <code>file</code> resource.
         * @return the <code>File</code> value of the resource.
         */
        public File getFile() {
            return file;
        }

        /**
         * <code>listener</code> resource.
         * @return the <code>INotifyListener</code> value of the resource.
         */
        public INotifyListener getListener() {
            return listener;
        }
        
    }

    public static class Event {
    	int				wd;
        int             flags;
        int				cookie;
        String          name;
    }
    
    private static native void init_class();
    private native void init(int flags);
    private native int add_watch(String f, int flags);
    private native void rm_watch(int wd);
    private native Event getEvent();
    private native void close();
    
    public INotify() {
        this(0);
    }
    
    public INotify(int flags) {
        init(flags);
    }
    
    /**
     * @throws Throwable
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    public Subscription addListener(File f, INotifyListener l, int flags) {
        return new Subscription(f, l, flags);
    }
    
    @Override
    public void run() {
    	Event event;

    	while ((event = getEvent()) != null) {
    		List<Subscription> l = map.get(event.wd);
    		if (l == null) {
    			rm_watch(event.wd);
            } else {
            	for (Subscription s: l) {
            		s.getListener().processEvent(
            				s.getFile(), 
            				event.flags, 
            				event.name);
            	}
            }
        }
        
    }
    
    public static void main(String[] args) {
    	INotify mon = new INotify();
    	INotifyListener lis = new INotifyListener() {
			
			@Override
			public void processEvent(File f, int flags, String name) {
				System.out.println("" + flags + f + (name != null ? ": " + name : ""));
			}
		};
		mon.addListener(new File("/tmp"), lis, IN_CREATE | IN_DELETE | IN_CLOSE);
		new Thread(mon).start();
    }
}

/* $Id$ */
