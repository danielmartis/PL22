import java.io.RandomAccessFile;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;
import java.util.Vector;
import java.util.LinkedList;
class AnalizadorSintacticoSLR{
    int estado;
    Stack<Integer> estados = new Stack<Integer>();
    //Queue<Integer> reglas = new LinkedList();
    AnalizadorLexico al;
    Token t = new Token();
    Vector<String> reglas = new Vector<String>();
    String ira= "";
    String [] simb = new String[12];
    ArrayList<Integer> tk = new ArrayList<Integer>();
    public AnalizadorSintacticoSLR(AnalizadorLexico alt){

        al = alt;
        t = al.siguienteToken();
        estado = 0;
        estados.push(0);
        simb[0] = "S";
        simb[1] = "C";
        simb[2] = "B";
        simb[3] = "P";
        simb[4] = "D";
        simb[5] = "FV";
        simb[6] = "Tipo";
        simb[7] = "Bloque";
        simb[8] = "SecInstr";
        simb[9] = "Instr";
        simb[10] = "Expr";
        simb[11] = "Term";
        ira= "";
    }
    public void imprimir(){
        StringBuilder sb = new StringBuilder();
        for (int i = reglas.size()-1; i >= 0; i--){
            sb.append(reglas.elementAt(i)+" ");
        }
        System.out.println(sb.toString());
    }

