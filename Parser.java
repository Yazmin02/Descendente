
import java.util.List;
import java.util.Stack;

public class Parser {

    private final List<Token> tokens;
    private Stack<String> pila;
    private String[][] Producciones;



    private final Token identificador = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token select = new Token(TipoToken.SELECT, "select");
    private final Token from = new Token(TipoToken.FROM, "from");
    private final Token distinct = new Token(TipoToken.DISTINCT, "distinct");
    private final Token coma = new Token(TipoToken.COMA, ",");
    private final Token punto = new Token(TipoToken.PUNTO, ".");
    private final Token asterisco = new Token(TipoToken.ASTERISCO, "*");
    private final Token finCadena = new Token(TipoToken.EOF, "");

    private int i = 0;
    private boolean hayErrores = false;

    private Token preanalisis;

    public Parser(List<Token> tokens){
        this.tokens = tokens;
        pila=new Stack<>();   

        

        Producciones=new String[][]{
            {"Q", "select D from T"},
            {"D", "distinctP"},
            {"D","P"},
            {"P", "*"},
            {"P","A"},
            {"A", "A1A2"},
            {"A1", ""},
            {"A1",",A"},
            {"A2","idA3"}, 
            {"A3",".id"},
            {"A3",""},
            {"T","T2T1"},
            {"T1",",T"},
            {"T1",""},
            {"T2", "idT3"},
            {"T3","id"},
            {"T3",""}
        };
    }

    public void parse(){
        i = 0;
        preanalisis = tokens.get(i);

        
       
            if (preanalisis.equals(select)) {
                pila.pop();
            }
            else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba la palabra reservada SELECT.");
            }
        
        


        
        if(!hayErrores && !preanalisis.equals(finCadena)){
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
            
        }
        else if(!hayErrores && preanalisis.equals(finCadena)){
            System.out.println("Consulta válida");
        }


        /*if(!preanalisis.equals(finCadena)){
            System.out.println("Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        }else if(!hayErrores){
            System.out.println("Consulta válida");
        }*/
    }


    void Q(){
        if(preanalisis.equals(select)){
            coincidir(select);
            D();
            coincidir(from);
            T();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba la palabra reservada SELECT.");
        }
    }

    void D(){
        if(hayErrores) return;

        if(preanalisis.equals(distinct)){
            coincidir(distinct);
            P();
        }
        else if(preanalisis.equals(asterisco) || preanalisis.equals(identificador)){
            P();
        }

        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba DISTINCT, * o un identificador.");
        }
    }

    void P(){
        if(hayErrores) return;

        if(preanalisis.equals(asterisco)){
            coincidir(asterisco);
        }
        else if(preanalisis.equals(identificador)){
            A();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición \" + preanalisis.posicion + \". Se esperaba * o un identificador.");
        }
    }

    void A(){
        if(hayErrores) return;

        if(preanalisis.equals(identificador)){
            A2();
            A1();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un identificador.");
        }
    }

    void A1(){
        if(hayErrores) return;

        if(preanalisis.equals(coma)){
            coincidir(coma);
            A();
        }
    }

    void A2(){
        if(hayErrores) return;

        if(preanalisis.equals(identificador)){
            coincidir(identificador);
            A3();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un identificador.");
        }
    }

    void A3(){
        if(hayErrores) return;

        if(preanalisis.equals(punto)){
            coincidir(punto);
            coincidir(identificador);
        }
    }

    void T(){
        if(hayErrores) return;

        if(preanalisis.equals(identificador)){
            T2();
            T1();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un identificador.");
        }
    }

    void T1(){
        if(hayErrores) return;

        if(preanalisis.equals(coma)){
            coincidir(coma);
            T();
        }
    }

    void T2(){
        if(hayErrores) return;

        if(preanalisis.equals(identificador)){
            coincidir(identificador);
            T3();
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un identificador.");
        }
    }

    void T3(){
        if(hayErrores) return;

        if(preanalisis.equals(identificador)){
            coincidir(identificador);
        }
    }


    void coincidir(Token t){
        if(hayErrores) return;

        if(preanalisis.tipo == t.tipo){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error en la posición " + preanalisis.posicion + ". Se esperaba un  " + t.tipo);

        }
    }

}
