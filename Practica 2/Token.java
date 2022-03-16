/**
 * @author Jorge Calera (curso 2012-2013)
 *  (con modificaciones/adaptaciones de FMS)
*/
import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("("); //0
		nombreToken.add(")"); //1
		nombreToken.add("+ -"); //2
		nombreToken.add(";"); //3
		nombreToken.add(":"); //4
		nombreToken.add("="); //
		nombreToken.add("{");
		nombreToken.add("}");
		nombreToken.add("'class'");
		nombreToken.add("'public'");
		nombreToken.add("'float'");
		nombreToken.add("'int'");
		nombreToken.add("numero entero");
		nombreToken.add("identificador");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

	public int tipo;		// tipo es: ID, NUMINT, NUMREAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		OPAS		= 2,
		PYC		= 3,
		DOSP		= 4,
		ASIG		= 5,
		LLAVEI          = 6,
		LLAVED          = 7,
		CLASS		= 8,
		PUBLIC		= 9,
		FLOAT		= 10,
		INT		= 11,
		NUMINT		= 12,
		ID		= 13,
		NUMREAL		= 14,
		EOF		= 15;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

