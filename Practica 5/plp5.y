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
TablaTipos* tt = new TablaTipos();

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

Funciones   : tkint tkmain pari pard Bloque {$$.cod = $5.cod;}
;

Tipo    : tkint {$$.tipo = ENTERO;}
        | tkfloat {$$.tipo = REAL;}
;

Bloque  : llavei {ts = new TablaSimbolos(ts); $$.tipo = memoria;} BDecl SeqInstr llaved {ts = ts->getPadre(); memoria = $2.tipo; $$.cod = $4.cod; }
;

BDecl   : BDecl Dvar {$$.cod = "";}
        | {$$.cod = "";}
;

Dvar    : Tipo {$$.tipo = $1.tipo;} LIdent pyc {}
;

LIdent  : LIdent coma {$$.tipo = $0.tipo;} Variable {}
        | Variable {}
;

Variable : id {$$.tipo = $0.tipo;} V {Simbolo s;
                                      s.dir = memoria;
                                      s.tam = $3.tam;
                                      s.tipo = $3.tipo;
                                      s.nombre = $1.lexema;
                                      memoria = memoria + $3.tam;
                                      if(!ts->newSymb(s)){
                                        errorSemantico(ERR_YADECL, $1.nlin, $1.ncol, $1.lexema);
                                      }
                                      }
;

V       : cori nentero dosp nentero cord {$$.tipo = $0.tipo;} V {tt->nuevoTipoArray(atoi($2.lexema), atoi($4.lexema),$7.tipo); $$.tam = $7.tam * (atoi($2.lexema) * atoi($4.lexema) - 1); $$.tipo = tt->tipos.size() -1;} 
        | {$$.tipo = $0.tipo; $$.tam = 1;}
;

SeqInstr : SeqInstr {ctemp = 16000;} Instr {$$.cod = $1.cod + $3.cod;}
         | {$$.cod = "";}
;

Instr   : pyc {}
        | Bloque {}
        | Ref assig Expr pyc {  // Faltan comprobaciones.
                                ss.str("");
                                ss << $1.cod << $3.cod;
                                ss << "mov " << $1.dir << " A\n";
                                ss << "addi #" << $1.dbase << "\n";
                                ss << "mov " << $3.dir << " @A\n";
                                $$.cod = ss.str();
                                ss.str("");
                           }
        | tkwrite pari Expr pard pyc {ss.str("");
                                      ss << $3.cod << "\nwri " << $3.dir << "\nwrl\n";
                                      $$.cod = ss.str();
                                      ss.str("");}
        | tkread pari Ref pard pyc {}
        | tkif pari Expr pard Instr {}
        | tkif pari Expr pard Instr tkelse Instr {}
        | tkwhile pari Expr pard Instr {}
;

Expr    : Expr relop Esimple {}
        | Esimple {$$.cod = $1.cod;
                   $$.tipo = $1.tipo;
                   $$.dir = $1.dir;
                   }
;

Esimple : Esimple addop Term {//Falta arreglar
                                $$.dir = nuevaTemp();
                                ss.str("");
                                ss << $1.cod << $3.cod << "\nmov " << $1.dir << " A\naddi " << $3.dir;
                                ss << "\nmov A " << $$.dir << "\n";
                                $$.cod = ss.str();
                                ss.str("");
                                $$.tipo = ENTERO; 
}
        | Term {$$.cod = $1.cod;
                $$.tipo = $1.tipo;
                $$.dir = $1.dir;}
;

Term    : Term mulop Factor {}
        | Factor {$$.cod = $1.cod;
                  $$.tipo = $1.tipo;
                  $$.dir = $1.dir;}
;

Factor  : Ref { if($1.tipo > REAL){
                        errorSemantico(ERR_FALTAN, $1.nlin, $1.ncol, $1.lexema);
                }
                $$.dir = nuevaTemp();
                ss.str("");
                ss << $1.cod << "mov " << $1.dir << " A\n" << "addi #" << $1.dbase << "\nmov @A " << $$.dir << "\n";
                $$.cod = ss.str();
                $$.tipo = $1.tipo;
                ss.str("");
          }
        | nentero {$$.dir = nuevaTemp();
                   ss.str("");
                   ss << "mov #" << $1.lexema << " " << $$.dir << "\n";
                   $$.cod = ss.str();
                   ss.str("");
                   $$.tipo = ENTERO;}
        | nreal {$$.dir = nuevaTemp();
                 ss.str("");
                 ss << "mov $" << $1.lexema << " " << $$.dir << "\n";
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
                        ss << "mov #0 " << $$.dir << "\n";
                        $$.cod = ss.str();
                        ss.str("");
                        $$.tipo = s->tipo;
                        $$.dbase = s->dir;

                }
        }
        | Ref cori {if($1.tipo <= REAL){ errorSemantico(ERR_SOBRAN,$1.nlin,$1.ncol, $1.lexema);}} 
          Esimple cord { if($4.tipo != ENTERO){
                                errorSemantico(ERR_INDICE_ENTERO, $4.nlin, $4.ncol, $4.lexema);
                         }
                         else{
                                 $$.dir = nuevaTemp();
                                 $$.dbase = $1.dbase;
                                 $$.tipo = tt->tipos[$1.tipo].tipoBase;
                                 ss.str("");
                                 ss << $1.cod << $4.cod << "mov " << $1.dir << " A\n";
                                 ss << "muli #" << tt->tipos[$1.tipo].limiteSuperior - tt->tipos[$1.tipo].limiteInferior + 1;
                                 ss << "\naddi " << $4.dir;
                                 ss << "\nsubi #" << tt->tipos[$1.tipo].limiteInferior;
                                 ss << "\nmov A " << $$.dir << "\n";
                                 $$.cod = ss.str();
                                 ss.str("");
                         }


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
