import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utils {

    private Utils() {
    }

    public static final String COMANDO = "comando";
    public static final String SERVIDOR = "servidor";
    public static final String RESPOSTA = "resposta";
    public static final String NOME_PEER = "nomepeer";

    public static BufferedReader criarBufferedReader(InputStream inputStream) {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new BufferedReader(inputStreamReader);
    }

}
