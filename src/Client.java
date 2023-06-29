import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    String current = null;

    public void noverlUser(String user) {
        meun();
    }
    public void any(){

    }
    Client() throws IOException {
        Socket socket = new Socket("127.0.0.1", 10001);
        System.out.println("socket ready");
        int flag = 0;

        BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter os = new PrintWriter(socket.getOutputStream());
        Scanner input = new Scanner(System.in);

        System.out.println("已连接到服务器");
        String id = null;
            while (flag == 0) {
                System.out.print("请输入账号:");
                id = input.nextLine();
                os.println(id);
                os.flush();

                if (!id.equals("anonymous")) {
                    System.out.print("请输入密码:");
                    String password = input.nextLine();
                    os.println(password);
                    os.flush();
                    String answer;
                    if ((answer = is.readLine()) != null) {
                        System.out.println("服务器端的响应: " + answer);
                    }

                    if (Objects.equals(answer, "登陆成功")) {
                        flag = 1;
                    } else {
                        System.out.println("登陆不成功，请重新输入");
                    }
                } else {
                    String answer = is.readLine();
                    System.out.println("服务器端的响应: " + answer);
                    flag = 1;
                }
            }
            // 处理已登录用户的逻辑
            if (!id.equals("anonymous")) {
                meun();
                while (true) {

                    String request = input.nextLine();
                    if(request.startsWith("put")){
                        String filename = request.substring(4);
                        File que = new File("local");
                        File[] files = que.listFiles();
                        boolean ishave = false;
                        assert files != null;
                        for(File file :files){
                            if(file.getName().equals(filename))
                            {
                                ishave=true;
                                break;
                            }
                        }
                        if(!ishave) {
                            System.out.println("文件不存在");
                            continue;
                        }
                    }else if(request.startsWith("get")){
                        String filename = request.substring(4);
                        os.println("dir");//发送命令
                        os.flush();
                        String str = is.readLine();
                        ArrayList<String> LIST = new ArrayList<>();
                        while (!str.equals("结束")) {
                            LIST.add(str.substring(4));
                            str = is.readLine();

                        }
                        boolean ishave = false;
                        for (String st : LIST){
                            if (st.equals(filename)) {
                                ishave = true;
                                break;
                            }
                        }
                        if(!ishave) {
                            System.out.println("文件不存在");
                            continue;
                        }
                    }
                    os.println(request);//发送命令
                    os.flush();

                    if(request.equals("dir")||request.startsWith("cd"))
                    {
                        String str = is.readLine();
                        while (!str.equals("结束")) {
                            System.out.println(str);
                            str = is.readLine();
                        }
                    }
                    else if(request.startsWith("get"))
                    {
                        get(request,is);
                        System.out.println("下载完成");
                    }
                    else if(request.startsWith("put")) {
                        put(request, is, os);
                    }else if(request.equals("exit"))
                    {
                        break;
                    }else{
                        System.out.println("无效的命令");
                    }

                }
            } else {
                meun();
                while (true) {

                    String request = input.nextLine();
                    if(request.startsWith("put")){
                        System.out.println("没有权限");
                        continue;
                    }
                    os.println(request);//发送命令
                    os.flush();

                    if(request.equals("dir")||request.startsWith("cd"))
                    {
                        String str = is.readLine();
                        while (!str.equals("结束")) {
                            System.out.println(str);
                            str = is.readLine();
                        }
                    }
                    else if(request.startsWith("get"))
                    {
                        get(request,is);
                    }
                   else if(request.equals("exit"))
                    {
                        break;
                    }else{
                        System.out.println("无效的命令");
                    }

                }
            }


        socket.close();
    }

    public void meun() {
        System.out.println("1:dir");
        System.out.println("2:cd");
        System.out.println("3:put");
        System.out.println("4:get");
        System.out.println("5:exit");
    }

    public void put(String req,BufferedReader is,PrintWriter os ) throws IOException {
        String filename = req.substring(4);
        BufferedInputStream in = new BufferedInputStream(new FileInputStream("local/"+filename));
        int b=0;
        System.out.println("开始上传");
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
        System.out.println("上传成功");

    }
    public void  get(String req,BufferedReader is) throws IOException {
        String filename = req.substring(4);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("local/"+filename));
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
        System.out.println("下载完成");
    }

}
