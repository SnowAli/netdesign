package chapter09;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class PortScannerFX extends Application {

    private TextField startPort = new TextField();
    private TextField endPort = new TextField();
    private TextField ip = new TextField();

    private TextArea result = new TextArea();

    private Button Scanner = new Button("扫描");

    public static void main(String[] args) { launch(); }


    public void scanner(){
        String host = ip.getText().trim();
        int start = Integer.parseInt(startPort.getText().trim());
        int end = Integer.parseInt(endPort.getText().trim());

        for(int i=start;i<=end;i++){
            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, i),300);
                socket.close();
                result.appendText("端口 " + i + " is open\n");
            } catch (IOException e) {
                result.appendText("端口 " + i + " is not open\n");
            }
        }
    }








    @Override
    public void start(Stage stage) throws Exception {
        BorderPane mainPane = new BorderPane();


        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10,20,10,20));
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(new Label("目标主机IP"),ip,new Label("起始端口号"),startPort,new Label("结束端口号"),endPort);
        mainPane.setCenter(hBox);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10,20,10,20));
        vBox.getChildren().addAll(new Label("扫描结果"),result);
        mainPane.setTop(vBox);

        HBox btnBox = new HBox();
        btnBox.setSpacing(10);
        btnBox.setPadding(new Insets(10,20,10,20));
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().addAll(Scanner);
        mainPane.setBottom(btnBox);




        Scene scene = new Scene(mainPane,700,400);
        stage.setScene(scene);
        stage.show();


        Scanner.setOnAction(event -> {
            scanner();
        });



    }
}
