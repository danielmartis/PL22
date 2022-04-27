import java.util.ArrayList;
import java.util.Stack;

class TraductorDR{


    public class Atributos{
        String trad, atrib;
        boolean acce;
        public Atributos(String t, String a){
            trad = t;
            atrib = a;
        }

        public Atributos(String t, boolean a){
            trad = t;
            acce = a;
        }

    }


    String vacio =  " ";

    public AnalizadorLexico al;
    
    public StringBuilder reglas;

    public boolean recopilar;

    public Token token;

    ArrayList<Integer> tokE;

    public StringBuilder errores;

    Stack<String> ambitos = new Stack<String>();

    String traduccion;

    String ambitoActual;

    TablaSimbolos ts;



    public TraductorDR(AnalizadorLexico ale){
        al = ale;
        token = al.siguienteToken();
        recopilar = false;
        reglas = new StringBuilder();
        tokE = new ArrayList<Integer>();
        ambitoActual = " ";
        traduccion = "";
        ts = new TablaSimbolos(null);
    }

    public void comprobarFinFichero(){
        System.out.println(reglas.toString());
    }

    private void nregla(int regla){
        if(recopilar){
            reglas.append(" " + regla);
        }
    }

    public void errorSintactico(ArrayList<Integer> te){
        errores = new StringBuilder();
        for(int i = 0; i<te.size(); i++){
            errores.append(Token.nombreToken.get(te.get(i)));
            errores.append(" ");
        }
        if(token.tipo == Token.EOF){
            System.err.println("Error sintactico: encontrado fin de fichero, esperaba " + errores.toString());
        }
        else{
            System.err.println("Error sintactico (" + token.fila + "," + token.columna + ")" + ": encontrado '" + token.lexema + "', esperaba " + errores.toString());
        }
        System.exit(-1);
    } 

    public void emparejar(int tokEsperado)
    {
        if (token.tipo == tokEsperado){
            token = al.siguienteToken();
        }
        else{
            tokE.add(tokEsperado);
            errorSintactico(tokE);
        }
    }

    public String S(){
        if(token.tipo == Token.CLASS){
            nregla(1);
            return C(true);
        }
        else{
            tokE.add(Token.CLASS);
            errorSintactico(tokE);
        }
        return "";
    }

