import java.io.RandomAccessFile;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;

class AnalizadorLexico{
        int fil;
        int col;
        int bytes;
        RandomAccessFile archivo;
        Token t;
        public AnalizadorLexico(RandomAccessFile file){
            fil = 1;
            col = 1;
            bytes = 0;
            archivo = file;
            t = new Token();
    }

        public int leerCaracter(){
            int leido = 0;
            try {
                leido = archivo.read();
                bytes++;
            }catch(EOFException e){
                return 21;
            }
            catch(IOException ie){
                System.exit(-1);
            }
            return leido;
        }

        public boolean letraDigito(char car){
            if((car>=65 && car<=90) || car >= 97 && car<=122){
                return true;
            }
            return false;
        }

        public boolean esNumero(char num){
            if(num>=48 && num<57){
                return true;
            }
            return false;
        }

        public boolean esLetra(char let){
            if((let>=65 && let<=90) || (let >= 97 && let<=122)){
                return true;
            }
            return false;
        }

        public Token siguienteToken(){
            int estado = 0;
            int b = 0;
            StringBuilder tok = new StringBuilder();
            char leido;
            boolean espacio = false;
            t = new Token();
            /*Comentario para que ocupe más el archivo*/
            b = leerCaracter();
            if(b == -1){
                t.tipo = 21;
                t.columna = col;
                t.fila = fil;
                return t;
            }
            leido = (char) b;
            do{
                if(leido == ' ' || leido == '\t'){
                    col = col+1;
                    b = leerCaracter();
                    leido = (char) b;
                    espacio = true;
                }
                else if(leido == '\n'){
                    col = 1;
                    fil++;
                    b = leerCaracter();
                    leido = (char) b;
                    espacio = true;
                }
                else{
                    espacio = false;
                }
            }while(espacio);
            t.fila = fil;
            t.columna = col;
            if(b == -1){
                t.tipo = 21;
                t.columna = col;
                t.fila = fil;
                return t;
            }
            /*Comentario para aumentar el tamaño*/
            switch(estado){
                case 0:
                    if(leido == '{'){
                        t.lexema = "{";
                        t.tipo = 8;
                    }
                    else if(leido == '}'){
                        t.lexema = "}";
                        t.tipo = 9;
                    }
                    else if(leido == '('){
                        t.lexema = "(";
                        t.tipo = 0;
                    }
                    else if (leido == ')'){
                        t.lexema = ")";
                        t.tipo = 1;
                    }
                    else if(leido == ':'){
                        t.lexema = ":";
                        t.tipo = 5;
                    }
                    else if(leido == '='){
                        t.lexema = "=";
                        t.tipo = 7;
                    }
                    else if(leido == ';'){
                        t.lexema = ";";
                        t.tipo = 4;
                    }
                    else if(leido == '+' || leido == '-'){
                        if(leido == '+')
                            t.lexema = "+";
                        else
                            t.lexema = "-";
                        t.tipo = 3;
                    }
                    else if(esLetra(leido)){
                        estado = 12;
                    }
                    else if(esNumero(leido)){
                        estado = 1;
                    }
                    else if(leido == '/'){
                        t.lexema = "/";
                        t.tipo = 2;
                        estado = 7;
                    }
                    else{
                        System.err.println("Error lexico (" + fil + "," + col + "): caracter '" + leido + "' incorrecto");
                        System.exit(-1);
                    }
                case 1:
                    if(estado == 1){
                        do{
                            tok.append(leido);
                            b = leerCaracter();
                            leido = (char) b;
                        }while(esNumero(leido));
                        if(leido == '.'){
                            estado = 3;
                        }
                        else{
                            bytes--;
                            try{
                                archivo.seek(bytes);
                            }catch(Exception e){
                                System.out.println("Error al mover el puntero");
                                System.exit(-1);
                            }
                            t.lexema = tok.toString();
                            t.tipo = 18;
                        }
                    }
                case 3:
                    if(estado == 3){
                        b = leerCaracter();
                        leido = (char) b;
                        if(esNumero(leido)){
                            tok.append('.');
                            do{
                                tok.append(leido);
                                b = leerCaracter();
                                leido = (char) b; 
                            }while(esNumero(leido));
                            bytes--;
                            t.lexema = tok.toString();
                            t.tipo = 20;
                            try{
                                archivo.seek(bytes);
                            }catch(Exception e){
                                System.out.println("Error al mover el puntero");
                                System.exit(-1);
                            }
                        }
                        else{
                            bytes--;
                            bytes--;
                            try{
                                archivo.seek(bytes);
                            }catch(Exception e){
                                System.out.println("Error al mover el puntero");
                                System.exit(-1);
                            }
                            t.lexema = tok.toString();
                            t.tipo = 18;
                        }
                    }
                case 7:
                    if(estado == 7){
                        boolean ast = false;
                        boolean terminado = false;
                        tok.append(leido);
                        b = leerCaracter();
                        leido = (char) b;
                        if(leido == '*'){
                            col++; col++;
                            ast = true;
                            do{
                                b = leerCaracter();
                                leido = (char) b;
                                if(b == -1){
                                    System.err.println("Error lexico: fin de fichero inesperado");
                                    System.exit(-1);
                                }
                                col++;
                                if(leido == '\n'){
                                    fil++;
                                    col = 1;
                                }
                                else if(ast && leido == '/'){
                                    terminado = true;
                                }
                                else if (leido == '*'){
                                    ast = true;
                                }
                                else{
                                    ast = false;
                                }
                            }while(!terminado);
                            return siguienteToken();
                        }
                        else{
				System.err.println("Error lexico (" + fil + "," + col + "): caracter '/' incorrecto");
                        }
                    }

                case 12:
                    if(estado == 12){
                        do{
                            tok.append(leido);
                            b = leerCaracter();
                            leido = (char) b;
                        }while(esLetra(leido) || esNumero(leido));
                        t.lexema = tok.toString();
                        bytes--;
                        try{
                            archivo.seek(bytes);
                        }catch(Exception e){
                            System.out.println("Error al mover el puntero");
                            System.exit(-1);
                        }
                        if(t.lexema.equals("class")){
                            t.tipo = 10;
                        }
                        else if(t.lexema.equals("public")){
                            t.tipo = 11;
                        }
                        else if(t.lexema.equals("int")){
                            t.tipo = 14;
                        }
                        else if(t.lexema.equals("float")){
                            t.tipo = 13;
                        }

                        else{
                            t.tipo = 19;
                        }
                    }
            }
            col += t.lexema.length();
            return t;
        }
}
