package chapter03;

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


public class TCPClientThreadFX extends Application {
    private TextField ipInput = new TextField();
    private TextField portInput = new TextField();

    private Button btnConnect = new Button("连接");
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextField sendInput = new TextField();
    private TextArea infoDisplay = new TextArea();

    private FileDialogClient tcpClient;

    Thread receiveThread;

    private void openConnect() {
      String ip = ipInput.getText().trim();
      String port = portInput.getText().trim();
      try {
          tcpClient = new FileDialogClient(ip, port);
          receiveThread = new Thread(() -> {
              String msg = null;
              while((msg = tcpClient.receive()) != null) {
                  String msgTemp = msg;
                  Platform.runLater(() -> {
                      infoDisplay.appendText(msgTemp + "\n");
                  });
              }
              Platform.runLater(() -> {
                  infoDisplay.appendText("对话已关闭!\n");
              });
          });
          receiveThread.start();

    //          String firstMsg = tcpClient.receive();
    //          infoDisplay.appendText(firstMsg + "\n");
          btnSend.setDisable(false);
          btnConnect.setDisable(true);
      } catch (Exception e) {
          infoDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
      }
    }
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
              btnConnect.setDisable(false);
          }
//          String receiveMsg = tcpClient.receive();
//          infoDisplay.appendText(receiveMsg + "\n");
      }
  };

  public TCPClientThreadFX() throws IOException {
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
    socketBox.getChildren().addAll(new Label("IP地址："), ipInput, new Label("端口："), portInput, btnConnect);
    socketBox.setAlignment(Pos.CENTER);

    vBox.getChildren().addAll(socketBox, new Label("信息显示区："),
      infoDisplay, new Label("信息输入区："), sendInput);

    VBox.setVgrow(infoDisplay, Priority.ALWAYS);

    btnConnect.setOnAction(event -> {
        openConnect();

    });

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

