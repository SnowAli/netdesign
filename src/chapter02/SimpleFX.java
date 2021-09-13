package chapter02;

import chapter01.TextFileIO;
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

import java.io.IOException;
import java.time.LocalDateTime;

public class SimpleFX extends Application {
  private TextField ipInput = new TextField();
  private TextField portInput = new TextField();

  private Button btnConnect = new Button("连接");
  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("加载");
  private Button btnSave = new Button("保存");

  private TextField sendInput = new TextField();
  private TextArea infoDisplay = new TextArea();

  private TCPClient tcpClient;

  public SimpleFX() throws IOException {
  }

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    VBox vBox = new VBox();
    vBox.setSpacing(10); // spacing of the components
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
     String ip = ipInput.getText().trim();
     String port = portInput.getText().trim();
     try {
       tcpClient = new TCPClient(ip, port);
       String firstMsg = tcpClient.receive();
       infoDisplay.appendText(firstMsg + "\n");
     } catch (Exception e) {
       infoDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
     }
   });

    btnSend.setOnAction(event -> {
      String sendMsg = sendInput.getText();
//      infoDisplay.appendText(msg + "\n");
//      sendInput.clear();
      if(tcpClient != null) {
        tcpClient.send(sendMsg);
        infoDisplay.appendText("客户端发送：" + sendMsg + "\n");
        String receiveMsg = tcpClient.receive();
        infoDisplay.appendText(receiveMsg + "\n");
      }
    });

    TextFileIO textFileIO = new TextFileIO();
    btnSave.setOnAction(event -> {
      textFileIO.append(
              LocalDateTime.now().withNano(0) + " " + infoDisplay.getText()
      );
    });
    btnOpen.setOnAction(event -> {
      String msg = textFileIO.load();
      if(msg != null){
        infoDisplay.clear();
        infoDisplay.setText(msg);
      }
    });

    sendInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          String msg = sendInput.getText();
          if(event.isShiftDown()) { // isShiftDown方法判断shift键是按下
            infoDisplay.appendText("echo:" + msg + "\n");
          } else {
            infoDisplay.appendText(msg + "\n");
          }
          sendInput.clear();
        };
      }
    });

    btnExit.setOnAction(event -> {
      if(tcpClient != null) {
        tcpClient.send("bye");
        tcpClient.close();
      }
      System.exit(0);
    });

    HBox btnBox = new HBox();
    btnBox.setSpacing(10);
    btnBox.setPadding(new Insets(10,20,10,20));
    btnBox.setAlignment(Pos.CENTER_RIGHT);
    btnBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);

    mainPane.setCenter(vBox);
    mainPane.setBottom(btnBox);


    Scene scene = new Scene(mainPane, 700, 400);

    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

