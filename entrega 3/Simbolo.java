
public class Simbolo {

  // los símbolos pueden ser variables enteras o reales, o funciones/clases. 
  public static final int ENTERO=1, REAL=2, FUNCLAS=3;
  

  /**
   * nombre del símbolo en el programa fuente
   */
  public String nombre;
  
  /**
   * tipo (ENTERO, REAL, FUNCLAS) 
   */
  public int tipo;        
  
  /**
   * nombre traducido al lenguaje objeto (sólo para atributos, se genera en la declaración)
   */
  public String nomtrad;
  
  
  /**
   * constructor
   * @param nombre  nombre en el programa fuente
   * @param tipo    tipo con el que se declara
   * @param nomtrad nombre en el lenguaje objeto
   */
  public Simbolo(String nombre,int tipo,String nomtrad)
  {
    this.nombre = nombre;
    this.tipo = tipo;
    this.nomtrad = nomtrad;
  }

}
