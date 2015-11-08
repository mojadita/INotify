/* $Id: fprintbuf.h,v 2.0 2005-10-04 14:54:49 luis Exp $
 * Author: Luis Colorado <Luis.Colorado@HispaLinux.ES>
 * Date: Thu Aug 18 15:47:09 CEST 2005
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

/* Do not include anything BEFORE the line below, as it would not be
 * protected against double inclusion from other files
 */
#ifndef FPRINTBUF_H
#define FPRINTBUF_H

static char FPRINTBUF_H_RCSId[] = "\n$Id: fprintbuf.h,v 2.0 2005-10-04 14:54:49 luis Exp $\n";

/* constants */

/* types */

/* prototypes */
int fprintbuf (FILE *f,	/* fichero de salida */
	int t,				/* tamano del buffer */
	unsigned char *b,	/* puntero al buffer */
	char *fmt, ...);	/* rotulo de cabecera */

#endif /* FPRINTBUF_H */
/* Do not include anything AFTER the line above, as it would not be
 * protected against double inclusion from other files.
 */

/* $Id: fprintbuf.h,v 2.0 2005-10-04 14:54:49 luis Exp $ */
