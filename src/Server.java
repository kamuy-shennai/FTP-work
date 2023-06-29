import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;


public class Server {
    HashMap<String, String> DATA ;
    String root;
    Socket socket;

     String current;

    Server(  Socket socket,HashMap<String, String> DATA,String root,String currrent) throws IOException {
           this.DATA= DATA;
           this.root=root;
           this.socket=socket;
           this.current=currrent;
           asa();
    }

    public void asa() throws IOException {

             BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter os = new PrintWriter(socket.getOutputStream());

            int flag = 0;
            String name = null;

            while (flag == 0) {
                name = is.readLine();
                if (!name.equals("anonymous")) {
                    String passid = is.readLine();

                    System.out.println(name);
                    System.out.println(passid);
                    if (DATA.containsKey(name)) {
                        if (!DATA.get(name).equals(passid)) {
                            os.println("密码错误");
                            os.flush();
                            Logger.logLoginFailure(String.valueOf(socket.getInetAddress()),name);
                        } else {
                            os.println("登陆成功");
                            os.flush();
                            flag = 1;
                            Logger.logLoginSuccess(String.valueOf(socket.getInetAddress()),name);
                        }
                    } else {
                        os.println("账号不存在");
                        os.flush();
                        Logger.logLoginFailure(String.valueOf(socket.getInetAddress()),name);
                    }
                } else {
                    os.println("登录成功");
                    os.flush();
                    flag = 1;
                    Logger.logLoginSuccess(String.valueOf(socket.getInetAddress()),name);
                }
            }
            current=root;
            // 在这里继续处理已登录用户的逻辑
            if (!name.equals("anonymous")) {
                // 处理已登录用户的逻辑
                while (true) {
                    String request = is.readLine();
                    a(request, os, socket,is);
                    Logger.logAction(String.valueOf(socket.getInetAddress()),name,request);
                    if(request.equals("exit")) break;

                }
            } else {
                // 处理匿名用户的逻辑
                while (true) {
                    String request = is.readLine();
                    a(request, os, socket,is);
                    if(request.equals("exit")) break;
                }
            }
            socket.close();


    }
    public void a(String request, PrintWriter os,  Socket socket,BufferedReader is) throws IOException {

            if (request.equals("dir")) {
                dir(os);
            } else if (request.startsWith("cd")) {
                String FILE = request.substring(3);
                cd(FILE, os);
            } else if (request.startsWith("get")) {
                String FILE = request.substring(4);
                get(FILE, os);
            } else if (request.startsWith("put")) {
                String FILE = request.substring(4);
                put(FILE, is);
            }

            //处理exit
    }

    private void get(String file, PrintWriter os) throws IOException {

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(current+"/"+file));
        int b=0;
        while (true){
            b=in.read();
            if (b != -1) {
                os.print(b);
                os.print(" ");
            }else {
                break;
            }
        }
        os.println();
        os.flush();
        in.close();
    }

    public void dir(PrintWriter os)
    {

        File directory = new File(current);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files.length >0) {
                for (File file : files) {
                    if (file.isFile()) {
                        System.out.println("文件: " + file.getName());
                        //发送给客户端
                        os.println("文件: " + file.getName());
                        os.flush();
                    } else if (file.isDirectory()) {
                        //发送给客户端
                        String dname = "目录: " + file.getName();
                        System.out.println(dname);
                        os.println(dname);
                        os.flush();
                    }
                }
            } else {
                System.out.println("目录为空或无法访问。");
                os.println("目录为空");
                os.flush();
            }
            os.println("结束");
            os.flush();
        } else {
            System.out.println("指定的路径不是有效的目录。");
        }


    }
    public void cd(String parm,PrintWriter os){
        if(parm.equals("..")){
            if(current.equals(root)){
                os.println("没有上级目录");
                os.flush();
                os.println("结束");
                os.flush();
            }else {
                int end = 0;
                for(int i=current.length()-1;i>0;i--)
                {
                    if(current.charAt(i)=='/') {
                        end = i;
                        break;
                    }
                }
                current = (String) current.subSequence(0,end);
                os.println("当前路径"+current);
                os.flush();
                os.println("结束");
                os.flush();
                return;
            }
        }
        else {
            File directory = new File(current);
            File[] files = directory.listFiles();
            assert files != null;
            for(File file :files){
                if(file.getName().equals(parm))
                {
                    current = current+"/"+parm;
                    os.println("当前路径"+current);
                    os.flush();
                    os.println("结束");
                    os.flush();
                    return;
                }
            }
            os.println("无效的目录");
            os.flush();
            os.println("结束");
            os.flush();
            return;
        }

    }
    public void put(String parm, BufferedReader is) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(current+"/"+parm));
        String bit = String.valueOf(is.readLine());
        int j = 0;
        for(int i =0;i<bit.length();i++)
        {
            if(bit.charAt(i)==' '){
                String sub = bit.substring(j,i);
                int a = Integer.parseInt(sub);
                bos.write((byte) a);
                if(i+1<bit.length())
                    j=i+1;
            }
        }

        bos.close();
    }

}
