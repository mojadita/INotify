# $Id$
# Author: Luis Colorado <luiscoloradourcola@gmail.com>
# Date: jue nov  5 20:13:03 EET 2015
# Disclaimer: (C) 2015 LUIS COLORADO SISTEMAS S.L.U.
# 			All rights reserved.


outdir = ../bin

targets = $(outdir)/libINotify.so

ARCH = linux

CFLAGS=-I${JAVA_JDK}/include -I${JAVA_JDK}/include/${ARCH} -fPIC

.SUFFIXES: all clean

all: $(targets)
clean:
	$(RM) $(targets) $(foreach i, $(targets), $($(i)_objs))

$(outdir)/libINotify.so_objs = INotify.o
$(outdir)/libINotify.so_libs =
$(outdir)/libINotify.so: $($(outdir)/libINotify.so_objs)
	$(CC) $(LDFLAGS) -shared -o $@ $($@_objs) $($@_ldflags) $($@_libs)

INotify.o: es_lcssl_linux_inotify_INotify.h

es_lcssl_linux_inotify_INotify.h: es/lcssl/linux/inotify/INotify.java
	javah es.lcssl.linux.inotify.INotify
