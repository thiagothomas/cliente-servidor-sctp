import javax.json.Json;
import javax.json.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

public class Peer {

    public static Servidor servidor;
    public static String nomePeer;
    private static BufferedReader leitor;

    public static String end;

    public static void main(String[] args) throws IOException {
        leitor = Utils.criarBufferedReader(System.in);

        System.out.print("> Digite um nome para este peer: ");
        nomePeer = leitor.readLine();

        System.out.print("> Digite o endereco deste peer: ");
        String[] endereco = leitor.readLine().split(":");
        end = endereco[0] + ":" + endereco[0];

        servidor = new Servidor(endereco[0], Integer.parseInt(endereco[1]));
        servidor.start();

        atualizarPeersConectados();
    }

    private static void atualizarPeersConectados() throws IOException {
        System.out.println("> Digite, separados por espaço, os peers a que deseja ouvir (e.g. localhost:1111): ");
        System.out.println("  Para pular esta etapa, digite 'p'");

        String resposta = leitor.readLine();
        List<String> peers = Arrays.asList(resposta.split("\\s"));

        if (!resposta.equals("p")) {
            for (String peer : peers) {
                conectarSockets(peer);
            }
        }

        iniciarComunicacao();
    }

    private static void conectarSockets(String peer) throws IOException {
        List<String> endereco = Arrays.asList(peer.split(":"));

        try {
            new Cliente(endereco.get(0), Integer.parseInt(endereco.get(1))).start();
        } catch (Exception e) {
            System.out.println("* Entrada Inválida, pulando para o próximo passo.");
        }
    }

    private static void iniciarComunicacao() {
        try {
            System.out.println("> Você pode começar a comunicação agora. Digite 's' para sair ou 'a' para adicionar peers");
            while (true) {
                System.out.print("> ");
                String comando = leitor.readLine();
                if (comando.equals("s")) {
                    break;
                } else if (comando.equals("a")) {
                    atualizarPeersConectados();
                } else {
                    enviarComando(comando);
                }
            }

            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void enviarComando(String comando) {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, servidor.endereco)
                            .add(Utils.NOME_PEER, nomePeer)
                            .add(Utils.COMANDO, comando)
                            .build()
            );
        }

        servidor.enviarComando(string.toString());
    }

}
