/*----------------------- comun.h -----------------------------*/

/* fichero con definciones comunes para los ficheros .l y .y */

typedef struct {
   char *lexema;
   int nlin,ncol;
   int tipo;
   string cod;
   int dir;
   int dbase;
} MITIPO;

#define YYSTYPE MITIPO

// ------------ este cacho debe ir en comun.h -------------------------

#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4

#define ERR_YADECL          10
#define ERR_NODECL          11
#define ERR_DIM             12
#define ERR_FALTAN          13
#define ERR_SOBRAN          14
#define ERR_INDICE_ENTERO  15

#define ERR_NOCABE     100
#define ERR_MAXTEMP    101

void errorSemantico(int nerror,int fila,int columna,const char *s);
void msgError(int nerror,int nlin,int ncol,const char *s);
