package chapter05;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GroupServer {
    private int port = 7777; //服务器监听端口
    private ServerSocket serverSocket; //定义服务器套接字
    private static CopyOnWriteArraySet<Socket> members = new CopyOnWriteArraySet<>(); // 聊天室成员,CopyOnWriteArraySet是线程安全的

    public GroupServer() throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("服务器启动监听在 " + port + " 端口");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
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
            members.add(socket); // 聊天室成员添加
            try {
                BufferedReader br = getReader(socket);
                PrintWriter pw = getWriter(socket);


                pw.println("From 服务器：欢迎使用本服务！");

                String msg = null;

                while ((msg = br.readLine()) != null) {
                    if (msg.trim().equalsIgnoreCase("bye")) {
                        pw.println("From 服务器：服务器已断开连接，结束服务！");
                        System.out.println("客户端离开");
                        break;
                    }
                    sendToAllMembers(msg, socket.getInetAddress().getHostAddress());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(socket != null) {
                        socket.close();
                        members.remove(socket);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendToAllMembers(String msg, String hostAddress) throws IOException {
        PrintWriter pw;
        OutputStream out;
        for (Socket tempSocket : members) { //members是什么类型的变量?
            out = tempSocket.getOutputStream();
            pw = new PrintWriter(
                    new OutputStreamWriter(out, "utf-8"), true);
            pw.println(hostAddress + " 发言：" + msg );
        }
    }

    public void Service() {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                ExecutorService executorService = Executors.newCachedThreadPool();
                executorService.execute(new Handler(socket));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) throws IOException{
        new GroupServer().Service();
    }
}
