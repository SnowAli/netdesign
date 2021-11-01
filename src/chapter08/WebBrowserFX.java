package chapter08;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;


public class WebBrowserFX extends Application {


    private Button btnRefresh = new Button("刷新");
    private Button btnForward = new Button("前进");
    private Button btnBack = new Button("回退");
    private Button btnIndex = new Button("首页");
    private Button btnGo = new Button("跳转");


    private TextField urlInput = new TextField("https://www.gdufs.edu.cn"); // 输入url

    private WebView webView = new WebView();

    private Thread receiveThread;

  public WebBrowserFX() throws IOException {
  }

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(10, 20, 10, 20));


    HBox btnBox = new HBox();
    btnBox.setSpacing(10);
    btnBox.setPadding(new Insets(10,20,10,20));

    btnBox.getChildren().addAll(btnRefresh, btnForward, btnBack, btnIndex, urlInput, btnGo);
    HBox.setHgrow(urlInput, Priority.ALWAYS);

    vBox.getChildren().addAll(btnBox, webView);
    VBox.setVgrow(webView, Priority.ALWAYS);

    btnGo.setOnAction(event -> {
        String url = urlInput.getText().trim();

        if(!url.matches("^(http|https)://([\\w.]+\\/?)\\S*")) {
            System.out.println("不合法的url");
            return;
        }
        System.out.println(url);


        WebEngine webEngine = webView.getEngine();
        webEngine.load(url);
    });




    mainPane.setCenter(vBox);
    mainPane.setTop(btnBox);


    Scene scene = new Scene(mainPane, 700, 400);

    primaryStage.setScene(scene);
    primaryStage.show();

    primaryStage.setOnCloseRequest(event -> {});

  }
}

