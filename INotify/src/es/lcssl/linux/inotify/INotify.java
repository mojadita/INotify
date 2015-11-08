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
 * 
 * The  inotify API provides a mechanism for monitoring 
 * filesystem events.  Inotify can be used to monitor individual 
 * files, or to monitor directories.   When  a  directory 
 * is monitored, inotify will return events for the directory 
 * itself, and for files inside the directory.
 * When monitoring a directory:
 * <ul>
 *   <li><a name="asterisk">the events marked above with an 
 *     asterisk (*) can occur both for  the directory itself 
 *     and for objects inside the directory; and</a></li>
 *   <li><a name="plus">the events marked with a plus sign 
 *     (+) occur only for objects inside the directory 
 *     (not for the directory itself).</a></li>
 * </ul>
 * 
 * @see es.lcssl.linux.inotify package.
 */
public class INotify<PrivateData> implements Runnable {
    
    /**
     * <a href="#plus">(+)</a> File was accessed (e.g., read(2), 
     * execve(2)).
     */
    public static final int IN_ACCESS           = 0x00000001;
    /** 
     * <a href="#plus">(+)</a> File was modified (e.g., write(2), 
     * truncate(2)).
     */
    public static final int IN_MODIFY           = 0x00000002;
    /**
     * <a href="#asterisk">(*)</a> Metadata changed—for example, permissions (e.g.,  
     * chmod(2)),  timestamps  (e.g.,  utimensat(2)),  extended 
     * attributes  (setxattr(2)),  link  count  (since  Linux  
     * 2.6.25;  e.g.,  for  the target of link(2) and for unlink(2)), 
     * and user/group ID (e.g., chown(2)).
     */
    public static final int IN_ATTRIB           = 0x00000004;
    /**
     * <a href="#plus">(+)</a> File opened for writing was closed.
     */
    public static final int IN_CLOSE_WRITE      = 0x00000008;
    /**
     * <a href="#asterisk">(*)</a> File or directory not opened for writing was closed.
     */
    public static final int IN_CLOSE_NOWRITE    = 0x00000010;
    /**
     * <a href="#asterisk">(*)</a> File or directory was opened.
     */
    public static final int IN_OPEN             = 0x00000020;
    /**
     * <a href="#plus">(+)</a> Generated for the directory containing the old 
     *     filename when a file is renamed.
     */
    public static final int IN_MOVED_FROM       = 0x00000040;
    /**
     * <a href="#plus">(+)</a> Generated for the directory containing the new 
     *     filename when a file is renamed.
     */
    public static final int IN_MOVED_TO         = 0x00000080;
    /**
     * <a href="#plus">(+)</a> File/directory  created  in watched directory (e.g., open(2)
     *     O_CREAT, mkdir(2), link(2), symlink(2), bind(2)  on  a  UNIX
     *     domain socket).
     */
    public static final int IN_CREATE           = 0x00000100;
    /**
     * <a href="#plus">(+)</a> File/directory deleted from watched directory.
     */
    public static final int IN_DELETE           = 0x00000200;
    /**
     *  Watched file/directory was itself deleted.  
     *  (This event also  occurs if an object is moved to  
     *  another  filesystem,  since  mv(1)  in effect copies 
     *  the file to the other filesystem and then deletes it 
     *  from the original filesystem.)  In addition, an  
     *  IN_IGNORED  event will subsequently be generated 
     *  for the watch descriptor.
     */
    public static final int IN_DELETE_SELF      = 0x00000400;
    /**
     * Watched file/directory was itself moved.
     */
    public static final int IN_MOVE_SELF        = 0x00000800;
    /**
     * Filesystem containing  watched  object  was  unmounted.   
     * In addition, an IN_IGNORED event will subsequently 
     * be generated for the watch descriptor.
     */
    public static final int IN_UNMOUNT          = 0x00002000;
    /**
     * Event queue overflowed (wd is -1 for this event).
     */
    public static final int IN_Q_OVERFLOW       = 0x00004000;
    /**
     * Watch was removed explicitly (inotify_rm_watch(2)) or  
     * automatically  (file  was deleted, or filesystem 
     * was unmounted).
     */
    public static final int IN_IGNORED          = 0x00008000;
    /**
     * Equates to IN_CLOSE_WRITE | IN_CLOSE_NOWRITE.
     */
    public static final int IN_CLOSE            = (IN_CLOSE_WRITE 
    		| IN_CLOSE_NOWRITE);
    /**
     * Equates to IN_MOVED_FROM | IN_MOVED_TO.
     */
    public static final int IN_MOVE             = (IN_MOVED_FROM 
    		| IN_MOVED_TO);
    /**
     * (since Linux 2.6.15)
     * Only watch pathname if it is a directory.  
     * Using  this  flag provides  an  application  with  a 
     * race-free way of ensuring that the monitored object is 
     * a directory.
     */
    public static final int IN_ONLYDIR          = 0x01000000;
    /**
     * (since Linux 2.6.15)
     * Don't dereference pathname if it is a symbolic link.
     */
    public static final int IN_DONT_FOLLOW      = 0x02000000;
    /**
     * (since Linux 2.6.36)
     * By default, when watching events on the children of a 
     * directory, events are generated for children even 
     * after they have been  unlinked from the directory.  
     * This can result in large numbers of uninteresting events 
     * for some applications (e.g., if  watching  /tmp, in 
     * which many applications create temporary files whose 
     * names are immediately unlinked).   
     * Specifying  IN_EXCL_UNLINK  changes  the  default 
     * behavior, so that events are not generated for children 
     * after they  have  been unlinked from the watched 
     * directory.
     */
    public static final int IN_EXCL_UNLINK      = 0x04000000;
    /**
     * If a watch instance already exists for the filesystem 
     * object corresponding to pathname, add (OR) the events  
     * in  mask  to the watch mask (instead of replacing the 
     * mask).
     */
    public static final int IN_MASK_ADD         = 0x20000000;
    /**
     * Subject of this event is a directory.
     */
    public static final int IN_ISDIR            = 0x40000000;
    /**
     * Monitor the filesystem object corresponding to pathname 
     * for one event, then remove from watch list.
     */
    public static final int IN_ONESHOT          = 0x80000000;
    
