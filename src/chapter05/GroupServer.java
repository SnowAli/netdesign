package chapter05;

import chapter02.FileDialogClient;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

    public static class MultiCastFX extends Application {
        private TextField ipInput = new TextField();
        private TextField portInput = new TextField();


        private Button btnExit = new Button("退出");
        private Button btnSend = new Button("发送");

        private TextField sendInput = new TextField();
        private TextArea infoDisplay = new TextArea();

        private FileDialogClient tcpClient;

        Thread receiveThread;


        private void exit() {
            if(tcpClient != null){
                tcpClient.send("bye"); //向服务器发送关闭连接的约定信息
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                tcpClient.close();
            }
            System.exit(0);
        }

        private void sendMessage() {
          String sendMsg = sendInput.getText();
    //      infoDisplay.appendText(msg + "\n");
          sendInput.clear();
          if(tcpClient != null) {
              tcpClient.send(sendMsg);
              infoDisplay.appendText("客户端发送：" + sendMsg + "\n");
              if(sendMsg.equals("bye")) {
                  btnSend.setDisable(true);

              }
    //          String receiveMsg = tcpClient.receive();
    //          infoDisplay.appendText(receiveMsg + "\n");
          }
      };

      public MultiCastFX() throws IOException {
      }

      @Override
      public void start(Stage primaryStage) {
        BorderPane mainPane = new BorderPane();

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10, 20, 10, 20));


        HBox socketBox = new HBox();
        socketBox.setSpacing(10);
        socketBox.setPadding(new Insets(10,20,10,20));
        socketBox.setAlignment(Pos.CENTER_RIGHT);
        socketBox.getChildren().addAll(new Label("IP地址："), ipInput, new Label("端口："), portInput);
        socketBox.setAlignment(Pos.CENTER);

        vBox.getChildren().addAll(socketBox, new Label("信息显示区："),
          infoDisplay, new Label("在线用户"),  new Label("信息输入区："), sendInput);

        VBox.setVgrow(infoDisplay, Priority.ALWAYS);


        btnSend.setDisable(true);
        btnSend.setOnAction(event -> {
            sendMessage();
        });


        sendInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
          @Override
          public void handle(KeyEvent event) {
            if(event.getCode() == KeyCode.ENTER) {
             sendMessage();
            };
          }
        });

        btnExit.setOnAction(event -> {
            exit();
        });

        HBox btnBox = new HBox();
        btnBox.setSpacing(10);
        btnBox.setPadding(new Insets(10,20,10,20));
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().addAll(btnSend, btnExit);

        mainPane.setCenter(vBox);
        mainPane.setBottom(btnBox);


        Scene scene = new Scene(mainPane, 700, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
           exit();
        });

      }
    }
}
