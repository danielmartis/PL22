
import java.util.ArrayList;


public class TablaSimbolos {

   /**
    * referencia a la tabla de símbolos del ámbito anterior
    */
   private TablaSimbolos padre;
   
   /**
    * vector de símbolos
    */
   private ArrayList<Simbolo> simbolos;
   
   /**
    * constructor
    * @param padre referencia a la tabla de símbolos anterior, o null si es la primera
    */
   public TablaSimbolos(TablaSimbolos padre) {
      this.padre = padre;
      simbolos = new ArrayList<Simbolo>();
   }

   /**
    * getter
    * @return tabla de símbolos anterior (o null si no hay)
    */
   public TablaSimbolos getPadre() {
      return padre;
   }

   /**
    * buscar el símbolo sólo en el ámbito actual, para ver si existe
    * @param s símbolo
    * @return true si existe, false en otro caso
    */
   private boolean buscarAmbito(Simbolo s) {
     for (Simbolo ss:simbolos)
       if (ss.nombre.equals(s.nombre))
          return true;
     return false;
   }
   
   /**
    * añade un símbolo al ámbito, si no existe
    * @param s símbolo
    * @return true si ha podido añadirlo, false si ya existía
    */
   public boolean newSymb(Simbolo s) {
     if (buscarAmbito(s))  // repetido en el ámbito
       return false;
     simbolos.add(s);
     return true;
   }
   
   /**
    * busca un símbolo en el ámbito actual y recursivamente en ámbitos anteriores
    * @param nombre nombre del símbolo
    * @return símbolo o null si no lo encuentra
    */
   public Simbolo searchSymb(String nombre) {
     for (Simbolo s:simbolos)
       if (s.nombre.equals(nombre)) return s;
       
     if (padre != null)
       return padre.searchSymb(nombre);
     else
       return null;
   }
}
