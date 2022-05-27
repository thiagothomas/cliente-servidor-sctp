import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.SctpChannel;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

public class Conexao {

    private final SctpChannel sc;
    ByteBuffer buf = ByteBuffer.allocateDirect(50000);
    CharBuffer cbuf = CharBuffer.allocate(50000);
    Charset charset = StandardCharsets.ISO_8859_1;
    CharsetEncoder encoder = charset.newEncoder();

    Conexao(SctpChannel sc) {
        this.sc = sc;
    }

    public void enviarComando(String message) {
        try {
            cbuf.put(message).flip();
            encoder.encode(cbuf, buf, true);
            buf.flip();

            MessageInfo messageInfo = MessageInfo.createOutgoing(null, 0);
            sc.send(buf, messageInfo);
            cbuf.clear();
            buf.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
