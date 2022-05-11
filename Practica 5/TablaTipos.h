
#ifndef _TablaTipos_
#define _TablaTipos_

#include <vector>

using namespace std;

#include "TablaSimbolos.h"

const unsigned TIPOBASICO=0,ARRAY=1;

/*
  el constructor mete en el vector los tipos básicos, ENTERO y REAL
  de manera que las primeras posiciones del vector son siempre los
  tipos básicos, luego para saber si un tipo es básico sólo hay que
  ver si es menor o igual que el último de ellos (en este caso, REAL)
*/
#define ES_TIPOBASICO(i)  ((i)<=REAL)



struct unTipo {
  unsigned clase;             // TIPOBASICO o ARRAY
  unsigned limiteInferior;
  unsigned limiteSuperior;
  unsigned tipoBase;
};

class TablaTipos {

  public:
  
     vector<unTipo> tipos;
     
     TablaTipos();
     unsigned nuevoTipoArray(unsigned linf,unsigned lsup,unsigned tbase);

};

#endif
