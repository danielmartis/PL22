


%token print id 
%token opas opmd
%token numentero numreal pari pard llavei llaved
%token pyc coma dosp asig
%token tkclass tkpublic tkprivate
%token tkint tkfloat
%token tkif tkelse tkreturn


%{

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>


using namespace std;

#include "comun.h"

// variables y funciones del A. Léxico
extern int ncol,nlin,findefichero;


extern int yylex();
extern char *yytext;
extern FILE *yyin;


int yyerror(char *s);


const int ENTERO=1;
const int REAL=2;

string operador, s1, s2;  // string auxiliares
stringstream ss;
%}
%%

S   : C {int tk = yylex();
         if(tk != 0){
            yyerror("");
         }
         else{
            $$.cod = $1.cod;
            cout << $$.cod;
        }}
;
C   : tkclass id llavei B V llaved {
        ss.str("");
        ss << "class " << $2.lexema << "{}";
        $$.cod = ss.str();
        ss.str("");
    }
;
B   : tkpublic dosp P {}
    |
;
V   : tkprivate dosp P {}
    |
;
P   : D P {}
    |
;
D   : Tipo id FV {}
    | C {}
;
FV  : pari L pard Bloque {}
    | pyc {}
;
L   : Tipo id coma L {}
    | Tipo id {}
;
Tipo    : tkint { $$.cod = "entero"; $$.tipo = ENTERO;}
        | tkfloat { $$.cod = "real"; $$.tipo = REAL;}
;
Bloque  : llavei SecInstr llaved{$$.cod = "\n{\n" + $2.cod + "\n}\n";  }
;
SecInstr    : Instr pyc SecInstr {$$.cod = $1.cod + ";" + $3.cod;}
            |
;
Instr   : Tipo id {$$.cod = $1.cod + $2.lexema;}
        | id asig Expr {}
        | Bloque {$$.cod = $1.cod;}
        | tkreturn Expr {$1.cod = "ret" + $2.cod;}
        | tkif Expr Bloque Ip {$$.cod = "if(" + $2.cod + ")" + $3.cod + $4.cod;}
;
Ip  : tkelse Bloque {}
    |
;
Expr    : Expr opas Term {}
        | Term {}
;
Term    : Term opmd Factor {}
        | Factor {}
;
Factor  : numentero {$$.cod = $1.lexema; $$.tipo = ENTERO;}
        | numreal {$$.cod = $1.lexema; $$.tipo = REAL;}
        | id {}
        | pari Expr pard {$$.cod = "(" + $2.cod + ")"; $$.tipo = $2.tipo;}
;




%%



//***-------------------- en plp4.y -------------------------------
/// ---------- Errores semánticos ---------------------------------
const int ERRYADECL=1,ERRNODECL=2,ERRNOSIMPLE=3,ERRTIPOS=4,ERRNOENTERO=5;
void errorSemantico(int nerror,char *lexema,int fila,int columna)
{
    fprintf(stderr,"Error semantico (%d,%d): en '%s', ",fila,columna,lexema);
    switch (nerror) {
      case ERRYADECL: fprintf(stderr,"ya existe en este ambito\n");
         break;
      case ERRNODECL: fprintf(stderr,"no ha sido declarado\n");
         break;
      case ERRNOSIMPLE: fprintf(stderr,"no es una variable\n");
         break;
      case ERRTIPOS: fprintf(stderr,"la expresion debe ser de tipo entero y es real\n");
         break;
      case ERRNOENTERO: fprintf(stderr,"la expresión debe ser de tipo entero\n");
         break;
    }
    exit(-1);
}




/// ---------- Errores léxicos y sintácticos ----------------------
void msgError(int nerror,int nlin,int ncol,const char *s)
{
     switch (nerror) {
         case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",nlin,ncol,s);
            break;
         case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",nlin,ncol,s);
            break;
         case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
            break;
         case ERRLEXEOF: fprintf(stderr,"Error lexico: fin de fichero inesperado\n");
            break;
     }
        
     exit(1);
}

int yyerror(char *s)
{
    if (findefichero) 
    {
       msgError(ERREOF,-1,-1,"");
    }
    else
    {  
       msgError(ERRSINT,nlin,ncol-strlen(yytext),yytext);
    }
    return 0;
}


int main(int argc,char *argv[])
{
    FILE *fent;

    if (argc==2)
    {
        fent = fopen(argv[1],"rt");
        if (fent)
        {
            yyin = fent;
            yyparse();
            fclose(fent);
        }
        else
            fprintf(stderr,"No puedo abrir el fichero\n");
    }
    else
        fprintf(stderr,"Uso: ejemplo <nombre de fichero>\n");
}
