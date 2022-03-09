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
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add("+ -");
		nombreToken.add(";");
		nombreToken.add(":");
		nombreToken.add("=");
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
		ASIG		= 6,
		LLAVEI          = 7,
		LLAVED          = 8,
		CLASS		= 9,
		PUBLIC		= 10,
		FLOAT		= 11,
		INT		= 12,
		NUMINT		= 13,
		ID		= 14,
		NUMREAL		= 15,
		EOF		= 16;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

