import com.sun.nio.sctp.SctpChannel;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;

public class Cliente extends Thread {

    public final SctpChannel sc;
    private final ByteBuffer buf = ByteBuffer.allocateDirect(50000);
    private Charset charset = StandardCharsets.ISO_8859_1;
    private CharsetDecoder decoder = charset.newDecoder();

    Cliente(String hostname, Integer port) throws IOException {
        InetSocketAddress socket = new InetSocketAddress(InetAddress.getByName(hostname), port);
        this.sc = SctpChannel.open(socket, 0, 0);
    }

    @Override
    public void run() {
        boolean aux = true;
        while (aux) {
            try {
                sc.receive(buf, System.out, null);
                buf.flip();
                String mensagem = decoder.decode(buf).toString();
                buf.clear();
                processarMensagem(mensagem);
            } catch (Exception e) {
                aux = false;
                super.interrupt();
            }
        }
    }

    private void processarMensagem(String mensagem) throws IOException {
        JsonObject mensagemJson = Json.createReader(new StringReader(mensagem)).readObject();
        StringBuilder stringBuilder = new StringBuilder();

        if (mensagemJson.containsKey(Utils.COMANDO)) {
            System.out.println("======================= EXECUTANDO COMANDO '" + mensagemJson.getString(Utils.COMANDO) + "' =======================");
            try {
                Process resultado = Runtime.getRuntime().exec(mensagemJson.getString(Utils.COMANDO));
                BufferedReader reader = Utils.criarBufferedReader(resultado.getInputStream());

                String linha;
                while ((linha = reader.readLine()) != null) {
                    stringBuilder.append(linha).append("\n");
                    System.out.println(linha);
                }

                resultado.waitFor();
                System.out.println("exit: " + resultado.exitValue());
                resultado.destroy();
                System.out.println("- FIM EXECUCAO -");
                enviarMensagemDeVolta(stringBuilder.toString(), mensagemJson.getString(Utils.SERVIDOR));
            } catch (IOException | InterruptedException e) {
                System.out.println("Erro na execução do comando");
                enviarMensagemDeVolta("Erro na exeução do comando", mensagemJson.getString(Utils.SERVIDOR));
            }

        } else if (mensagemJson.containsKey(Utils.RESPOSTA)
                && Peer.servidor.endereco.equals(mensagemJson.getString(Utils.SERVIDOR))) {
            System.out.println("========================= RETORNO DE [" + mensagemJson.getString(Utils.NOME_PEER) + "] =========================");
            System.out.println(mensagemJson.getString(Utils.RESPOSTA));
            System.out.println("- FIM RETORNO -");
        }
        System.out.print("> ");

    }

    private void enviarMensagemDeVolta(String resposta, String endereco) throws IOException {
        StringWriter string = new StringWriter();

        try (JsonWriter json = Json.createWriter(string)) {
            json.writeObject(
                    Json.createObjectBuilder()
                            .add(Utils.SERVIDOR, endereco)
                            .add(Utils.NOME_PEER, Peer.nomePeer)
                            .add(Utils.RESPOSTA, resposta)
                            .build()
            );
        }

        Peer.servidor.enviarComando(string.toString());
    }

}
