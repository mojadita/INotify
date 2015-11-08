/* $Id$
 * Author: Luis Colorado <lc@luiscoloradosistemas.com>
 * Date: 4 de nov. de 2015
 * Project: INotify
 * Filename: INotifyListener.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.
 *      All rights reserved.  This file must considered
 *      confidential.
 */
package es.lcssl.linux.inotify;

import java.io.File;

import es.lcssl.linux.inotify.INotify.Subscription;


/**
 * The {@link INotifyListener} represents a listener of
 * inotify events.  Once a {@link INotifyListener} is
 * registered and the {@link INotify#run()} method is
 * active, the 
 * {@link #processEvent(File, int, String, Object)}
 * method can be called as soon as the kernel notifies
 * for a file change.
 * 
 * 
 * @param <PrivateData> is private data the {@link INotifyListener}
 * wants to be passed in in each call for this {@link Subscription}.
 */
public interface INotifyListener<PrivateData> {
    
    /**
     * Method to process an {@link INotify} event.
     * @param f The {@link File} being monitored.
     * @param flags The bitmask of the conditions that have
     * 		happened to file.  This is a subset of the exported
     * 		bitmasks from {@link INotify} class.
     * @param name The affected file name.  This is used on file
     * 		creation/deletion on monitored dirs.
     * @param pd The <code>&lt;PrivateData&gt;</code> registered
     * 		in the {@link Subscription} when registering this
     * 		{@link INotifyListener}.
     */
    public void processEvent(
    		File f, 
    		int flags, 
    		String name, 
    		PrivateData pd);

}

/* $Id$ */
