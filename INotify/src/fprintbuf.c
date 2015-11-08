/* $Id: fprintbuf.c,v 2.0 2005-10-04 14:54:49 luis Exp $
 * AUTHOR: Luis Colorado <licolorado@indra.es>
 * DATE: 7.10.92.
 * DESC: muestra un buffer de datos en hexadecimal y ASCII.
 */

#include <sys/types.h>
#include <ctype.h>
#include <stdio.h>
#include <stdarg.h>
#include "fprintbuf.h"

static char *id = "$Id: fprintbuf.c,v 2.0 2005-10-04 14:54:49 luis Exp $\n";

#define 	TAM_REG		16

int fprintbuf (FILE *f,	/* fichero de salida */
	int t,			/* tamano del buffer */
	unsigned char *b,	/* puntero al buffer */
	char *fmt, ...)		/* rotulo de cabecera */
{
	int off, i;
	unsigned char c;
	va_list lista;
	size_t escritos = 0;

	if (fmt)
		escritos += fprintf (f, "DESPLAZ. : ");
	va_start (lista, fmt);
	escritos += vfprintf (f, fmt, lista);
	va_end (lista);
	escritos += fprintf (f, "\n");
	off = 0;
	while (t > 0) {
		escritos += fprintf (f, "%08x : ", off);
		for (i = 0; i < TAM_REG; i++) {
			if (t > 0)
				escritos += fprintf (f, "%02x ", *b);
			else escritos += fprintf (f, "   ");
			off++;
			t--;
			b++;
		}
		escritos += fprintf (f, ": ");
		t += TAM_REG;
		b -= TAM_REG;
		off -= TAM_REG;
		for (i = 0; i < TAM_REG; i++) {
			c = *b++;
			if (t > 0)
				if (isprint (c))
					escritos += fprintf (f, "%c", c);
				else	escritos += fprintf (f, ".");
			else break;
			off++;
			t--;
		}
		escritos += fprintf (f, "\n");
	}
	escritos += fprintf (f, "%08x\n", off);

	return escritos;
} /* fprintbuf */

/* $Id: fprintbuf.c,v 2.0 2005-10-04 14:54:49 luis Exp $ */
