


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
TablaSimbolos ts(NULL);
stack<string> ambitos;
string ambitoActual = "";
%}
%%

S   : {$$.acce = true;} C {int tk = yylex();
         if(tk != 0){
            yyerror("");
         }
         else{
            $$.cod = $1.cod;
            cout << $$.cod;
        }}
;

C   : tkclass id {
                  if(ambitos.empty()){
                     ambitoActual = $2.lexema;
                     ambitos.push(ambitoActual);
                  }         
                  else{
                     ambitoActual = ambitoActual + "_" + $2.lexema;
                     ambitos.push(ambitoActual);
                  }
                  if(ts.searchSymb($2.lexema) != NULL) /*añadir error*/; 
                  else{
                        Simbolo s;
                        ts.newSymb(s);
                    }
                 }
      llavei {
                ts = TablaSimbolos(ts); $$.acce = $0.acce; 
             } 
      B {
            $$.acce = false;
        } 
      V llaved {
        ts = ts.getPadre();
        ss.str("");
        ss << "class " << $2.lexema << "{" << $6.cod + $8.cod << "}";
        $$.cod = ss.str();
        ss.str("");
      }
;

B   : tkpublic dosp {$$.acce = $0.acce;} P { 
        $$.cod = $4.cod; $$.atrib = $4.atrib;
      }
    |
;

V   : tkprivate dosp {$$.acce = false;} P { 
        $$.cod = $4.cod; $$.atrib = $4.atrib;
    }
    |
;

P   : {$$.acce = $0.acce;} D {$$.acce = $0.acce;} P {}

    |
;

D   : Tipo id {if(ts.searchSymb($2.lexema) == NULL) /*añadir error*/; $$.tipo = $1.tipo; } 
      FV {}

    | {$$.acce = $0.acce;} C {}
;

FV  : pari L pard Bloque {}

    | pyc {$$.cod = ";";}
;

L   : Tipo id coma L {
                      ss.str(""); 
                      ss << $2.lexema << ": " << $1.cod << "; " << $4.cod; 
                      $$.cod = ss.str(); 
                      ss.str("");
                     }
                    
    | Tipo id { 
                ss.str("");
                ss << $2.lexema << ":" << $1.cod;
                $$.cod = ss.str();
                ss.str("");
              }
;
Tipo    : tkint   { $$.cod = "entero"; $$.tipo = ENTERO;}

        | tkfloat { $$.cod = "real"; $$.tipo = REAL;}
;

Bloque  : llavei SecInstr llaved {$$.cod = "\n{\n" + $2.cod + "\n}\n";  }
;

SecInstr    : Instr pyc SecInstr {$$.cod = $1.cod + ";" + $3.cod;}
            |
;

Instr   : Tipo id {$$.cod = $1.cod + $2.lexema;}

        | id asig Expr {
                         ss.str(""); 
                         //Faltan comprobaciones
                         ss << $1.lexema << " := " << $3.cod;
                         ss.str("");
                       }
                              
        | Bloque {$$.cod = $1.cod;}
        
        | tkreturn Expr {//Faltan comprobaciones 
                         $1.cod = "ret" + $2.cod;}
                               
        | tkif Expr Bloque Ip {//Faltan comprobaciones 
                               $$.cod = "if(" + $2.cod + ")" + $3.cod + $4.cod;}
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
        
        | id {Simbolo *s = ts.searchSymb($1.lexema);
          if(s == NULL){
          	//errorSemantico(2, $1.lexema, $1.nlin, $1.ncol);
          }
          else{
          }
        }
        
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
