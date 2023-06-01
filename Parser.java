
import java.util.List;
import java.util.Stack;

public class Parser {

    private final List<Token> tokens;
    private Stack<String> pila;
    private String[][] Producciones;
    private String[] Terminales;



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

         Terminales =new String[] {"select", "distinct", "*", ",", "id", "from"};
         Producciones =new String[][] {
                {"Q", "select D from T"},
                {"D", "distinct P"},
                {"D", "P"},
                {"P", "*"},
                {"P", "A"},
                {"A", "A2 A1"},
                {"A1", ", A"},
                {"A1", "Ɛ"},
                {"A2", "id A3"},
                {"A3", ". id"},
                {"A3", "Ɛ"},
                {"T", "T2 T1"},
                {"T1", ", T"},
                {"T1", "Ɛ"},
                {"T2", "id T3"},
                {"T3", "id"},
                {"T3", "Ɛ"}
    };
    }

    public void parse(){
        i = 0;
        preanalisis = tokens.get(i);
        System.out.println(Producciones[0][0] + Producciones[0][1]);
        analizar();
        

        
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

    void analizar() {
        
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
