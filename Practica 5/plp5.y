%token global
%token tkint tkfloat
%token tkmain
%token tkwrite tkread
%token tkif tkelse tkwhile
%token id nentero nreal
%token dosp coma pyc
%token pari pard
%token relop addop mulop assig
%token cori cord
%token llavei llaved

%{

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>
#include "TablaTipos.h"
#include "TablaSimbolos.h"
#include <stack>
using namespace std;

#include "comun.h"

// variables y funciones del A. Léxico
extern int ncol,nlin,findefichero;


extern int yylex();
extern char *yytext;
extern FILE *yyin;



int yyerror(char *s);

string operador, s1, s2;  // string auxiliares
stringstream ss;
TablaSimbolos* ts = new TablaSimbolos(NULL);
TablaTipos tt;

int ctemp = 16000; // contador de direcciones temporales
int memoria = 0;
int nuevaTemp(void)
{
    int t = ctemp;
    ctemp ++;
    return t;
}

%}
%%

S   : global llavei BDecl llaved Funciones {
        int tk = yylex();
        if(tk != 0){
            yyerror("");
        }
        else{
            ss.str("");
            ss << $5.cod << "\nhalt";
            $$.cod = ss.str();
            ss.str("");
            cout << $$.cod;
        }           
}
;

Funciones   : tkint tkmain pari pard Bloque {}
;

Tipo    : tkint {$$.tipo = ENTERO;}
        | tkfloat {$$.tipo = REAL;}
;

Bloque  : llavei {ts = new TablaSimbolos(ts); $$.tipo = memoria;} BDecl SeqInstr llaved {ts = ts->getPadre(); memoria = $2.tipo;}
;

BDecl   : BDecl Dvar {}
        | {}
;

Dvar    : Tipo LIdent pyc {}
;

LIdent  : LIdent coma Variable {}
        | Variable {}
;

Variable : id V {}
;

V       : cori nentero dosp nentero cord V {}
        | {}
;

SeqInstr : SeqInstr {ctemp = 16000;} Instr {}
         | {}
;

Instr   : pyc {}
        | Bloque {}
        | Ref assig Expr pyc {}
        | tkwrite pari Expr pard pyc {}
        | tkread pari Ref pard pyc {}
        | tkif pari Expr pard Instr {}
        | tkif pari Expr pard Instr tkelse Instr {}
        | tkwhile pari Expr pard Instr {}
;

Expr    : Expr relop Esimple {}
        | Esimple {}
;

Esimple : Esimple addop Term {}
        | Term {}
;

Term    : Term mulop Factor {}
        | Factor {}
;

Factor  : Ref { if($1.tipo > REAL){
                        errorSemantico(ERR_FALTAN, $1.nlin, $1.ncol, $1.lexema);
                }
                $$.dir = nuevaTemp();
                ss.str("");
                ss << $1.cod << "\n mov " << $1.dir << " A\n" << "addi #" << $1.dbase << "mov @A " << $$.dir;
                $$.cod = ss.str();
                $$.tipo = $1.tipo;
                ss.str("");
          }
        | nentero {$$.dir = nuevaTemp();
                   ss.str("");
                   ss << "mov # " << $1.lexema << " " << $$.dir << "\n";
                   $$.cod = ss.str();
                   ss.str("");
                   $$.tipo = ENTERO;}
        | nreal {$$.dir = nuevaTemp();
                 ss.str("");
                 ss << "mov $ " << $1.lexema << " " << $$.dir << "\n";
                 $$.cod = ss.str();
                 ss.str("");
                 $$.tipo = REAL;
                 }
        | pari Expr pard {
                $$.cod = $2.cod;
                $$.dir = $2.dir;
                $$.tipo = $2.tipo;
        }
;

Ref     : id {
                if(ts->searchSymb($1.lexema) == NULL){
                        errorSemantico(ERR_NODECL, $1.nlin, $1.ncol, $1.lexema);
                }
                else{
                        $$.dir = nuevaTemp();
                        Simbolo *s = ts->searchSymb($1.lexema);
                        ss.str("");
                        ss << "mov #0 " << $$.dir;
                        $$.cod = ss.str();
                        ss.str("");
                        $$.tipo = s->tipo;
                        $$.dbase = s->dir;

                }
        }
        | Ref cori Esimple cord {

        }
;

%%


// ------------ este cacho debe ir en plp5.y -------------------------

void errorSemantico(int nerror,int fila,int columna,const char *s)
{
    fprintf(stderr,"Error semantico (%d,%d): ", fila,columna);
    switch(nerror) {
     case ERR_YADECL: fprintf(stderr,"simbolo '%s' ya declarado\n",s);
       break;
     case ERR_NODECL: fprintf(stderr,"identificador '%s' no declarado\n",s);
       break;
     case ERR_DIM: fprintf(stderr,"la segunda dimension debe ser mayor o igual que la primera\n");
       break;
     case ERR_FALTAN: fprintf(stderr,"faltan indices\n");
       break;
     case ERR_SOBRAN: fprintf(stderr,"sobran indices\n");
       break;
     case ERR_INDICE_ENTERO: fprintf(stderr,"la expresion entre corchetes debe ser de tipo entero\n");
       break;
     case ERR_NOCABE:fprintf(stderr,"la variable '%s' ya no cabe en memoria\n",s);
       break;
     case ERR_MAXTEMP:fprintf(stderr,"no hay espacio para variables temporales\n");
       break;
    }
    exit(-1);
}


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
