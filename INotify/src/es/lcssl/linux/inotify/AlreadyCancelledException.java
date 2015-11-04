/* $Id$
 * Author: Luis Colorado <lc@luiscoloradosistemas.com>
 * Date: 4 de nov. de 2015
 * Project: INotify
 * Filename: AlreadyCancelledException.java
 * Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.
 * 		All rights reserved.  This file must considered
 *		confidential.
 */
package es.lcssl.linux.inotify;

/**
 * Exception generated on {@link Subscription#cancel()} for a 
 * {@link Subscription} that has been already
 * {@link Subscription#cancel()}led.
 */
public class AlreadyCancelledException extends Exception {

	private static final long serialVersionUID = 6325631853001047020L;

}

/* $Id$ */