import com.sun.nio.sctp.SctpServerChannel;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Servidor extends Thread {

    public final SctpServerChannel server;
    public final String endereco;
    public final Set<Conexao> conexoes = new HashSet<>();


    Servidor(String hostname, Integer port) throws IOException {
        this.endereco = hostname + ":" + port;
        this.server = SctpServerChannel.open();
        InetSocketAddress socket = new InetSocketAddress(InetAddress.getByName(hostname), port);
        this.server.bind(socket);
    }

    @Override
    public void run() {
        try {
            while (true) {
                Conexao conexao = new Conexao(this.server.accept());
                this.conexoes.add(conexao);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarComando(String comando) {
        try {
            conexoes.forEach(c -> c.enviarComando(comando));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
