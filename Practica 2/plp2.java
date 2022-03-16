import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;

class plp2 {
    public static void main(String[] args) {

        if (args.length == 1)
        {
          try {
            RandomAccessFile entrada = new RandomAccessFile(args[0],"r");
            AnalizadorLexico al = new AnalizadorLexico(entrada);
            AnalizadorSintacticoSLR aslr = new AnalizadorSintacticoSLR(al);

            aslr.analizar();
          }
          catch (FileNotFoundException e) {
            System.out.println("Error, fichero no encontrado: " + args[0]);
          }
        } 
        else System.out.println("Error, uso: java plp2 <nomfichero>");
    }
}

