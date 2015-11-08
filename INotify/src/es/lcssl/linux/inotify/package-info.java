/* $Id$
 * Author: Luis Colorado <lc@luiscoloradosistemas.com>
 * Date: 4 de nov. de 2015
 * Project: INotify
 * Filename: package-info.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.
 * 		All rights reserved.  This file must considered
 *		confidential.
 */
/**
 * INotify Native class to get info about file modifications or
 * access.  See <code>inotify(7)</code>, 
 * <code>inotify_add_watch(2)</code> and 
 * <code>inotify_rm_watch(2)</code> in 
 * linux reference manual.
 * 
 * The  inotify API provides a mechanism for monitoring 
 * filesystem events.  Inotify can be used to monitor individual 
 * files, or to monitor directories.   When  a  directory 
 * is monitored, inotify will return events for the directory 
 * itself, and for files inside the directory.
 */
package es.lcssl.linux.inotify;