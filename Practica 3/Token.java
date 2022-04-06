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
		nombreToken.add("* /");
		nombreToken.add("+ -");
		nombreToken.add(";");
		nombreToken.add(":");
		nombreToken.add(",");
		nombreToken.add("=");
		nombreToken.add("{");
		nombreToken.add("}");
		nombreToken.add("'class'");
		nombreToken.add("'public'");
		nombreToken.add("'private'");
		nombreToken.add("'float'");
		nombreToken.add("'int'");
		nombreToken.add("'return'");
		nombreToken.add("'if'");
		nombreToken.add("'else'");
		nombreToken.add("numero entero");
		nombreToken.add("identificador");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

	public int tipo;		// tipo es: ID, NUMINT, NUMREAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		OPMD		= 2,
		OPAS		= 3,
		PYC		= 4,
		DOSP		= 5,
		COMA		= 6,
		ASIG		= 7,
		LLAVEI          = 8,
		LLAVED          = 9,
		CLASS		= 10,
		PUBLIC		= 11,
		PRIVATE		= 12,
		FLOAT		= 13,
		INT		= 14,
		RETURN		= 15,
		IF              = 16,
		ELSE            = 17,
		NUMINT		= 18,
		ID		= 19,
		NUMREAL		= 20,
		EOF		= 21;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

