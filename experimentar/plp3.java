import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

class plp3 {
    public static void main(String[] args) {

        if (args.length == 1)
        {
          try {
            RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
            AnalizadorLexico al = new AnalizadorLexico(entrada);
            TraductorDR tdr = new TraductorDR(al);

            String trad = tdr.S(); // simbolo inicial de la gramatica
            tdr.comprobarFinFichero();
            System.out.println(trad);
          }
          catch (FileNotFoundException e) {
            System.out.println("Error, fichero no encontrado: " + args[0]);
          }
        } 
        else System.out.println("Error, uso: java plp3 <nomfichero>");
    }
}

