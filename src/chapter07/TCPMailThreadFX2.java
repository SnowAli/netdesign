package chapter07;

import chapter02.FileDialogClient;
import javafx.application.Application;
import javafx.application.Platform;
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

import java.io.IOException;


public class TCPMailThreadFX2 extends Application {
    private TextField tfSmtpAddr = new TextField("smtp.qq.com"); // 邮件服务地址
    private TextField tfSmtpPort = new TextField("25"); // 邮件服务器端口

    private TextField tASenderAddr = new TextField("958259016@qq.com"); // 邮件发送者地址
    private TextField tfReceiverAddr = new TextField("luo_hai_jiao@163.com"); // 邮件接收者地址

    private TextField tfTitle = new TextField("20191003149给老师的一封邮件"); // 邮件标题


    private TextArea tASend = new TextArea("20191003149&曾繁浩");
    private TextArea tAReceive = new TextArea();

    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TCPMailClient tcpMailClient;

    Thread receiveThread;
    Thread sendThread;
    private void sendMessage() {
        String smtpAddr = tfSmtpAddr.getText().trim();
        String smtpPort = tfSmtpPort.getText().trim();
        System.out.println(smtpAddr);
        System.out.println(smtpPort);
        try {
            tcpMailClient = new TCPMailClient(smtpAddr, smtpPort);
            receiveThread = new Thread(() -> {
                String msg = null;
                while((msg = tcpMailClient.receive()) != null) {
                    String msgTemp = msg;
                    tAReceive.appendText(msgTemp + "\n");
                    Platform.runLater(() -> {
                        System.out.println(msgTemp + "\n");
                    });
                }
                Platform.runLater(() -> {
//                    infoDisplay.appendText("对话已关闭!\n");
                });
            });
            receiveThread.start();

            sendThread = new Thread(() -> {
                tcpMailClient.send("HELO friend");

                tcpMailClient.send("AUTH LOGIN");

                String username = "ccqstark@qq.com";
//                String authCode = "henwfxouvsmzbajh";
                String authCode = "qzktnxinqufpjjbg";
                String msg = BASE64.encode(username);
                tcpMailClient.send(msg);

                msg = BASE64.encode(authCode);
                tcpMailClient.send(msg);


                msg = "MAIL FROM:<" + tASenderAddr.getText().trim() + ">";
                tcpMailClient.send(msg);

                msg = "RCPT TO:<" + tfReceiverAddr.getText().trim() + ">";
                tcpMailClient.send(msg);

                msg = "DATA";
                tcpMailClient.send(msg);



                msg = "From:" + tASenderAddr.getText().trim();
                tcpMailClient.send(msg);

                msg = "Subject:" + tfTitle.getText().trim();
                tcpMailClient.send(msg);

                msg = "To:" + tfReceiverAddr.getText().trim();
                tcpMailClient.send(msg);

                tcpMailClient.send("\n");

                msg = tASend.getText().trim();
                tcpMailClient.send(msg);

                tcpMailClient.send(".");
                tcpMailClient.send("QUIT");
            });
            sendThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
   };

  public TCPMailThreadFX2() throws IOException {
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
    socketBox.getChildren().addAll(new Label("邮件服务器地址："), tfSmtpAddr, new Label("邮件服务器端口："), tfSmtpPort);
    socketBox.setAlignment(Pos.CENTER);

    HBox emailBox = new HBox();
    emailBox.setSpacing(10);
    emailBox.setPadding(new Insets(10,20,10,20));
    emailBox.getChildren().addAll(new Label("邮件发送者地址："), tASenderAddr, new Label("邮件接收者地址："),  tfReceiverAddr);
    emailBox.setAlignment(Pos.CENTER);

    HBox titleBox = new HBox();
    titleBox.setSpacing(10);
    titleBox.setPadding(new Insets(10,20,10,20));
    tfTitle.setPrefWidth(480);
    titleBox.getChildren().addAll(new Label("邮件标题："),tfTitle);
    titleBox.setAlignment(Pos.CENTER);

    HBox infoBox = new HBox();
    VBox sendBox = new VBox();
    VBox receiveBox = new VBox();
    sendBox.getChildren().addAll(new Label("邮件正文："),tASend);
    receiveBox.getChildren().addAll(new Label("服务器反馈程序："),tAReceive);
    infoBox.setSpacing(10);
    infoBox.setPadding(new Insets(10,20,10,20));
    infoBox.getChildren().addAll(sendBox, receiveBox);
    infoBox.setAlignment(Pos.CENTER);

    vBox.getChildren().addAll(socketBox, emailBox, titleBox, infoBox);

    VBox.setVgrow(tASend, Priority.ALWAYS);


    btnSend.setOnAction(event -> {
        sendMessage();
    });


    tASend.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
         sendMessage();
        };
      }
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
//       exit();
    });

  }
}

