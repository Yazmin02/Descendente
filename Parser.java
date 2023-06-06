import java.util.List;
import java.util.Stack;

// select * from tabla
// Non-terminal\Terminal|  select           |  from  |  distinct   |  *  |  ,      |  .      |  id       |  $
//----------------------| --------          | ------ | ----------  | --- | ---     | ---     | ----      | ---
//        Q             | Q->select D from T|    -   |    -        |  -  |  -      |  -      |  -        |  -
//        D                |     -          |    -   |D->distinct P| D->P|  -      |  -      | D->P      |  -
//        P                |     -          |    -   |    -        |P->* |  -      |  -      | P->A      |  -
//        A                |     -          |    -   |    -        |  -  |  -      |  -      |A->id A3 A1|  -
//        A1               |     -          |A1->ε   |    -        |  -  |A1->, A  |  -      |  -        |  -
//        A2               |     -          |    -   |    -        |  -  |  -      |  -      |A2->id A3  |  -
//        A3               |     -          |A3->ε   |    -        |  -  |A3->ε    |A3->. id |  -        |  -
//        T                |     -          |    -   |    -        |  -  |  -      |  -      |T->id T3 T1|  -
//        T1               |     -          |T1->ε   |    -        |  -  |T1->, T  |  -      |  -        |  -
//        T2               |     -          |    -   |    -        |  -  |  -      |  -      |T2->id     |  -
//        T3               |     -          |T3->ε   |    -        |  -  |T3->ε    |  -      |T3->id     |  -
public class Parser {

    static Stack<Token> PilaToken = new Stack<>();
    private final List<Token> tokens;
    // Terminales
    private final Token identificador = new Token(TipoToken.IDENTIFICADOR, "");
    private final Token select = new Token(TipoToken.SELECT, "select");
    private final Token from = new Token(TipoToken.FROM, "from");
    private final Token distinct = new Token(TipoToken.DISTINCT, "distinct");
    private final Token coma = new Token(TipoToken.COMA, ",");
    private final Token punto = new Token(TipoToken.PUNTO, ".");
    private final Token asterisco = new Token(TipoToken.ASTERISCO, "*");
    private final Token finCadena = new Token(TipoToken.EOF, "");
    // No terminales
    private final Token Q = new Token(TipoToken.Q, "Q");
    private final Token D = new Token(TipoToken.D, "D");
    private final Token P = new Token(TipoToken.P, "P");
    private final Token A = new Token(TipoToken.A, "A");
    private final Token A1 = new Token(TipoToken.A1, "A1");
    private final Token A2 = new Token(TipoToken.A2, "A2");
    private final Token A3 = new Token(TipoToken.A3, "A3");
    private final Token T = new Token(TipoToken.T, "T");
    private final Token T1 = new Token(TipoToken.T1, "T1");
    private final Token T2 = new Token(TipoToken.T2, "T2");
    private final Token T3 = new Token(TipoToken.T3, "T3");

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        i = 0;
        preanalisis = tokens.get(i);
        PilaToken.push(finCadena);
        PilaToken.push(Q);
        do {
            Token aux = PilaToken.peek();
            if (PilaToken.peek().equals(finCadena) && preanalisis.equals(finCadena)) {
                System.out.println("Consulta válida");
                break;
            } else if (PilaToken.peek().equals(preanalisis)) {
                coincidir(PilaToken.peek()); // Coincide con el token en la cima de la pila
            } else if (!PilaToken.peek().equals(preanalisis)) {
                ManejoNoTerminales(PilaToken.peek()); // Realiza el manejo de los no terminales
            } else {
                System.out.println(
                        "Error en la posición " + preanalisis.posicion + ". No se esperaba el token "
                                + preanalisis.tipo);
                hayErrores = true;
            }
            aux = PilaToken.peek();
        } while ((!hayErrores && PilaToken.peek() != finCadena));
        if (!hayErrores && !preanalisis.equals(finCadena)) {
            System.out.println(
                    "Error en la posición " + preanalisis.posicion + ". No se esperaba el token " + preanalisis.tipo);
        } else if (!hayErrores && preanalisis.equals(finCadena)) {
            System.out.println("Consulta válida");
        }
    }

    void ManejoNoTerminales(Token t) {
        PilaToken.pop(); // Sacar el símbolo no terminal de la pila

        if (t.equals(Q)) {
            if (preanalisis.equals(select)) {
                PilaToken.push(T);
                PilaToken.push(from);
                PilaToken.push(D);
                PilaToken.push(select);
            }
        } else if (t.equals(D)) {
            if (preanalisis.equals(distinct)) {
                PilaToken.push(P);
                PilaToken.push(distinct);
            } else if (preanalisis.equals(identificador)) {
                PilaToken.push(P);
            } else if (preanalisis.equals(asterisco)) {
                PilaToken.push(P);
            }
        } else if (t.equals(P)) {
            if (preanalisis.equals(asterisco)) {
                PilaToken.push(asterisco);
            } else if (preanalisis.equals(identificador)) {
                PilaToken.push(A);
            }
        } else if (t.equals(A)) {
            if (preanalisis.equals(identificador)) {
                PilaToken.push(A1);
                PilaToken.push(A3);
                PilaToken.push(identificador);
            }
        } else if (t.equals(A1)) {
            if (preanalisis.equals(coma)) {
                if(i+1 < tokens.size() && tokens.get(i+1).equals(identificador)) {
                    PilaToken.push(A);
                    PilaToken.push(coma);
                } else {
                    throw new IllegalStateException("Error en la posición " + preanalisis.posicion + ". Se esperaba un identificador después de la coma.");
                }
            } else if (preanalisis.equals(from)) {
                // Produce epsilon
            }
        } else if (t.equals(A2)) {
            if (preanalisis.equals(identificador)) {
                PilaToken.push(A3);
                PilaToken.push(identificador);
            }
        } else if (t.equals(A3)) {
            if (preanalisis.equals(punto)) {
                PilaToken.push(identificador);
                PilaToken.push(punto);
            } else if (preanalisis.equals(from) || preanalisis.equals(coma)) {
                // Produce epsilon
            }
        } else if (t.equals(T)) {
            if (preanalisis.equals(identificador)) {
                PilaToken.push(T1);
                PilaToken.push(T3);
                PilaToken.push(identificador);
            }
        } else if (t.equals(T1)) {
            if (preanalisis.equals(coma)) {
                PilaToken.push(T);
                PilaToken.push(coma);
            } else if (preanalisis.equals(finCadena) || preanalisis.equals(from)) {
                // No hacemos nada para producir epsilon
            }
        } else if (t.equals(T2)) {
            if (preanalisis.equals(identificador)) {
                PilaToken.push(T3);
                PilaToken.push(identificador);
            }
        } else if (t.equals(T3)) {
            if (preanalisis.equals(identificador)) {
                PilaToken.push(identificador);
            } else if (preanalisis.equals(finCadena) || preanalisis.equals(coma)) {
                // Produce epsilon
            }
        } else {
            throw new IllegalStateException("Token no terminal inesperado: " + t.lexema);
        }
    }

    public void coincidir(Token t) {
        if (t.equals(PilaToken.peek())) {
            PilaToken.pop();
            i++;
            preanalisis = tokens.get(i);
        } else {
            System.out.println(
                    "Error en la posición " + preanalisis.posicion + ". Se esperaba el token " + t.tipo);
            hayErrores = true;
        }
    }
}