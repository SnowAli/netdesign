package chapter05;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatServer {
    private int port = 5005;
    private ServerSocket serverSocket;
    private static Map<Integer, String> loginMembers = new ConcurrentHashMap<>();

    public ChatServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动监听在" + port + "端口");
    }

    private PrintWriter getWriter (Socket socket) throws IOException {
        OutputStream socketOut = socket.getOutputStream();
        return new PrintWriter(
                new OutputStreamWriter(socketOut, "utf-8"), true);
    }

    private BufferedReader getReader(Socket socket) throws IOException {
        InputStream socketIn = socket.getInputStream();
        return new BufferedReader(
                new InputStreamReader(socketIn, "utf-8"));
    }

    private class Handler implements Runnable{
        private Socket socket;
        public Handler(Socket socket) {
            this.socket = socket;
        }
        @Override
        public void run() {
            System.out.println("New connection accepted： " + socket.getInetAddress().getHostAddress());
//            members.add(socket);
            try {
                BufferedReader br = getReader(socket);
                PrintWriter pw = getWriter(socket);
                pw.println("From 服务器：请输入<用户名&登录>进行登录！\r 输入'1'查看当前在线用户");

                String msg = null;
                while ((msg = br.readLine()) != null) {
                    String[] loginInfo = msg.split("&");
                    if(loginInfo.length == 2) {
                        loginMembers.put(loginMembers.size(), loginInfo[0]);
                    }

                    if(msg.equals("1")){
                        for (String val : loginMembers.values()) {
                            pw.println(val);
                        }
                    }
                    if (msg.trim().equalsIgnoreCase("bye")) {
                        pw.println("From 服务器：服务器已断开连接，结束服务！");
                        System.out.println("客户端离开");
                        break;
                    }
//                    sendToAllMembers(msg, socket.getInetAddress().getHostAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(socket != null) {
                        socket.close();
//                        members.remove(socket);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void Service() {
        while(true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                ExecutorService executorService  = Executors.newCachedThreadPool();
                executorService.execute(new Handler(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException{
        new ChatServer().Service();
    }
}