    /**
     * Size of the input events buffer. This reduces the number
     * of system calls.  Can be changed as the native code part
     * asks for the buffer length before <code>read(2)</code>
     */
    private static final int BUFFERSIZE			= 8192;
        
    static {
        System.loadLibrary("INotify");
        init_class();
    }
    
    /* internal data used in native functions */
    private int fd = -1; /* file descriptor */
    private byte[] buffer = new byte[BUFFERSIZE]; /* data buffer */
    private int p1 = 0, p2 = 0; /* data pointers */

    /* These are fields from the struct inotify_event */
    private int wd,      /* watch descriptor */ 
    			mask,    /* bitmask */
    			cookie;  /* cookie, to correlate 
    					  * events for moved files */
    private String name; /* file name */
    
    protected Map<Integer, List<Subscription>> map = 
            new TreeMap<Integer, List<Subscription>>();

    /**
     * Subscription represents one subscription for a registered
     * {@link INotifyListener}.  It is returned as a result of
     * registering {@link INotifyListener}s, and allows to cancel
     * subscriptions, and access to the subscription data.
     */
    public class Subscription {
        int             wd;
        File            file;
        INotifyListener<PrivateData> listener;
        PrivateData		pd;
        
        /**
         * Construct a Subscription object and register it in the map.
         * @param file {@link File} this {@link Subscription} 
         * 			is registered on.
         * @param listener {@link INotifyListener} this 
         * 			{@link Subscription} is registered to.
         * @param mask bitmap of flags to register this 
         * 			subscription on.
         * @param pd Is the <code>PrivateData</code> stored
         * 			in the {@link Subscription}.
         */
        public Subscription(
        		File file, 
        		INotifyListener<PrivateData> listener, 
        		int mask, 
        		PrivateData pd) 
        {
            this.file = file; this.listener = listener; this.pd = pd;
            int wd = add_watch(file.toString(), mask);
            List<Subscription> l = map.get(wd);
            if (l == null) {
                map.put(wd, l = new ArrayList<Subscription>());
            }
            l.add(this);
        }

