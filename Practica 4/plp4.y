


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


string operador, s1, s2;  // string auxiliares
stringstream ss;
TablaSimbolos* ts = new TablaSimbolos(NULL);
stack<string> ambitos;
string ambitoActual = "";
%}
%%

S   : {$$.acce = true;} C {int tk = yylex();
         if(tk != 0){
            yyerror("");
         }
         else{
            $$.cod = $2.cod;
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
                  if(ts->searchSymb($2.lexema) != NULL) /*añadir error*/; 
                  else{
                        Simbolo s;
                        ts->newSymb(s);
                    }
                 }
      llavei {
                ts = new TablaSimbolos(ts); $$.acce = $0.acce; 
             } 
      B {
            $$.acce = false;
        } 
      V llaved {
        ts = ts->getPadre();
        ss.str("");
        if($6.atrib != ""|| $8.atrib != ""){
           ss << "global " << ambitoActual << "{\n" << $6.atrib << "\n" << $8.atrib << "\n}\n";
        }
        ambitos.pop();
        if(!ambitos.empty()){
           ambitoActual = ambitos.top();
        }
        ss << $6.cod << $8.cod;
        $$.cod = ss.str();
        ss.str("");
        //cout << $$.cod;
      }
;

B   : tkpublic dosp {$$.acce = $0.acce;} P { 
        $$.cod = $4.cod; $$.atrib = $4.atrib;
      }
    | {$$.cod = ""; $$.atrib = "";}
;

V   : tkprivate dosp {$$.acce = false;} P { 
        $$.cod = $4.cod; $$.atrib = $4.atrib;
    }
    | {$$.cod = ""; $$.atrib = "";}
;

P   : {$$.acce = $0.acce;} D {$$.acce = $0.acce;} P {
                                                      $$.cod = $2.cod + $4.cod;
                                                      $$.atrib = $2.atrib + $4.atrib;
                                                   }

    | {$$.cod = ""; $$.atrib = "";}
;

D   : Tipo id { $$.tipo = $1.tipo;$$.acce = $0.acce; $$.nombre = $2.lexema;} FV {
                                                                               if($4.cod == ";"){
                                                                                    Simbolo s;
                                                                                    s.nombre = $2.lexema;
                                                                                    ss.str("");
                                                                                    ss << ambitoActual << "." << $2.lexema;
                                                                                    s.nomtrad = ss.str();
                                                                                    s.tipo = $1.tipo;
                                                                                    if(!ts->newSymb(s)){
                                                                                       errorSemantico(ERRYADECL,$2.lexema,$2.nlin,$2.ncol);
                                                                                    }
                                                                                    ss.str("");
                                                                                    s.tipo = $1.tipo;
                                                                                    ss << $2.lexema << ":" << $1.cod << "\n";
                                                                                    $$.cod = "";
                                                                                    $$.atrib = ss.str();
                                                                                    ss.str("");
                                                                               }
                                                                               else{
                                                                                    $$.cod = $4.cod;
                                                                                    $$.atrib = "";
                                                                               }
                                                                           }

    | {$$.acce = $0.acce;} C { $$.cod = $2.cod; $$.atrib = "";}
;

FV  : pari {ts = new TablaSimbolos(ts);
            Simbolo s;
            s.nombre = $0.nombre;
            s.tipo = FUNCLAS;
            ss.str("");
            ss << ambitoActual << "." << $0.cod;
            s.nomtrad = ss.str();
            ss.str("");
            if(!ts->newSymb(s)){
               errorSemantico(ERRYADECL,$1.lexema,$1.nlin,$1.ncol);
            }
            ts = new TablaSimbolos(ts);
         } L pard {$$.tipo = $0.tipo;} Bloque {
                                                ss.str("");
                                                if(!$0.acce){
                                                   ss << "fun private_" << ambitoActual << "_" << $0.nombre << "(" << $3.cod << "):";
                                                }
                                                else{
                                                   ss << "fun " << ambitoActual << "_" << $0.nombre << "(" << $3.cod << "):";
                                                }
                                                if($0.tipo == ENTERO){
                                                   ss << "entero";
                                                }
                                                else{
                                                   ss << "real";
                                                }
                                                ss << $6.cod << "\n";
                                                $$.cod = ss.str();
                                                ss.str("");
                                                ts = ts->getPadre();
                                              }

    | pyc {
            $$.cod = ";";
            $$.atrib = true;
         }
;

L   : Tipo id {   Simbolo s;
                  s.nombre = s.nomtrad = $2.lexema;
                  s.tipo = $1.tipo;
                  if(ts->newSymb(s) == false){
                     errorSemantico(ERRYADECL,$2.lexema,$2.nlin,$2.ncol);
                  }
               }
               coma L {
                      ss.str(""); 
                      ss << $2.lexema << ": " << $1.cod << "; " << $5.cod; 
                      $$.cod = ss.str(); 
                      ss.str("");
                     }
                    
    | Tipo id { 
                ss.str("");
                ss << $2.lexema << ":" << $1.cod;
                Simbolo s;
                s.nombre = s.nomtrad = $2.lexema;
                s.tipo = $1.tipo;
                if(ts->newSymb(s) == false){
                     errorSemantico(ERRYADECL,$2.lexema,$2.nlin,$2.ncol);
                }
                $$.cod = ss.str();
                ss.str("");
              }
;
Tipo    : tkint   { $$.cod = "entero"; $$.tipo = ENTERO;}

        | tkfloat { $$.cod = "real"; $$.tipo = REAL;}
;

Bloque  : llavei {$$.tipo = $0.tipo; ts = new TablaSimbolos(ts);} SecInstr llaved {ts = ts->getPadre(); $$.cod = "\n{\n" + $3.cod + "\n}";  }
;

SecInstr    : {$$.tipo = $0.tipo;} Instr pyc {$$.tipo = $0.tipo;} SecInstr {$$.cod = $2.cod + "\n" + $5.cod;}
            | {$$.cod = "";}
;

Instr   : Tipo id {
                   Simbolo s;
                   s.nombre = s.nomtrad = $2.lexema;
                   s.tipo = $1.tipo;
                   if(!ts->newSymb(s)){
                      errorSemantico(ERRYADECL,$2.lexema,$2.nlin,$2.ncol);
                   }
                   ss.str("");
                   ss << "var " << $2.lexema << ": " << $1.cod << ";";
                   $$.cod = ss.str();
                   ss.str("");}

        | id asig Expr { Simbolo *s = ts->searchSymb($1.lexema);
                         ss.str("");
                         if(s == NULL){
                              errorSemantico(ERRNODECL,$1.lexema,$1.nlin,$1.ncol);
                         }
                        if(s->tipo == FUNCLAS){
                           errorSemantico(ERRNOSIMPLE, $1.lexema,$1.nlin,$1.ncol);
                        }
                         if(s->tipo == ENTERO && $3.tipo == REAL){
                            errorSemantico(ERRTIPOS,$2.lexema,$2.nlin,$2.ncol);
                         }
                         else if(s->tipo == REAL && $3.tipo == ENTERO){
                            ss << s->nomtrad << ":= itor(" << $3.cod << ");";
                         } 
                         else{
                           ss << s->nomtrad << " := " << $3.cod << ";";
                         }
                         $$.cod = ss.str();
                         ss.str("");
                       }
                              
        | {$$.tipo = $0.tipo; ts = new TablaSimbolos(ts);} Bloque {ts = ts->getPadre(); $$.cod = $1.cod;}
        
        | {$$.tipo = $0.tipo;}tkreturn Expr {
                                                if($1.tipo == ENTERO && $3.tipo == REAL){
                                                   errorSemantico(ERRTIPOS,$2.lexema,$2.nlin,$2.ncol);
                                                }
                                                else if($1.tipo == REAL && $3.tipo == ENTERO){
                                                   ss.str("");
                                                   ss << "ret " << "itor(" << $3.cod << ");";
                                                   $$.cod = ss.str();
                                                   ss.str("");
                                                } 
                                                else{
                                                   $$.cod = "ret " + $3.cod + ";";
                                                }
                                             }
                                                
                               
        | tkif Expr {$$.tipo = $0.tipo;} Bloque {$$.tipo = $0.tipo;} Ip {
                                                                           if($2.tipo == REAL){
                                                                              errorSemantico(ERRNOENTERO,$1.lexema,$1.nlin,$1.ncol);
                                                                           }
                                                                           $$.cod = "if (" + $2.cod + ")" + $4.cod + $6.cod;}
;

Ip  : tkelse {$$.tipo = $0.tipo;} Bloque {$$.cod = "else " + $3.cod;}
    | {$$.cod = "";}
;

Expr    : Expr opas Term { if($1.tipo == ENTERO && $3.tipo == REAL){
                              $$.tipo = REAL;
                              ss.str("");
                              ss << "itor(" << $1.cod << ")" << " " << $2.lexema << "r " << $3.cod;
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           else if($1.tipo == REAL && $3.tipo == ENTERO){
                              $$.tipo = REAL;
                              ss.str("");
                              ss <<$1.cod << " " << $2.lexema << "r " << "itor(" << $3.cod << ")";
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           else if ($1.tipo == ENTERO){
                              $$.tipo = $1.tipo;
                              ss.str("");
                              ss << $1.cod <<" " << $2.lexema <<"i " << $3.cod;
                              $$.cod = ss.str();
                              ss.str("");

                           }
                           else{
                              $$.tipo = $1.tipo;
                              ss.str("");
                              ss << $1.cod << " " << $2.lexema << "r " << $3.cod;
                              $$.cod = ss.str();
                              ss.str("");
                           }
}

        | Term {$$.tipo = $1.tipo; $$.cod = $1.cod;}
;

Term    : Term opmd Factor {if($1.tipo == ENTERO && $3.tipo == REAL){
                              $$.tipo = REAL;
                              ss.str("");
                              ss << "itor(" << $1.cod << ") " << $2.lexema << "r " << $3.cod;
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           else if($1.tipo == REAL && $3.tipo == ENTERO){
                              $$.tipo = REAL;
                              ss.str("");
                              ss << $1.cod << " " << $2.lexema << "r " << " itor(" << $3.cod << ")";
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           else if ($1.tipo == ENTERO){
                              $$.tipo = $1.tipo;
                              ss.str("");
                              ss<< $1.cod << " " << $2.lexema << "i " <<  $3.cod;
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           else{
                              $$.tipo = $1.tipo;
                              ss.str("");
                              ss << $1.cod << " " << $2.lexema << "r " << $3.cod;
                              $$.cod = ss.str();
                              ss.str("");
                           }
                           }


        | Factor {$$.tipo = $1.tipo; $$.cod = $1.cod;}
;

Factor  : numentero {$$.cod = $1.lexema; $$.tipo = ENTERO;}

        | numreal {$$.cod = $1.lexema; $$.tipo = REAL;}
        
        | id {Simbolo *s = ts->searchSymb($1.lexema);
          if(s == NULL){
          	 errorSemantico(2, $1.lexema, $1.nlin, $1.ncol);
          }
          if(s->tipo == FUNCLAS){
             errorSemantico(ERRNOSIMPLE, $1.lexema, $1.nlin, $1.ncol);
          }
          else{
               $$.cod = s->nomtrad;
               $$.tipo = s->tipo;}
        }
        
        | pari Expr pard {$$.cod = "(" + $2.cod + ")"; $$.tipo = $2.tipo;}
;




%%



//***-------------------- en plp4.y -------------------------------
/// ---------- Errores semánticos ---------------------------------





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
