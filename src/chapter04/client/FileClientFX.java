package chapter04.client;

import chapter02.FileDialogClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class FileClientFX extends Application {
    private TextField ipInput = new TextField();
    private TextField portInput = new TextField();

    private Button btnConnect = new Button("连接");
    private Button btnSend = new Button("发送");
    private Button btnDownload = new Button("下载");
    private Button btnExit = new Button("退出");


    private TextField sendInput = new TextField();
    private TextArea infoDisplay = new TextArea();

    private String ip = "";
    private String port = "";

    private FileDialogClient fileDialogClient;

    private Thread receiveThread;

    private class ReceiveHandler implements Runnable{ // 对话线程
        @Override
        public void run() {
            String msg = null;
            while((msg = fileDialogClient.receive()) != null) {
                String msgTemp = msg;
                Platform.runLater(() -> {
                    infoDisplay.appendText(msgTemp + '\n');
                });
            }
            Platform.runLater(() -> {
                infoDisplay.appendText("对话已关闭！\n");
            });
        }
    }

    private void openConnect() {
      ip = ipInput.getText().trim();
      port = portInput.getText().trim();
      try {
          fileDialogClient = new FileDialogClient(ip, port);
          receiveThread = new Thread(new ReceiveHandler(),
                  "my-receiveThread");
          receiveThread.start();

    //          String firstMsg = tcpClient.receive();
    //          infoDisplay.appendText(firstMsg + "\n");
          btnSend.setDisable(false);
          btnDownload.setDisable(false);
          btnConnect.setDisable(true);
      } catch (Exception e) {
          infoDisplay.appendText("服务器连接失败！" + e.getMessage() + "\n");
      }
    }
    private void exit() {
        if(fileDialogClient != null){
            fileDialogClient.send("bye"); //向服务器发送关闭连接的约定信息
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
            fileDialogClient.close();
        }
        System.exit(0);
    }
    private void sendMessage() {
      String sendMsg = sendInput.getText();
    //      infoDisplay.appendText(msg + "\n");
      sendInput.clear();
      if(fileDialogClient != null) {
          fileDialogClient.send(sendMsg);
          infoDisplay.appendText("客户端发送：" + sendMsg + "\n");
          if(sendMsg.equals("bye")) {
              btnSend.setDisable(true);
              btnDownload.setDisable(true);
              btnConnect.setDisable(false);
          }
    //          String receiveMsg = tcpClient.receive();
    //          infoDisplay.appendText(receiveMsg + "\n");
      }
    };
    private void downloadFile() {
        if(sendInput.getText().equals("")) {
            return ; // 没有输入文件名则返回
        }

        String fName = sendInput.getText().trim();
        sendInput.clear();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(fName);
        File saveFile = fileChooser.showSaveDialog(null);
        if(saveFile == null) {
            return ; //用户放弃操作则返回
        }

        try {
            new FileDataClient(ip, "2020").getFile(saveFile);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(saveFile.getName() + " 下载完毕！");
            alert.showAndWait();
            //通知服务器已经完成了下载动作，不发送的话，服务器不能提供有效反馈信息
            fileDialogClient.send("客户端开启下载");
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

  public FileClientFX() throws IOException {
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

    btnDownload.setOnAction(event -> {
     downloadFile();
    });

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
    btnBox.getChildren().addAll(btnSend, btnDownload,btnExit);
    btnSend.setDisable(true);
    btnDownload.setDisable(true);
    infoDisplay.selectionProperty().addListener(((observable, oldValue, newValue) -> {
        // 鼠标拖动复制
        if(oldValue == newValue) return;
        if(!infoDisplay.getSelectedText().equals("")) {
            sendInput.setText(infoDisplay.getSelectedText());
        }
    }));
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

