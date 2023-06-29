import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class Myrun implements Runnable{

     Socket socket;
     HashMap<String, String> DATA ;
     String root;
     String current;
    public Myrun(Socket socket,HashMap<String, String> DATA , String root) {
        this.socket = socket;
        this.DATA=DATA;
        this.root=root;

    }

    @Override
    public void run() {
        try {
            Server server = new Server(this.socket,this.DATA,this.root,this.current);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