        /**
         * Cancels a living subscription.
         * @throws AlreadyCancelledException In case {@link Subscription}
         * has been already cancelled.
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
         * <code>wd</code> resource.  This is the internal
         * watch descriptor from the <code>inotify_add_watch(2)</code> 
         * system call.
         * @return the <code>int</code> value of the resource.
         */
        public int getWd() {
            return wd;
        }

        /**
         * <code>file</code> resource.  This is the file being
         * monitored.
         * @return the <code>File</code> value of the resource.
         */
        public File getFile() {
            return file;
        }

        /**
         * <code>listener</code> resource.  This is a reference to
         * the {@link INotifyListener} registered at this point.
         * @return the <code>INotifyListener</code> 
         * 			value of the resource.
         */
        public INotifyListener<PrivateData> getListener() {
            return listener;
        }
        
        /**
         * <code>Pd</code> resource.  This is the 
         * <code>PrivateData</code> the 
         * {@link INotifyListener} stores in the {@link Subscription}.
         * @return  The private data of the {@link Subscription}.
         */
        public PrivateData getPd() {
        	return pd;
        }
    }

    /* native procedures */
    private static native void init_class();
    private native void init(int flags);
    private native int add_watch(String f, int flags);
    private native void rm_watch(int wd);
    private native boolean getEvent();
    private native void close();
    
    /**
     * Default constructor.
     */
    public INotify() {
        this(0);
    }
    
    /**
     * Constructor allowing to specify <code>flags</code>. See
     * inotify_init1(2) system call for an explanation of these
     * flags.
     * @param flags  the flags to use in {@link INotify} creation.
     */
    public INotify(int flags) {
        init(flags);
    }
    
    /**
     * This method closes the <code>inotify(7)</code> descriptor
     * in case the user forgets to do it.
     * @throws Throwable
     * @see java.lang.Object#finalize()
     */
    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
    
    /**
     * Adds a new {@link INotifyListener} to monitor for file
     * from now on.
     * @param f {@link File} to be monitored on.
     * @param l {@link INotifyListener} listener that processes
     * 		this Subscription events.
     * @param flags <code>int</code> flags indicating the events 
     * 		to be monitored.
     * @param pd <code>PrivateData</code> to save on behalf of the
     *      {@link INotifyListener}.
     * @return This method returns a {@link Subscription} object 
     * 		which can be used to further act upon the subscription.
     */
    public Subscription addListener(
    		File f, 
    		INotifyListener<PrivateData> l, 
    		int flags, 
    		PrivateData pd) 
    {
        return new Subscription(f, l, flags, pd);
    }
    
    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
    	while (getEvent()) {
    		List<Subscription> l = map.get(wd);
    		if (l == null) {
    			rm_watch(wd);
            } else {
            	for (Subscription s: l) {
            		s.getListener().processEvent(
            				s.getFile(), 
            				mask, 
            				name, 
            				s.getPd());
            	}
            }
        }
    }
    
    /**
     * Example application that lists the events with one 
     * subscription per file indicated in the command
     * line.
     * @param args The names of the {@link File}s to be monitored.
     */
    public static void main(String[] args) {
    	INotify<Integer> mon = new INotify<Integer>();
    	INotifyListener<Integer> lis = 
    			new INotifyListener<Integer>() {
			
			@Override
			public void processEvent(
					File f, 
					int flags, 
					String name, 
					Integer dumb) {
				System.out.println(String.format(
						"%d:[%#x]: %s ==> %s", 
						dumb, flags, f, name));
			}
		};
		for (int i = 0; i < args.length; i++)
			mon.addListener(
					new File(args[i]), 
					lis, 
					IN_CREATE | IN_DELETE | IN_DELETE_SELF |IN_CLOSE, 
					i);
		new Thread(mon).start();
    }
}

/* $Id$ */
