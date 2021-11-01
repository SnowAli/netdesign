package chapter08;


import chapter05.TCPClient;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;


public class URLClientFX extends Application {


    private Button btnExit = new Button("退出");
    private Button btnSend = new Button("发送");

    private TextField sendInput = new TextField("http://www.baidu.com");
    private TextArea infoDisplay = new TextArea();

    private Thread receiveThread;

  public URLClientFX() throws IOException {
  }

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(10, 20, 10, 20));

    vBox.getChildren().addAll(new Label("信息显示区："),
      infoDisplay, new Label("信息输入区："), sendInput);

    VBox.setVgrow(infoDisplay, Priority.ALWAYS);

    btnSend.setOnAction(event -> {
        infoDisplay.clear();
        String address = sendInput.getText().trim();
        try {
            URL url = new URL(address);
            System.out.printf("连接%s成功！", address);
            // 获得url的字节流输入
            InputStream in = url.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));

            String msg = null;
            while((msg = br.readLine()) != null) {
                String msgTemp = msg;
                System.out.println(msgTemp);
                Platform.runLater(() -> {
                    infoDisplay.appendText(msgTemp + "\n");
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
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

    });

  }
}

