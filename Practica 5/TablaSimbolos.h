

#ifndef _TablaSimbolos_
#define _TablaSimbolos_

#include <string>
#include <vector>

using namespace std;

const unsigned ENTERO=0;
const unsigned REAL=1;

struct Simbolo {

  string nombre;
  unsigned tipo;
  unsigned dir;
  unsigned tam;
};


class TablaSimbolos {

   private:
   
      bool buscarAmbito(Simbolo s); // ver si está en el ámbito actual

   public:
   
      TablaSimbolos *padre;
      vector<Simbolo> simbolos;
   
      TablaSimbolos(TablaSimbolos *padre);
      TablaSimbolos *getPadre() { return padre; }

      bool newSymb(Simbolo s);
      Simbolo* searchSymb(string nombre);
};
   
#endif