    public  void errorSintaxis(ArrayList<Integer> tk){
        StringBuilder posibles = new StringBuilder();
        Token p = new Token();
        for(int i = 0; i<tk.size(); i++){
            p.tipo = tk.get(i);  
            posibles.append(p.toString());
            posibles.append(" ");
        }
        if(t.tipo == Token.EOF){
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba  " + posibles.toString());
        }
        else{
            System.err.println("Error sintactico (" + t.fila + "," + t.columna +"): encontrado '" + t.lexema + "', esperaba " + posibles.toString());
        }
        System.exit(-1);
    }
    public final void emparejar(int tokEsperado){
        if(t.tipo == tokEsperado){
            t = al.siguienteToken();
        }
        else{
            tk.add(tokEsperado);
            errorSintaxis(tk);
        }
    }
    public void popEst(int cant){
        for(int i = 0; i<cant; i++){
            estados.pop();
        }
    }
    public void actEst(int est){
        estado = est;
        estados.push(est);
    }
    public void analizar(){
        boolean terminado = false;
        do{
            //System.out.println("Estado: " + estado + " Ir a: " + ira + " Token: " + t.lexema); 
            if(estado == 0){
                if(ira==simb[0]){
                    estado = 1;
                    ira="";
                    estados.push(1);
                }
                else if(ira==simb[1]){
                    estado = 2;
                    ira="";
                    estados.push(2);
                }
                else if(t.tipo == Token.CLASS){
                    estado = 3;
                    estados.push(3);
                    emparejar(Token.CLASS);
                }
                else{
                    tk.add(Token.CLASS);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 1){
                if(t.tipo == Token.EOF){
                    estado = -1;
                    terminado = true;
                }
                else{
                    tk.add(Token.EOF);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 2){
                if(t.tipo == Token.EOF){
                       popEst(1);
                       estado = estados.peek();
                       ira= simb[0];
                       reglas.add("1");       
                }
                else{
                    tk.add(Token.EOF);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 3){
                if(t.tipo == Token.ID){
                    actEst(4);
                    emparejar(Token.ID);
                }
                else{
                	tk.add(Token.ID);
                	errorSintaxis(tk);
                }
            }

            else if (estado == 4){
                if(t.tipo == Token.LLAVEI){
                    actEst(5);
                    emparejar(Token.LLAVEI);
                }
                else{
                	tk.add(Token.LLAVEI);
                	errorSintaxis(tk);
                }
            }
            else if (estado == 5){
                if(ira== simb[2]){
                    actEst(6);
                    ira= "";
                }
                else if(t.tipo == Token.PUBLIC){
                    actEst(7);
                    emparejar(Token.PUBLIC);
                }
                else if(t.tipo == Token.LLAVED){
                    ira= simb[2];
                    reglas.add("4");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.PUBLIC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 6){
                if(t.tipo == Token.LLAVED){
                    actEst(8);
                    emparejar(Token.LLAVED);
                }
                else{
                    tk.add(Token.LLAVED);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 7){
                if(t.tipo == Token.DOSP){
                    actEst(9);
                    emparejar(Token.DOSP);
                }
                else{
                    tk.add(Token.DOSP);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 8){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT || t.tipo == Token.EOF){
                    popEst(5);
                    ira= simb[1];
                    estado = estados.peek();
                    reglas.add("2");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    tk.add(Token.EOF);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 9){
                if(ira==simb[1]){
                    ira="";
                    actEst(13);
                }
                else if(ira==simb[3]){
                    ira="";
                    actEst(10);
                }
                else if(ira==simb[4]){
                    ira="";
                    actEst(11);
                }
                else if(ira==simb[6]){
                    ira="";
                    actEst(12);
                }
                else if(t.tipo == Token.CLASS){
                    actEst(3);
                    emparejar(Token.CLASS);
                }
                
                else if(t.tipo == Token.FLOAT){
                    actEst(15);
                    emparejar(Token.FLOAT);
                }
                else if(t.tipo == Token.INT){
                    actEst(14);
                    emparejar(Token.INT);
                }
                else if(t.tipo == Token.LLAVED){
                    ira= simb[3];
                    reglas.add("6");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 10){
                if(t.tipo == Token.LLAVED){
                    popEst(3);
                    estado = estados.peek();
                    ira= simb[2];
                    reglas.add("3");
                }
                else{
                    tk.add(Token.LLAVED);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 11){
                if(ira==simb[1]){
                    ira="";
                    actEst(13);
                }
                else if(ira==simb[3]){
                    ira="";
                    actEst(16);
                }
                else if(ira==simb[4]){
                    ira="";
                    actEst(11);
                }
                else if(ira==simb[6]){
                    ira="";
                    actEst(12);
                }
                else if(t.tipo == Token.CLASS){
                    actEst(3);
                    emparejar(Token.CLASS);
                }
                
                else if(t.tipo == Token.FLOAT){
                    actEst(15);
                    emparejar(Token.FLOAT);
                }
                else if(t.tipo == Token.INT){
                    actEst(14);
                    emparejar(Token.INT);
                }
                else if(t.tipo == Token.LLAVED){
                    ira= simb[3];
                    reglas.add("6");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 12){
                if(t.tipo == Token.ID){
                    actEst(17);
                    emparejar(Token.ID);
                }
                else{
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 13){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT){
                    popEst(1);
                    estado = estados.peek();    
                    ira= simb[4];
                    reglas.add("8");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 14){
                if(t.tipo == Token.ID){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[6];
                    reglas.add("11");
                }
                else{
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 15){
                if(t.tipo == Token.ID){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[6];
                    reglas.add("12");
                }
                else{
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 16){
                if(t.tipo == Token.LLAVED){
                    popEst(2);
                    estado = estados.peek();
                    ira= simb[3];
                    reglas.add("5");
                }
                else{
                    tk.add(Token.LLAVED);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 17){
                if(ira== simb[5]){
                    actEst(18);
                    ira="";
                }
                else if(t.tipo == Token.PARI){
                    actEst(19);
                    emparejar(Token.PARI);
                }
                else if(t.tipo == Token.PYC){
                    actEst(20);
                    emparejar(Token.PYC);
                }
                else{
                    tk.add(Token.PARI);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 18){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT){
                    popEst(3);
                    ira= simb[4];
                    estado = estados.peek();
                    reglas.add("7");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 19){
                if(ira== simb[6]){
                    ira="";
                    estado = 21;
                    estados.push(21);
                }
                else if(t.tipo == Token.INT){
                    actEst(14);
                    emparejar(Token.INT);
                }
                else if(t.tipo == Token.FLOAT){
                    actEst(15);
                    emparejar(Token.FLOAT);
                }
                else{
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 20){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[5];
                    reglas.add("10");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 21){
               if(t.tipo == Token.ID){
                   actEst(22);
                   emparejar(Token.ID);
               }
               else{
                   tk.add(Token.ID);
                   errorSintaxis(tk);
               }
            }
            else if (estado == 22){
                if(t.tipo == Token.PARD){
                    actEst(23);
                    emparejar(Token.PARD);
                }
                else{
                    tk.add(Token.PARD);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 23){
                if(ira== simb[7]){
                    actEst(24);
                    ira="";
                }
                else if(t.tipo == Token.LLAVEI){
                    actEst(25);
                    emparejar(Token.LLAVEI);
                }
                else{
                    tk.add(Token.LLAVEI);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 24){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT){
                    popEst(5);
                    estado = estados.peek();
                    reglas.add("9");
                    ira= simb[5];
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 25){
                if(ira== simb[6]){
                    actEst(28);
                    ira= "";
                }
                else if(ira== simb[8]){
                    actEst(26);
                    ira= "";
                }
                else if(ira== simb[9]){
                    actEst(27);
                    ira= "";
                }
                else if(t.tipo == Token.ID){
                    actEst(29);
                    emparejar(Token.ID);
                }
                else if(t.tipo == Token.LLAVED){
                    ira= simb[8];
                    reglas.add("15");
                }

                else if(t.tipo == Token.INT){
                    actEst(14);
                    emparejar(Token.INT);
                }
                else if(t.tipo == Token.FLOAT){
                    actEst(15);
                    emparejar(Token.FLOAT);
                }
                else{
                    tk.add(Token.LLAVEI);
                    tk.add(Token.LLAVED);
                    tk.add(Token.FLOAT);
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 26){
                if(t.tipo == Token.LLAVED){
                    actEst(30);
                    emparejar(Token.LLAVED);
                }
                else{
                    tk.add(Token.LLAVED);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 27){
                if(t.tipo == Token.PYC){
                    actEst(31);
                    emparejar(Token.PYC);
                }
                else{
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 28){
                if(t.tipo == Token.ID){
                    actEst(32);
                    emparejar(Token.ID);
                }
                else{
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 29){
                if(t.tipo == Token.ASIG){
                    actEst(33);
                    emparejar(Token.ASIG);
                }
                else{
                    tk.add(Token.ASIG);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 30){
                if(t.tipo == Token.CLASS || t.tipo == Token.LLAVED || t.tipo == Token.INT || t.tipo == Token.FLOAT){
                    popEst(3);
                    estado = estados.peek();
                    ira= simb[7];
                    reglas.add("13");
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.CLASS);

                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 31){
                if(ira== simb[6]){
                    actEst(28);
                    ira= "";
                }
                else if(ira== simb[8]){
                    actEst(34);
                    ira="";
                }
                else if(ira== simb[9]){
                    actEst(27);
                    ira= "";
                }
                else if(t.tipo == Token.ID){
                    actEst(29);
                    emparejar(Token.ID);
                }
                else if(t.tipo == Token.LLAVED){
                    ira= simb[8];
                    reglas.add("15");
                }
                else if(t.tipo == Token.FLOAT){
                    actEst(15);
                    emparejar(Token.FLOAT);
                }
                else if(t.tipo == Token.INT){
                    actEst(14);
                    emparejar(Token.INT);
                }
                else{
                    tk.add(Token.LLAVED);
                    tk.add(Token.FLOAT);
                    tk.add(Token.INT);
                    tk.add(Token.ID);
                    errorSintaxis(tk);
                }

            }
            else if (estado == 32){
                if(t.tipo == Token.PYC){
                    popEst(2);
                    estado = estados.peek();
                    ira= simb[9];
                    reglas.add("16");
                }
                else{
                	tk.add(Token.PYC);
                	errorSintaxis(tk);
                }
            }
            else if (estado == 33){
                if(ira== simb[10]){
                    actEst(35);
                    ira= "";
                }
                else if (ira== simb[11]){
                    actEst(36);
                    ira= "";
                }
                else if(t.tipo == Token.ID){
                    actEst(39);
                    emparejar(Token.ID);
                }
                else if(t.tipo == Token.NUMREAL){
                    actEst(37);
                    emparejar(Token.NUMREAL);
                }
                else if(t.tipo == Token.NUMINT){
                    actEst(38);
                    emparejar(Token.NUMINT);
                }
                else{
                    tk.add(Token.NUMINT);
                    tk.add(Token.ID);
                    tk.add(Token.NUMREAL);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 34){
                if(t.tipo == Token.LLAVED){
                    popEst(3);
                    estado = estados.peek();
                    ira= simb[8];
                    reglas.add("14");
                }
                else{
                    tk.add(Token.LLAVED);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 35){
                if(t.tipo == Token.PYC){
                    popEst(3);
                    estado = estados.peek();
                    ira= simb[9];
                    reglas.add("17");
                }
                else if(t.tipo == Token.OPAS){
                    actEst(40);
                    emparejar(Token.OPAS);
                }
                else{
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 36){
                if(t.tipo == Token.PYC || t.tipo == Token.OPAS){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[10];
                    reglas.add("19");
                }
                else{
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 37){
                if(t.tipo == Token.PYC || t.tipo == Token.OPAS){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[11];
                    reglas.add("20");
                }
                else{
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 38){
                if(t.tipo == Token.PYC || t.tipo == Token.OPAS){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[11];
                    reglas.add("21");
                }
                else {
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 39){
                if(t.tipo == Token.PYC){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[11];
                    reglas.add("22");
                }
                else if(t.tipo == Token.OPAS){
                    popEst(1);
                    estado = estados.peek();
                    ira= simb[11];
                    reglas.add("22");
                }
                else{
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }
            else if (estado == 40){
                if(ira== simb[11]){
                    ira= "";
                    actEst(41);
                }
                else if(t.tipo == Token.ID){
                    actEst(39);
                    emparejar(Token.ID);
                }
                else if(t.tipo == Token.NUMREAL){
                    actEst(37);
                    emparejar(Token.NUMREAL);
                }
                else if(t.tipo == Token.NUMINT){
                    actEst(38);
                    emparejar(Token.NUMINT);
                }
                else{
                    tk.add(Token.NUMINT);
                    tk.add(Token.ID);
                    tk.add(Token.NUMREAL);
                }
            }
            else if (estado == 41){
                if(t.tipo == Token.PYC || t.tipo == Token.OPAS){
                    popEst(3);
                    estado = estados.peek();
                    ira= simb[10];
                    reglas.add("18");
                }
                else{
                    tk.add(Token.OPAS);
                    tk.add(Token.PYC);
                    errorSintaxis(tk);
                }
            }

        }while(!terminado);
        imprimir();
    }

}
