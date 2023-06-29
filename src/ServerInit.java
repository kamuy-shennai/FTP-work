import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerInit {
    public static HashMap<String, String> DATA = new HashMap<>();
    public static String root;
    public  static String data = "src/data.txt";
    private final ServerSocket ss = new ServerSocket(10011);

    public ServerInit() throws IOException {
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fin = new FileInputStream(ServerInit.data);
        InputStreamReader reader = new InputStreamReader(fin);
        BufferedReader buffReader = new BufferedReader(reader);
        String strTmp = "";
        root = buffReader.readLine();
        while ((strTmp = buffReader.readLine()) != null) {
            int index = strTmp.indexOf(' ');
            String stu = strTmp.substring(0, index);
            String pass = strTmp.substring(index + 1);
            DATA.put(stu, pass);
        }
        ServerSocket server = new ServerSocket(10001);
        
        while (true)
        {
            Socket socket = server.accept();
            new Thread(new Myrun(socket,DATA,root)).start();
        }



    }
}
