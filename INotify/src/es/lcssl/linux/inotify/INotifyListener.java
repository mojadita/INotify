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

/**
 * The {@link INotifyListener} represents a listener of
 * inotify events.  Once a {@link INotifyListener} is
 * registered and the {@link INotify#run()} method is
 * active, the {@#processEvent(File, es.lcssl.linux.inotify.INotify.Event)
 * method can be called as soon as the kernel notifies
 * for a file change.
 */
public interface INotifyListener {
    
    public void processEvent(File f, INotify.Event ev);

}

/* $Id$ */
