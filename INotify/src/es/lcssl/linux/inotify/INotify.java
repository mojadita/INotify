/* $Id$
 * Author: Luis Colorado <lc@luiscoloradosistemas.com>
 * Date: 4 de nov. de 2015
 * Project: INotify
 * Filename: INotify.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.
 * 		All rights reserved.  This file must considered
 *		confidential.
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
	
	public static final int IN_ACCESS 			= 0x00000001;
	public static final int IN_MODIFY 			= 0x00000002;
	public static final int IN_ATTRIB 			= 0x00000004;
	public static final int IN_CLOSE_WRITE 		= 0x00000008;
	public static final int IN_CLOSE_NOWRITE 	= 0x00000010;
	public static final int IN_OPEN 			= 0x00000020;
	public static final int IN_MOVED_FROM 		= 0x00000040;
	public static final int IN_MOVED_TO 		= 0x00000080;
	public static final int IN_CREATE 			= 0x00000100;
	public static final int IN_DELETE 			= 0x00000200;
	public static final int IN_DELETE_SELF 		= 0x00000400;
	public static final int IN_MOVE_SELF 		= 0x00000800;
	public static final int IN_UNMOUNT 			= 0x00002000;
	public static final int IN_Q_OVERFLOW 		= 0x00004000;
	public static final int IN_IGNORED 			= 0x00008000;
	public static final int IN_CLOSE 			= (IN_CLOSE_WRITE | IN_CLOSE_NOWRITE);
	public static final int IN_MOVE 			= (IN_MOVED_FROM | IN_MOVED_TO);
	public static final int IN_ONLYDIR 			= 0x01000000;
	public static final int IN_DONT_FOLLOW 		= 0x02000000;
	public static final int IN_EXCL_UNLINK 		= 0x04000000;
	public static final int IN_MASK_ADD 		= 0x20000000;
	public static final int IN_ISDIR 			= 0x40000000;
	public static final int IN_ONESHOT 			= 0x80000000;
	private static final int HEADER_SIZE		= 16;
	
	static {
		System.loadLibrary("INotify");
	}
	
	private int fd = -1;
	protected Map<Integer, List<Subscription>> map = 
			new TreeMap<Integer, List<Subscription>>();
	
	/**
	 * Subscription represents one subscription for a registered
	 * {@link INotifyListener}.
	 */
	public class Subscription {
		int				wd;
		File			file;
		INotifyListener listener;
		
		/**
		 * Construct a Subscription object and register it in the map.
		 * @param file {@link File} this {@link Subscription} is registered on.
		 * @param listener {@link INotifyListener} this {@link Subscription} is registered to.
		 * @param mask bitmap of flags to register this subscription on.
		 */
		public Subscription(File file, INotifyListener listener, int mask) {
			this.file = file; this.listener = listener;
			int wd = add_watch(fd, file.toString(), mask);
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
				rm_watch(fd, wd);
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

	public class Event {
		Subscription 	subscription;
		int				flags;
		String			name;

		/**
		 * @param sub
		 * @param fl
		 * @param nm
		 */
		public Event(Subscription sub, int fl, String nm) {
			subscription = sub; flags = fl; name = nm;
		}
		
		/**
		 * <code>subscription</code> resource.
		 * @return the <code>Subscription</code> value of the resource.
		 */
		public Subscription getSubscription() {
			return subscription;
		}
		/**
		 * <code>flags</code> resource.
		 * @return the <code>int</code> value of the resource.
		 */
		public int getFlags() {
			return flags;
		}
		/**
		 * <code>name</code> resource.
		 * @return the <code>String</code> value of the resource.
		 */
		public String getName() {
			return name;
		}
	}
	
	private static native int init(int flags);
	private static native int add_watch(int fd, String f, int flags);
	private static native int rm_watch(int fd, int wd);
	private static native int read(int fd, byte[] buffer, int offset);
	private static native void close(int fd);
	
	public INotify() {
		this(0);
	}
	
	public INotify(int flags) {
		fd = init(flags);
	}
	
	/**
	 * @throws Throwable
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		close(fd);
		super.finalize();
	}
	
	public Subscription addListener(File f, INotifyListener l, int flags) {
		return new Subscription(f, l, flags);
	}
	
	@Override
	public void run() {
		byte[] buffer = new byte[8192];
		int buffer_size = 0;
		int n;
		
		while ((n = read(fd, buffer, buffer_size)) > 0) {
			buffer_size += n; n = 0;
			while (buffer_size - n >= HEADER_SIZE) {
				
			}
			
		}
		
	}
}

/* $Id$ */