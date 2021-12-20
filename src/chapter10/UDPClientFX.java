package chapter10;


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


public class UDPClientFX extends Application {
    private TextField ipInput = new TextField("202.116.195.71");
    private TextField portInput = new TextField("6868");

    private Button btnInit = new Button("初始化");
    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextField sendInput = new TextField();
    private TextArea infoDisplay = new TextArea();

    private UDPClient udpClient;

    Thread receiveThread;

    private void openInit() {
      String ip = ipInput.getText().trim();
      String port = portInput.getText().trim();
      try {
          udpClient = new UDPClient(ip, port);
          receiveThread = new Thread(() -> {
              String msg = null;
              while((msg = udpClient.receive()) != null) {
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
          btnSend.setDisable(false);
          btnInit.setDisable(true);
      } catch (Exception e) {
          e.printStackTrace();
          infoDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
      }
    }

    private void exit() {
        if(udpClient != null){
            udpClient.send("bye"); //向服务器发送关闭连接的约定信息
            try {
                Thread.sleep(100);
                btnSend.setDisable(true);
                btnInit.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void sendMessage() {
      String sendMsg = sendInput.getText();
      StringBuffer stringBuffer = new StringBuffer();
      stringBuffer.append("20191003149&曾繁浩\n");
      stringBuffer.append("1636945525:220071 /10.173.217.24->/202.116.192.5 protocol(6) priority(0)  hop(64)  offset(0) ident(50263) TCP 7420 > 443 seq(3551629480) win(64240)  S\n");
      stringBuffer.append("1636945525:226564 /202.116.192.5->/10.173.217.24 protocol(6) priority(0)  hop(59)  offset(0) ident(0) TCP 443 > 7420 seq(4012026018) win(29200) ack 3551629481  S\n");
      stringBuffer.append("1636945525:226617 /10.173.217.24->/202.116.192.5 protocol(6) priority(0)  hop(64)  offset(0) ident(50264) TCP 7420 > 443 seq(3551629481) win(256) ack 4012026019");
      sendInput.clear();
      if(udpClient != null) {
//          udpClient.send(sendMsg);
          udpClient.send(stringBuffer.toString());
          infoDisplay.appendText("客户端发送：" + sendMsg + "\n");
          if(sendMsg.equals("bye")) {
              btnSend.setDisable(true);
              btnInit.setDisable(false);
          }
      }
  };

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
    socketBox.getChildren().addAll(new Label("IP地址："), ipInput, new Label("端口："), portInput, btnInit);
    socketBox.setAlignment(Pos.CENTER);

    vBox.getChildren().addAll(socketBox, new Label("信息显示区："),
      infoDisplay, new Label("信息输入区："), sendInput);

    VBox.setVgrow(infoDisplay, Priority.ALWAYS);

    btnInit.setOnAction(event -> {
        openInit();
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

