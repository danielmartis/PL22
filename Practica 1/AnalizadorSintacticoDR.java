import java.util.ArrayList;

class AnalizadorSintacticoDR{

    public AnalizadorLexico al;
    public StringBuilder reglas;
    public boolean recopilar;
    public Token token;
    ArrayList<Integer> tokE;
    public StringBuilder errores;
    public AnalizadorSintacticoDR(AnalizadorLexico ale){
        al = ale;
        token = al.siguienteToken();
        recopilar = true;
        reglas = new StringBuilder();
        tokE = new ArrayList<Integer>();
    }

    public void comprobarFinFichero(){
        System.out.println(reglas.toString());
    }

    private void nregla(String regla){
        if(recopilar){
            reglas.append(regla);
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

    public void S(){
        if(token.tipo == Token.CLASS){
            nregla(" 1");
            C();
        }
        else{
            tokE.add(Token.CLASS);
            errorSintactico(tokE);
        }
    }

    public void C(){
        if(token.tipo == Token.CLASS){
            nregla(" 2");
            emparejar(Token.CLASS);
            emparejar(Token.ID);
            emparejar(Token.LLAVEI);
            B();
            V();
            emparejar(Token.LLAVED);
        }
        else{
            tokE.add(Token.CLASS);
            errorSintactico(tokE);
        }
    }

    public void B(){
        if(token.tipo == Token.PUBLIC){
            nregla(" 3");
            emparejar(Token.PUBLIC);
            emparejar(Token.DOSP);
            P();
        }
        else if(token.tipo == Token.PRIVATE || token.tipo == Token.LLAVED){
            nregla(" 4");
        }
        else{
            tokE.add(Token.LLAVED);
            tokE.add(Token.PUBLIC);
            tokE.add(Token.PRIVATE);
            errorSintactico(tokE);
        }
    }

    public void V(){
        if(token.tipo == Token.PRIVATE){
            nregla(" 5");
            emparejar(Token.PRIVATE);
            emparejar(Token.DOSP);
            P();
        }
        else if(token.tipo == Token.LLAVED){
            nregla(" 6");
        }
        else{
            tokE.add(Token.LLAVED);
            tokE.add(Token.PRIVATE);
            errorSintactico(tokE);
        }
    }

    public void P(){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT || token.tipo == Token.CLASS){
            nregla(" 7");
            D();
            P();
        }
        else if(token.tipo == Token.PRIVATE || token.tipo == Token.LLAVED){
            nregla(" 8");
        }
        else{
            tokE.add(Token.CLASS);
            tokE.add(Token.LLAVED);
            tokE.add(Token.PRIVATE);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
    }

    public void D(){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT){
            nregla(" 9");
            Tipo();
            emparejar(Token.ID);
            FV();
        }
        else if(token.tipo == Token.CLASS){
            nregla(" 10");
            C();
        }
        else{
            tokE.add(Token.CLASS);
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
    }

    public void FV(){
        if(token.tipo == Token.PARI){
            nregla(" 11");
            emparejar(Token.PARI);
            Tipo();
            emparejar(Token.ID);
            L();
            emparejar(Token.PARD);
            Bloque();
        }
        else if(token.tipo == Token.PYC){
            nregla(" 12");
            emparejar(Token.PYC);
        }
        else{
            tokE.add(Token.PARI);
            tokE.add(Token.PYC);
            errorSintactico(tokE);
        }
    }

    public void L(){
        if(token.tipo == Token.COMA){
            nregla(" 13");
            emparejar(Token.COMA);
            Tipo();
            emparejar(Token.ID);
            L();
        }
        else if(token.tipo == Token.PARD){
            nregla(" 14");
        }
        else{
            tokE.add(Token.PARD);
            tokE.add(Token.COMA);
            errorSintactico(tokE);
        }
    }

    public void Tipo(){
        if(token.tipo == Token.INT){
            nregla(" 15");
            emparejar(Token.INT);
        }
        else if(token.tipo == Token.FLOAT){
            nregla(" 16");
            emparejar(Token.FLOAT);
        }
        else{
            tokE.add(Token.INT);
            tokE.add(Token.FLOAT);
            errorSintactico(tokE);
        }
    }

    public void Bloque(){
        if(token.tipo == Token.LLAVEI){
            nregla(" 17");
            emparejar(Token.LLAVEI);
            SecInstr();
            emparejar(Token.LLAVED);
        }
        else{
            tokE.add(Token.LLAVEI);
            errorSintactico(tokE);
        }
    }

    public void SecInstr(){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT || token.tipo == Token.ID || token.tipo == Token.LLAVEI || token.tipo == Token.RETURN || token.tipo == Token.IF){
            nregla(" 18");
            Instr();
            emparejar(Token.PYC);
            SecInstr();
        }
        else if(token.tipo == Token.LLAVED){
            nregla(" 19");
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
    }

    public void Instr(){
        if(token.tipo == Token.INT || token.tipo == Token.FLOAT){
            nregla(" 20");
            Tipo();
            emparejar(Token.ID);
        }
        else if(token.tipo == Token.ID){
            nregla(" 21");
            emparejar(Token.ID);
            emparejar(Token.ASIG);
            Expr();
        }
        else if(token.tipo == Token.LLAVEI){
            nregla(" 22");
            Bloque();
        }
        else if(token.tipo == Token.RETURN){
            nregla(" 23");
            emparejar(Token.RETURN);
            Expr();
        }
        else if(token.tipo == Token.IF){
            nregla(" 24");
            emparejar(Token.IF);
            Expr();
            Bloque();
            Ip();
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
    }

    public void Ip(){
        if(token.tipo == Token.ELSE){
            nregla(" 25");
            emparejar(Token.ELSE);
            Bloque();
        }
        else if(token.tipo == Token.PYC){
            nregla(" 26");
        }
        else{
            tokE.add(Token.PYC);
            tokE.add(Token.ELSE);
        }
    }

    public void Expr(){
        if(token.tipo == Token.NUMREAL || token.tipo == Token.NUMINT || token.tipo == Token.ID || token.tipo == Token.PARI){
            nregla(" 27");
            Term();
            Exprp();
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
    }

    public void Exprp(){
        if(token.tipo == Token.OPAS){
            nregla(" 28");
            emparejar(Token.OPAS);
            Term();
            Exprp();
        }
        else if(token.tipo == Token.PYC || token.tipo == Token.LLAVEI || token.tipo == Token.PARD){
            nregla(" 29");
        }
        else{
            tokE.add(Token.LLAVEI);
            tokE.add(Token.PARD);
            tokE.add(Token.PYC);
            tokE.add(Token.OPAS);
            errorSintactico(tokE);
        }
    }

    public void Term(){
        if(token.tipo == Token.NUMREAL || token.tipo == Token.NUMINT || token.tipo == Token.ID || token.tipo == Token.PARI){
            nregla(" 30");
            Factor();
            Termp();
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
    }

    public void Termp(){
        if(token.tipo == Token.OPMD){
            nregla(" 31");
            emparejar(Token.OPMD);
            Factor();
            Termp();
        }
        else if(token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.LLAVEI || token.tipo == Token.PARD){
            nregla(" 32");
        }
        else{
            tokE.add(Token.LLAVEI);
            tokE.add(Token.PARD);
            tokE.add(Token.PYC);
            tokE.add(Token.OPAS);
            tokE.add(Token.OPMD);
            errorSintactico(tokE);
        }
    }

    public void Factor(){
        if(token.tipo == Token.NUMREAL){
            nregla(" 33");
            emparejar(Token.NUMREAL);
        }
        else if(token.tipo == Token.NUMINT){
            nregla(" 34");
            emparejar(Token.NUMINT);
        }
        else if(token.tipo == Token.ID){
            nregla(" 35");
            emparejar(Token.ID);
        }
        else if(token.tipo == Token.PARI){
            nregla(" 36");
            emparejar(Token.PARI);
            Expr();
            emparejar(Token.PARD);
        }
        else{
            tokE.add(Token.ID);
            tokE.add(Token.PARI);
            tokE.add(Token.NUMREAL);
            tokE.add(Token.NUMINT);
            errorSintactico(tokE);
        }
    }
}