package chapter11;

import chapter10.ConfigDialog;
import chapter11.NetworkChoiceDialog;
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
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;


public class SendPacketFX extends Application {

    private TextField tfSrcPort  = new TextField("8000");
    private TextField tfDstPort  = new TextField("8008");

    private CheckBox cbSYN = new CheckBox("SYN");
    private CheckBox cbACK = new CheckBox("ACK");
    private CheckBox cbRST = new CheckBox("RST");
    private CheckBox cbFIN = new CheckBox("FIN");

    private TextField tfSrcHost  = new TextField("10.173.41.133");
    private TextField tfDstHost  = new TextField("202.116.195.71");
    private TextField tfSrcMAC  = new TextField("22-47-f0-01-93-50");
    private TextField tfDstMAC  = new TextField("00-11-5d-9c-94-00");
    private TextField tfData  = new TextField("20191003149&曾繁浩");


    private Button btnSendTCPPacket = new Button("发送TPC包");
    private Button btnSet = new Button("选择网卡");
    private Button btnExit = new Button("退出");


    private TextArea infoDisplay = new TextArea();


    private NetworkChoiceDialog networkChoiceDialog;
    private JpcapSender sender;




  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();


    HBox portBox = new HBox();
    portBox.setSpacing(10);
    portBox.setPadding(new Insets(10,20,10,20));
    portBox.setAlignment(Pos.CENTER);
    portBox.getChildren().addAll(new Label("源端口"),
            tfSrcPort,
            new Label("目的端口"),
            tfDstPort);
    mainPane.setTop(portBox);

    HBox checkBox = new HBox();
    checkBox.setSpacing(10);
    checkBox.setPadding(new Insets(10,20,10,20));
    checkBox.setAlignment(Pos.CENTER);
    checkBox.getChildren().addAll(new Label("TCP标识位："),
            cbSYN, cbACK, cbRST, cbFIN);

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(10, 20, 10, 20));
    vBox.getChildren().addAll(
            checkBox,
            new Label("源主机地址"),
            tfSrcHost,
            new Label("目的主机地址"),
            tfDstHost,
            new Label("源MAC地址"),
            tfSrcMAC,
            new Label("目的MAC地址"),
            tfDstMAC,
            new Label("发送的数据"),
            tfData
      );

    mainPane.setCenter(vBox);


    HBox btnBox = new HBox();
    btnBox.setSpacing(10);
    btnBox.setPadding(new Insets(10,20,10,20));
    btnBox.setAlignment(Pos.CENTER);
    btnBox.getChildren().addAll(btnSendTCPPacket, btnSet, btnExit);
    mainPane.setBottom(btnBox);


    Scene scene = new Scene(mainPane, 700, 400);

      primaryStage.setScene(scene);
      primaryStage.setTitle("发送自构包");
      primaryStage.setWidth(500);
      networkChoiceDialog = new NetworkChoiceDialog(primaryStage);
      networkChoiceDialog.showAndWait();
      sender = networkChoiceDialog.getSender();
      primaryStage.show();


      primaryStage.setOnCloseRequest(event -> {

    });

      btnSendTCPPacket.setOnAction(event -> {
          try {
              int srcPort = Integer.parseInt(tfSrcPort.getText().trim());
              int dstPort = Integer.parseInt((tfDstPort.getText().trim()));
              String srcHost = tfSrcHost.getText().trim();
              String dstHost = tfDstHost.getText().trim();
              String srcMAC = tfSrcMAC.getText().trim();
              String dstMAC = tfDstMAC.getText().trim();
              String data = tfData.getText();
              //调用发包方法
              PacketSender.sendTCPPacket(sender, srcPort, dstPort, srcHost,
                      dstHost, data, srcMAC, dstMAC,cbSYN.isSelected(),
                      cbACK.isSelected(),cbRST.isSelected(),cbFIN.isSelected());

              new Alert(Alert.AlertType.INFORMATION, "已发送！").showAndWait();
          } catch (Exception e) {
              new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
          }
      });

      btnSet.setOnAction(event -> {
        if(networkChoiceDialog == null) {
            networkChoiceDialog = new NetworkChoiceDialog(primaryStage);
        }

        //阻塞式显示，等待设置窗体完成设置
        networkChoiceDialog.showAndWait();

        //获取设置后的JpcapCaptor对象实例
        sender = networkChoiceDialog.getSender();
    });
    btnExit.setOnAction(event -> {
        System.exit(0);
    });
    }
}

