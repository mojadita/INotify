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
#include <string.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <sys/types.h>
#include <time.h>
#include <unistd.h>

#include "es_lcssl_linux_inotify_INotify.h"

#ifndef DEBUG
#define DEBUG 0
#endif

#if DEBUG
#define D(X) __FILE__":%d: " X, __LINE__
#define TR(s, typ, fmt) do { \
        typ _res = (s); \
        printf(D("%s ==> " fmt "\n"), #s, _res); \
    } while (0)
#define TRZ(s) do { \
        printf(D("%s;\n"), #s); \
        s; \
    } while (0)
#else
#define D(X) X
#define TR(s, typ, fmt) (s)
#define TRZ(s) do { s; } while (0)
#endif

/* constants */

/* types */

/* prototypes */

/* variables */
static char INOTIFY_C_RCSId[]=
		"\n$Id: main.c.m4,v 1.7 2005/11/07 19:39:53 luis Exp $\n";

static jclass   Event_class,
                INotify_class,
                IOException_class;
static jfieldID INotify_fd_ID,
                INotify_buffer_ID,
                INotify_p1_ID,
                INotify_p2_ID,
                INotify_wd_ID,
                INotify_mask_ID,
                INotify_cookie_ID,
                INotify_name_ID;
static jmethodID
                IOException_C_ID;
static jobject  IOException_inst;

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
    TR(IOException_class = (*env)->FindClass(env, "java/io/IOException"),
    		jclass, "%#p");

    /* fields */
