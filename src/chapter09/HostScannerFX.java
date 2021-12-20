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

import java.net.InetAddress;

public class HostScannerFX extends Application {

    private TextField startIP = new TextField();
    private TextField endIP = new TextField();

    private TextArea result = new TextArea();

    private Button Scanner = new Button("端口扫描");

    public static void main(String[] args) { launch(); }


    public long ipToLong(String ip){
        String[] ipArray = ip.split("\\.");
        long num = 0;
        for(int i=0;i<ipArray.length;i++){
            long valueOfSection = Long.parseLong(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }


    public String longToIp(long i){
        return ((i >> 24) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                (i & 0xFF);
    }


    public void scanner() {
        String start = startIP.getText().trim();
        String end = endIP.getText().trim();

        long startLong = ipToLong(start);
        long endLong = ipToLong(end);

        for(long i=startLong;i<=endLong;i++){
            String host = longToIp(i);
            try {
                InetAddress addr = InetAddress.getByName(host);
                boolean status = addr.isReachable(500);
                if(status){
                    result.appendText(host + " is reachable." + "\n");
                }else{
                    result.appendText(host + " is not reachable." + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.appendText(start + "is not reachable.");
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
        hBox.getChildren().addAll(new Label("起始地址"),startIP,new Label("结束地址"),endIP,Scanner);

        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10,20,10,20));
        vBox.getChildren().addAll(new Label("扫描结果"),result,hBox);
        mainPane.setCenter(vBox);




        Scene scene = new Scene(mainPane,700,400);
        stage.setScene(scene);
        stage.show();

        Scanner.setOnAction(event -> {
            scanner();
        });

    }
}
