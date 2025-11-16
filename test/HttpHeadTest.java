
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.miktim.http.HttpHead;

public class HttpHeadTest {

    static void log(Object o) {
        System.out.println(o);
    }
    static void logErr(Object o) {
        System.err.println(o);
        System.exit(1);
    }
    
    public static void main(String[] args) throws IOException {
        
        String body = "Hello!";
        String rq = "GET /chat HTTP/1.1\r\n"
                + "Host: server.example.com\r\n"
                + "Upgrade: websocket\r\n"
                + "Connection: Upgrade\r\n"
                + "Sec-WebSocket-Key: dGhlIHNhbXBsZSBub25jZQ==\r\n"
                + "Origin: http://example.com\r\n"
                + "Sec-WebSocket-Protocol: chat, superchat\r\n"
                + "Sec-WebSocket-Version: 13\r\n"
                + "Sec-WebSocket-Extensions:\r\n"
                + "     permessage-deflate;\r\n"
                + "     client_max_window_bits; server_max_window_bits=10,\r\n"
                + "\t   permessage-deflate;\r\n"
                + "\t   client_max_window_bits\r\n"
                + "\r\n"
                + body; // it has nothing to do with the WebSocket request
        InputStream is = new ByteArrayInputStream(rq.getBytes());
        HttpHead rqHead = (new HttpHead()).read(is);
        log(rqHead);
        
        byte[] buf = new byte[body.getBytes().length];
        is.read(buf);
        if(!(new String(buf)).equals(body)){
            logErr("HttpHead.read() FAILED!");
        }
        
        HttpHead head = new HttpHead(rqHead);
        log(head.getStartLine() + "\r\n");
        if(head.getStartLine().isEmpty() || rqHead.size() != head.size() || 
                !rqHead.toString().equals(head.toString())) {
            logErr("HttpHead clone FAILED!");
        }
        
        log(head);
        log(HttpHead.join(head.listValues("sec-webSocket-extensions"),",\r\n"));

        head = (new HttpHead())
                .setStartLine("GET /chat HTTP/1.1")
                .set("Host", "server.example.com")
                .set("Origin", "http://example.com")
                .set("Upgrade", "websocket")
                .add("Connection", "Upgrade")
                .add("Sec-WebSocket-Protocol", "chat, superchat")
                .add("Sec-WebSocket-Version", "13")
                .set("Sec-WebSocket-Key", "dGhlIHNhbXBsZSBub25jZQ==");

        rqHead.remove("sec-webSocket-extensions");
        if(!rqHead.toString().startsWith(head.toString())) {
            logErr("HttpHead set/add FAILED!");
        }
        log("\r\nTest OK");
    }
}
