
#ifndef _TablaSimbolos_
#define _TablaSimbolos_

#include <string>
#include <vector>

using namespace std;

const int ENTERO=1;
const int REAL=2;
const int FUNCLAS=3;

struct Simbolo {

  string nombre;
  int tipo;
  string nomtrad;
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
