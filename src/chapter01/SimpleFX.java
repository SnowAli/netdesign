package chapter01;

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

import java.time.LocalDateTime;

public class SimpleFX extends Application {
  private Button btnExit = new Button("退出");
  private Button btnSend = new Button("发送");
  private Button btnOpen = new Button("加载");
  private Button btnSave = new Button("保存");

  private TextField tfSend = new TextField();
  private TextArea taDisplay = new TextArea();

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    VBox vBox = new VBox();
    vBox.setSpacing(10); // spacing of the components
    vBox.setPadding(new Insets(10, 20, 10, 20));

    vBox.getChildren().addAll(new Label("信息显示区："),
      taDisplay, new Label("信息输入区："), tfSend);

    VBox.setVgrow(taDisplay, Priority.ALWAYS);


    btnSend.setOnAction(event -> {
      String msg = tfSend.getText();
      taDisplay.appendText(msg + "\n");
      tfSend.clear();
    });

    TextFileIO textFileIO = new TextFileIO();

    btnSave.setOnAction(event -> {
      textFileIO.append(
              LocalDateTime.now().withNano(0) + " " + taDisplay.getText()
      );
    });

    btnOpen.setOnAction(event -> {
      String msg = textFileIO.load();
      if(msg != null){
        taDisplay.clear();
        taDisplay.setText(msg);
      }
    });

    btnExit.setOnAction(event -> {System.exit(0);});

    tfSend.setOnKeyPressed(new EventHandler<KeyEvent>() {
      @Override
      public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.ENTER) {
          String msg = tfSend.getText();
          if(event.isShiftDown()) { // isShiftDown方法判断shift键是按下
            taDisplay.appendText("echo:" + msg + "\n");
          } else {
            taDisplay.appendText(msg + "\n");
          }
          tfSend.clear();
        };
      }
    });



    HBox hBox = new HBox();
    hBox.setSpacing(10);
    hBox.setPadding(new Insets(10,20,10,20));
    hBox.setAlignment(Pos.CENTER_RIGHT);
    hBox.getChildren().addAll(btnSend, btnSave, btnOpen, btnExit);

    mainPane.setCenter(vBox);
    mainPane.setBottom(hBox);


    Scene scene = new Scene(mainPane, 700, 400);

    primaryStage.setScene(scene);
    primaryStage.show();
  }
}