#define F(cls, nam, sig) do { \
        TR(cls##_##nam##_ID = (*env)->GetFieldID(env, cls##_class, #nam, sig), \
        		jfieldID, "%#p"); \
        if (!cls##_##nam##_ID) \
			TRZ((*env)->FatalError(env, \
					"Cannot access field " #cls "." #nam " ID\n")); \
    } while (0)
    F(INotify, fd,     "I");
    F(INotify, buffer, "[B");
    F(INotify, p1,     "I");
    F(INotify, p2,     "I");
    F(INotify, wd,     "I");
    F(INotify, mask,   "I");
    F(INotify, cookie, "I");
    F(INotify, name,   "Ljava/lang/String;");
#undef F

    /* constructors */
#define C(cls, sig) do { \
        TR(cls##_C_ID = (*env)->GetMethodID(env, cls##_class, \
        		"<init>", sig), jmethodID, "%#p"); \
        if (!cls##_C_ID) \
			TRZ((*env)->FatalError(env, \
					"Cannot access constructor " #cls "() ID\n")); \
    } while (0)
    C(IOException, "()V");
#undef C

#if DEBUG
#define CHK(id) do { \
        if ((int)es_lcssl_linux_inotify_INotify_##id != (int)id) { \
            char buffer[256]; \
            snprintf(buffer, sizeof buffer, \
                D(#id "(%#x) in C native code differs " \
                    "from class INotify definition(%#x)"), \
                id, \
                es_lcssl_linux_inotify_INotify_##id); \
            (*env)->FatalError(env, buffer); \
        } \
    } while (0)
#else
#define CHK(id)
#endif
    CHK(IN_ACCESS);             CHK(IN_MODIFY);
    CHK(IN_ATTRIB);             CHK(IN_CLOSE_WRITE);
    CHK(IN_CLOSE_NOWRITE);      CHK(IN_OPEN);
    CHK(IN_MOVED_FROM);         CHK(IN_MOVED_TO);
    CHK(IN_CREATE);             CHK(IN_DELETE);
    CHK(IN_DELETE_SELF);        CHK(IN_MOVE_SELF);
    CHK(IN_UNMOUNT);            CHK(IN_Q_OVERFLOW);
    CHK(IN_IGNORED);            CHK(IN_CLOSE);
    CHK(IN_MOVE);               CHK(IN_ONLYDIR);
    CHK(IN_DONT_FOLLOW);        CHK(IN_EXCL_UNLINK);
    CHK(IN_MASK_ADD);           CHK(IN_ISDIR);
    CHK(IN_ONESHOT);
#undef CHK
    TR(IOException_inst = (*env)->NewObject(env,
    		IOException_class, IOException_C_ID), jobject, "%#p");
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    init
 * Signature: (I)I
 */
JNIEXPORT void JNICALL Java_es_lcssl_linux_inotify_INotify_init
  (JNIEnv *env, jobject obj, jint flags)
{
    jint fd;
    TR(fd = inotify_init1(flags), jint, "%d");
    TRZ((*env)->SetIntField(env, obj, INotify_fd_ID, fd));
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    add_watch
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_es_lcssl_linux_inotify_INotify_add_1watch
  (JNIEnv *env, jobject obj, jstring s, jint flags)
{
    const char *str;
    jint res;
    jint fd;

    TR(str = (*env)->GetStringUTFChars(env, s, NULL), const char *, "%s");
    TR(fd  = (*env)->GetIntField(env, obj, INotify_fd_ID), jint, "%d");
    TR(flags, jint, "%#x");
    TR(res = inotify_add_watch( fd, str, flags), jint, "%d");
    TRZ((*env)->ReleaseStringUTFChars(env, s, str));

    return res;
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    rm_watch
 * Signature: (I)I
 */
JNIEXPORT void JNICALL Java_es_lcssl_linux_inotify_INotify_rm_1watch
  (JNIEnv *env, jobject obj, jint wd)
{
    TR(inotify_rm_watch((*env)->GetIntField(env, obj,
    		INotify_fd_ID), wd), int, "%d");
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    getEvent
 * Signature: ()Les/lcssl/linux/inotify/INotify/Event;
 */
JNIEXPORT jboolean JNICALL Java_es_lcssl_linux_inotify_INotify_getEvent
  (JNIEnv *env, jobject o)
{
    jobject ba;
    jsize sz;
    jint p1, p2, fd;
    jbyte *buffer;
    struct inotify_event *ptr;
    jint wd, mask, cookie;
    jobject name = NULL;
    jboolean res = JNI_FALSE;

    TR(fd = (*env)->GetIntField(   env, o, INotify_fd_ID),     jint,   "%d");
    TR(ba = (*env)->GetObjectField(env, o, INotify_buffer_ID), jobject,"%#p");
    TR(p1 = (*env)->GetIntField(   env, o, INotify_p1_ID),     jint,   "%d");
    TR(p2 = (*env)->GetIntField(   env, o, INotify_p2_ID),     jint,   "%d");
    TR(sz = (*env)->GetArrayLength(env, ba),                   jsize,  "%d");

    TR(buffer = (*env)->GetByteArrayElements(env, ba, NULL),   jbyte*, "%#p");

    while (p2 < p1 + sizeof(struct inotify_event)) {
        int l = p2 - p1;
        if (p1) {
            if (l) memmove(buffer, buffer + p1, l);
            p1 = 0; p2 = l;
        }
        l = read(fd, buffer + p2, sz - p2);
        if (l < 0) {
            TRZ((*env)->Throw(env, IOException_inst));
            goto end;
        }
        if (l == 0) {
            goto end;
        }
        /* l > 0 */
#if DEBUG
        fprintbuf(stderr, l, buffer + p2, D("Read %d bytes:"), l);
#endif
        p2 += l;
    } /* while */

    TR(ptr = (struct inotify_event *) (buffer + p1),
    		struct inotify_event*, "%#p");
    TR(p1 += sizeof (struct inotify_event) + ptr->len, jint, "%d");

    TR(ptr->wd, jint, "%d");
    TRZ((*env)->SetIntField(env, o, INotify_wd_ID, ptr->wd));
    TR(ptr->mask, jint, "%#x");
    TRZ((*env)->SetIntField(env, o, INotify_mask_ID, ptr->mask));
    TR(ptr->cookie, jint, "%#x");
    TRZ((*env)->SetIntField(env, o, INotify_cookie_ID, ptr->cookie));
    if (ptr->len) {
        TR(name = (*env)->NewStringUTF(env, ptr->name), jobject, "%#p");
    }
    TR(ptr->name, const char*, "%s");
    TRZ((*env)->SetObjectField(env, o, INotify_name_ID, name));
    TR(p1, jint, "%d");
    TRZ((*env)->SetIntField(env, o, INotify_p1_ID, p1));
    TR(p2, jint, "%d");
    TRZ((*env)->SetIntField(env, o, INotify_p2_ID, p2));
    res = JNI_TRUE;

end:
    TRZ((*env)->ReleaseByteArrayElements(env, ba, buffer, 0));

    return res;
}

/*
 * Class:     es_lcssl_linux_inotify_INotify
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_es_lcssl_linux_inotify_INotify_close
  (JNIEnv *env, jobject o)
{
    jint fd;
    TR(fd = (*env)->GetIntField(env, o, INotify_fd_ID), jint, "%d");
    TRZ(close(fd));
}

/* $Id: main.c.m4,v 1.7 2005/11/07 19:39:53 luis Exp $ */