    public String C(boolean acce){
        if(token.tipo == Token.CLASS){
            String id;
            Atributos bs,vs;
            Simbolo s;
            nregla(2);
            emparejar(Token.CLASS);
            id = token.lexema;
            s = new Simbolo(id, 3, ambitoActual + id);

            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }
            emparejar(Token.ID);
            emparejar(Token.LLAVEI);
            ts = new TablaSimbolos(ts);
            if(ambitoActual.equals(vacio)){
                ambitoActual = id;
                ambitos.add(id);
            }
            else{
                ambitoActual = ambitoActual + "_" + id;
                ambitos.add(ambitoActual);
            }
            bs = B(acce);
            vs = V(acce);
            emparejar(Token.LLAVED);
            ts = ts.getPadre();
            
            
            if(!bs.atrib.equals(vacio) || ! vs.atrib.equals(vacio)){
                String tr =  "global " + ambitoActual + "{\n" + bs.atrib + vs.atrib + "}\n" + bs.trad + vs.trad;  
                ambitoActual = ambitos.pop();
                if(!ambitos.empty()){
                    ambitoActual = ambitos.peek();
                }
                return tr;
            }
            else{
                String tr =  bs.trad + vs.trad;

                ambitoActual = ambitos.pop();
                if(!ambitos.empty()){
                    ambitoActual = ambitos.peek();
                }
                return tr;            } 

        }
        else{
            tokE.add(Token.CLASS);
            errorSintactico(tokE);
        }
        return "";
    }

    public Atributos B(boolean acce){
        if(token.tipo == Token.PUBLIC){
            Atributos ptrad;
            nregla(3);
            emparejar(Token.PUBLIC);
            emparejar(Token.DOSP);
            if(acce){
                ptrad = P(true);
            }
            else{
                ptrad = P(false);
            }
            return ptrad;
        }
        else if(token.tipo == Token.PRIVATE || token.tipo == Token.LLAVED){
            nregla(4);
            return new Atributos(vacio, vacio);
        }
        else{
            tokE.add(Token.LLAVED);
            tokE.add(Token.PUBLIC);
            tokE.add(Token.PRIVATE);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    public Atributos V(boolean acce){
        if(token.tipo == Token.PRIVATE){
            Atributos ptrad;
            nregla(5);
            emparejar(Token.PRIVATE);
            emparejar(Token.DOSP);
            ptrad = P(false);
            return ptrad;
        }
        else if(token.tipo == Token.LLAVED){
            nregla(6);
            return new Atributos(vacio,vacio);
        }
        else{
            tokE.add(Token.LLAVED);
            tokE.add(Token.PRIVATE);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    public Atributos P(boolean acce){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT || token.tipo == Token.CLASS){
            Atributos ds;
            Atributos ps;
            nregla(7);
            ds = D(acce);
            ps = P(acce);
            if(ds.atrib.equals(vacio) && ps.atrib.equals(vacio)){
                return new Atributos(ds.trad + ps.trad, vacio);
            }
            return new Atributos(ds.trad + ps.trad, ds.atrib + ps.atrib);
        }
        else if(token.tipo == Token.PRIVATE || token.tipo == Token.LLAVED){
            nregla(8);
            return new Atributos(vacio,vacio);
        }
        else{
            tokE.add(Token.CLASS);
            tokE.add(Token.LLAVED);
            tokE.add(Token.PRIVATE);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    public Atributos D(boolean acce){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT){
            Atributos fvs;
            String tipotrad;
            String id;
            nregla(9);
            tipotrad = Tipo();
            id = token.lexema;
            
            emparejar(Token.ID);
            //System.out.println(tipotrad);
            fvs = FV(tipotrad, id, acce);
            if(fvs.acce){
                return new Atributos(vacio, fvs.trad);
            }
            else{
                return new Atributos(fvs.trad, vacio);
            }
            //return fvs;
        }
        else if(token.tipo == Token.CLASS){
            nregla(10);
            String ctrad;
            ctrad = C(acce);
            return new Atributos(ctrad, vacio);
        }
        else{
            tokE.add(Token.CLASS);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    public Atributos FV(String tipo, String id, boolean acce){
        if(token.tipo == Token.PARI){
            String tipotrad,ltrad,bloquetrad, id2;
            String trad = "";
            nregla(11);
            Simbolo s = new Simbolo(id, 3, ambitoActual + "." + id);
            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }
            emparejar(Token.PARI);
            ts = new TablaSimbolos(ts);
            tipotrad = Tipo();
            id2 = token.lexema;
            if(tipotrad.equals("entero")){
                s = new Simbolo(id2, 1,  id2);
            }
            else{
                s = new Simbolo(id2,2,id2);
            }
            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }
            emparejar(Token.ID);
            ltrad = L();
            emparejar(Token.PARD);
            bloquetrad = Bloque(tipo);
            if(!acce){
                trad = "private_";
            }
            trad = "fun " + trad +  ambitoActual + "_" + id + "(" + id2 + ":" + tipotrad + ltrad +"):" + tipo + "\n" + bloquetrad;
            ts = ts.getPadre();
            return new Atributos(trad, false);

        }
        else if(token.tipo == Token.PYC){
            nregla(12);
            emparejar(Token.PYC);
            Simbolo s;
            if(tipo.equals("entero")){
                s = new Simbolo(id, 1, ambitoActual + "." + id);
            }
            else{
                s = new Simbolo(id,2,ambitoActual + "." + id);
            }
            
            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }
            return new Atributos(id + ":" + tipo + "\n", true);
        }
        else{
            tokE.add(Token.PARI);
            tokE.add(Token.PYC);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    public String L(){
        if(token.tipo == Token.COMA){
            String tipotrad, ltrad, id;
            Simbolo s;
            nregla(13);
            emparejar(Token.COMA);
            tipotrad = Tipo();
            id = token.lexema;
            //System.out.println("id:" + id + " tipo:" + tipotrad);
            if(tipotrad.equals("entero")){
                s = new Simbolo(id,1,id);
            }
            else{
                s = new Simbolo(id, 2,id);
            }
            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }
            emparejar(Token.ID);
            ltrad = L();
            return ";" + id + ":" + tipotrad + ltrad;
        }
        else if(token.tipo == Token.PARD){
            nregla(14);
            return "";
        }
        else{
            tokE.add(Token.PARD);
            tokE.add(Token.COMA);
            errorSintactico(tokE);
        }
        return "";
    }

    public String Tipo(){
        if(token.tipo == Token.INT){
            nregla(15);
            emparejar(Token.INT);
            return "entero";
        }
        else if(token.tipo == Token.FLOAT){
            nregla(16);
            emparejar(Token.FLOAT);
            return "real";
        }
        else{
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
        return "";
    }

    public String Bloque(String tipo){
        if(token.tipo == Token.LLAVEI){
            String sectrad;
            //System.out.println(tipo);

            nregla(17);
            ts = new TablaSimbolos(ts);
            emparejar(Token.LLAVEI);
            sectrad = SecInstr(tipo);
            emparejar(Token.LLAVED);    
            ts = ts.getPadre();
            return "{\n" + sectrad + "\n}\n";

        }
        else{
            tokE.add(Token.LLAVEI);
            errorSintactico(tokE);
        }
        return "";
    }

    public String SecInstr(String tipo){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT || token.tipo == Token.ID || token.tipo == Token.LLAVEI || token.tipo == Token.RETURN || token.tipo == Token.IF){
            String instrtrad, sectrad;
            nregla(18);
            instrtrad = Instr(tipo);
            emparejar(Token.PYC);
            sectrad = SecInstr(tipo);
            return instrtrad + sectrad;
        }
        else if(token.tipo == Token.LLAVED){
            nregla(19);
            return vacio;
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.LLAVEI);
            tokE.add(Token.LLAVED);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            tokE.add(Token.RETURN);
            tokE.add(Token.IF);
            errorSintactico(tokE);
        }
        return "";
    }

    public String Instr(String tipo){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT){
            String tipotrad, id;
            Simbolo s;
            nregla(20);
            tipotrad = Tipo();
            id = token.lexema;
            if(tipotrad.equals("entero")){
                s = new Simbolo(id,1,id);
            }
            else{
                s = new Simbolo(id, 2,id);
            }
            if(!ts.newSymb(s)){
                errorSemantico(1, token);
            }

            emparejar(Token.ID);
            return "var " + id + ":" + tipotrad + ";\n";
        }
        else if(token.tipo == Token.ID){
            //Hacer luego
            String id;
            Atributos expr;
            Simbolo s;
            Token token2;
            token2 = token;
            nregla(21);
            id = token.lexema;
            if(ts.searchSymb(token.lexema) == null){
                errorSemantico(2,token);
            }
            else if((s = ts.searchSymb(token.lexema)).tipo == 3){
                errorSemantico(3,token);
            }
            s = ts.searchSymb(token.lexema);
        
            emparejar(Token.ID);
            token2 = token;
            emparejar(Token.ASIG);
            expr = Expr();
            if(expr.atrib.equals("entero") && s.tipo == 2){
                return s.nomtrad + ":= itor(" + expr.trad + ");\n"; 
            }
            else if(expr.atrib.equals("real") && s.tipo == 1){
                errorSemantico(4,token2);
            }
            return s.nomtrad + ":=" + expr.trad + ";\n";
        }
        else if(token.tipo == Token.LLAVEI){
            String bloquetrad;
            nregla(22);
            bloquetrad = Bloque(tipo);
            return bloquetrad;
        }
        else if(token.tipo == Token.RETURN){
            //Hacer luego.
            Token token2;
            Atributos expr;
            nregla(23);
            token2 = token;
            emparejar(Token.RETURN);
            expr = Expr();
            //System.out.println(tipo + " " + expr.atrib);
            if(tipo.equals("entero") && expr.atrib.equals("real")){
                errorSemantico(4,token2);
            }
            else if(tipo.equals("real") && expr.atrib.equals("entero")){
                return "ret itor(" + expr.trad + ");\n"; 
            }
            return "ret " + expr.trad + ";\n";
        }
        else if(token.tipo == Token.IF){
            String bloquetrad,iptrad;
            Atributos expr;
            Token token2;
            token2 = token;
            nregla(24);
            emparejar(Token.IF);
            expr = Expr();
            if(expr.atrib.equals("real")){
                errorSemantico(5,token2);
            }
            bloquetrad = Bloque(tipo);
            iptrad = Ip(tipo);
            return "if (" + expr.trad + ")\n" + bloquetrad + iptrad;
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.LLAVEI);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            tokE.add(Token.RETURN);
            tokE.add(Token.IF);
            errorSintactico(tokE);
        }
        return "";
    }

    public String Ip(String tipo){
        if(token.tipo == Token.ELSE){
            String bloquetrad;
            nregla(25);
            emparejar(Token.ELSE);
            bloquetrad = Bloque(tipo);
            return "else" + bloquetrad;
        }
        else if(token.tipo == Token.PYC){
            nregla(26);
        }
        else{
            tokE.add(Token.PYC);
            tokE.add(Token.ELSE);
        }
        return "";
    }

    public Atributos Expr(){
        if(token.tipo == Token.NUMREAL || token.tipo == Token.NUMINT || token.tipo == Token.ID || token.tipo == Token.PARI){
            Atributos term, exprp;
            nregla(27);
            term = Term();
            //System.out.println(term.atrib);
            exprp = Exprp(term.trad, term.atrib);
            return exprp;
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
        return new Atributos("","");

    }

    public Atributos Exprp(String tr, String t){
        if(token.tipo == Token.OPAS){
            Atributos term, exprp;
            String si;
            nregla(28);
            si = token.lexema;
            emparejar(Token.OPAS);
            term = Term();
            if(t.equals(term.atrib)){
                if(t.equals("entero")){
                    tr = tr + si + "i " + term.trad;
                }
                else{
                    tr = tr + si + "r " + term.trad;
                }
            }
            else{
                if(t.equals("entero")){
                    tr = "itor(" + tr + ")" + si + "r " + term.trad;
                    t = "real";
                }
                else{
                    tr = tr + si + "r " + "itor(" + term.trad + ")";
                }
            }
            exprp = Exprp(tr,t);
            return exprp;
        }
        else if(token.tipo == Token.PYC || token.tipo == Token.LLAVEI || token.tipo == Token.PARD){
            nregla(29);
            return new Atributos(tr, t);
        }
        else{
            tokE.add(Token.LLAVEI);
            tokE.add(Token.PARD);
            tokE.add(Token.PYC);
            tokE.add(Token.OPAS);
            errorSintactico(tokE);
        }
        return new Atributos("","");

    }

    public Atributos Term(){
        if(token.tipo == Token.NUMREAL || token.tipo == Token.NUMINT || token.tipo == Token.ID || token.tipo == Token.PARI){
            Atributos factor, termp;
            nregla(30);
            factor = Factor();
            termp = Termp(factor.trad,factor.atrib);
            //System.out.println("factor: " + factor.atrib + " termp: " + termp.atrib);
            return termp;
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
        return new Atributos("","");

    }

    public Atributos Termp(String tr, String t){
        if(token.tipo == Token.OPMD){
            Atributos factor, termp;
            String si;
            si = token.lexema;
            nregla(31);
            emparejar(Token.OPMD);
            factor = Factor();
            if(t.equals(factor.atrib)){
                if(t.equals("entero")){
                    tr = tr + si + "i " + factor.trad;
                }
                else{
                    tr = tr + si + "r " + factor.trad;
                }
            }
            else{
                if(t.equals("entero")){
                    tr = "itor(" + tr + ")" + si + "r " + factor.trad;
                    t = "real";
                }
                else{
                    tr = tr + si + "r " + "itor(" + factor.trad + ")";
                }
            }
            termp = Termp(tr,t);
            return termp;
        }
        else if(token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.LLAVEI || token.tipo == Token.PARD){
            nregla(32);
            return new Atributos(tr,t);
        }
        else{
            tokE.add(Token.LLAVEI);
            tokE.add(Token.PARD);
            tokE.add(Token.PYC);
            tokE.add(Token.OPAS);
            tokE.add(Token.OPMD);
            errorSintactico(tokE);
        }
        return new Atributos("","");

    }

    public Atributos Factor(){
        if(token.tipo == Token.NUMREAL){
            Atributos num;
            num = new Atributos(token.lexema, "real");
            nregla(33);
            emparejar(Token.NUMREAL);
            return num;
        }
        else if(token.tipo == Token.NUMINT){
            Atributos num;
            num = new Atributos(token.lexema, "entero");
            nregla(34);
            emparejar(Token.NUMINT);
            return num;
        }
        else if(token.tipo == Token.ID){
            Atributos id = new Atributos("","");
            Simbolo s;
            //Hacer luego
            if(ts.searchSymb(token.lexema) == null){
                errorSemantico(2,token);
            }
            else if((s = ts.searchSymb(token.lexema)).tipo == 3){
                errorSemantico(3,token);
            }
            
            else if(s.tipo == 1){
                id = new Atributos(s.nomtrad, "entero");
                //System.out.println(s.nombre + " " + s.tipo);
            }
            else{
                id = new Atributos(s.nomtrad,"real");
                //System.out.println(s.nombre + " " + s.tipo);

            }
            

            nregla(35);
            emparejar(Token.ID);
            return id;
        }
        else if(token.tipo == Token.PARI){
            Atributos expr;
            nregla(36);
            emparejar(Token.PARI);
            expr = Expr();
            emparejar(Token.PARD);
            expr = new Atributos("( " + expr.trad + " )", expr.atrib); 
            return expr;
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
        return new Atributos("","");
    }

    private final int ERRYADECL=1,ERRNODECL=2,ERRNOSIMPLE=3,ERRTIPOS=4,ERRNOENTERO=5;
    private void errorSemantico(int nerror,Token tok)
    {
        System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
        switch (nerror) {
            case ERRYADECL: System.err.println("ya existe en este ambito");
                break;
            case ERRNODECL: System.err.println("no ha sido declarado");
                break;
            case ERRNOSIMPLE: System.err.println("no es una variable");
                break;
            case ERRTIPOS: System.err.println("la expresion debe ser de tipo entero y es real");
                break;
            case ERRNOENTERO: System.err.println("la expresion debe ser de tipo entero");
                break;
        }
        System.exit(-1);
    }
}
