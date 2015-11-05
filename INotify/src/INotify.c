/* $Id: main.c.m4,v 1.7 2005/11/07 19:39:53 luis Exp $
 * Author: Luis Colorado <luiscoloradourcola@gmail.com>
 * Date: Thu Nov  5 18:34:56 EET 2015
 *
 * Disclaimer:
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

#define IN_INOTIFY_C

/* Standard include files */
#include <linux/inotify.h>
#include <stdio.h>
#include <stdlib.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>

#include "es_lcssl_linux_inotify_INotify_Event.h"
#include "es_lcssl_linux_inotify_INotify.h"
#include "es_lcssl_linux_inotify_INotify_Subscription.h"

#ifndef DEBUG
#define DEBUG 1
#endif

#if DEBUG
#define D(X) __FILE__":%d:%s: " X, __LINE__, __func__
#define TR(s, typ, fmt) do { \
        typ _res = (s); \
        printf(D("%s ==> " fmt "\n"), #s, _res); \
    } while (0)
#else
#define D(X) X
#define TR(s, typ, fmt) (s)
#endif

/* constants */

/* types */

/* prototypes */

/* variables */
static char INOTIFY_C_RCSId[]="\n$Id: main.c.m4,v 1.7 2005/11/07 19:39:53 luis Exp $\n";

static jclass   Event_class,
				INotify_class;
static jfieldID INotify_fd_ID,
				Event_wd_ID,
                Event_flags_ID,
                Event_name_ID;

/* functions */

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    init_class
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_es_lcssl_linux_inotify_INotify_init_1class
  (JNIEnv *env, jclass cl)
{
	TR(INotify_class = cl, jclass, "%#p");
    TR(Event_class = (*env)->FindClass(env, "es/lcssl/linux/inotify/INotify/Event"), jclass, "%#p");

#define P(nam, sig) do { \
        Event_##nam##_ID = (*env)->GetFieldID(env, Event_class, #nam, sig); \
        if (!Event_##nam##_ID) (*env)->FatalError(env, "Cannot access Event." #nam " ID\n"); \
    } while (0)

    P(wd, "I");
    P(flags, "I");
    P(name, "Ljava/lang/String");
#undef P
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    init
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_es_lcssl_linux_inotify_INotify_init
  (JNIEnv *env, jobject obj, jint flags)
{
	jint fd = inotify_init1(flags);
	(*env)->SetIntField(env, obj, INotify_fd_ID, fd);
	return fd;
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    add_watch
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_es_lcssl_linux_inotify_INotify_add_1watch
  (JNIEnv *env, jobject obj, jstring s, jint flags)
{
	const char *str = (*env)->GetStringUTFChars(env, s, NULL);
	jint res = inotify_add_watch(
            (*env)->GetIntField(env, obj, INotify_fd_ID),
            str,
            flags);
    (*env)->ReleaseStringUTFChars(env, s, str);
    return res;
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    rm_watch
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_es_lcssl_linux_inotify_INotify_rm_1watch
  (JNIEnv *env, jobject obj, jint wd)
{
    inotify_rm_watch((*env)->GetIntField(env, obj, INotify_fd_ID), wd);
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    getEvent
 * Signature: ()Les/lcssl/linux/inotify/INotify/Event;
 */
JNIEXPORT jobject JNICALL Java_es_lcssl_linux_inotify_INotify_getEvent
  (JNIEnv *env, jobject o);

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_es_lcssl_linux_inotify_INotify_close
  (JNIEnv *env, jobject o);



/* $Id: main.c.m4,v 1.7 2005/11/07 19:39:53 luis Exp $ */
