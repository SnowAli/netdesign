package chapter10;

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
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.PacketReceiver;
import jpcap.packet.Packet;

import java.io.IOException;


public class PacketCaptureFX extends Application {


    private Button btnStart = new Button("开始抓包");
    private Button btnStop = new Button("停止");
    private Button btnClear = new Button("清空");
    private Button btnSet = new Button("设置");
    private Button btnExit = new Button("退出");


    private TextArea infoDisplay = new TextArea();


    private ConfigDialog configDialog;
    private JpcapCaptor jpcapCaptor;

    private Thread  packetCaptureThread;
    private int index = 0;
    private class PacketHandler implements PacketReceiver {
        @Override
        public void receivePacket(Packet packet) {
            System.out.println(packet);
            Platform.runLater(() -> {
                index ++;
                infoDisplay.appendText(index + "行: " + packet.toString()+"\n\n");
                System.out.println(packet);
            });
        }
    }
    private void interrupt(String threadName) {
        ThreadGroup currentGroup =
                Thread.currentThread().getThreadGroup();
//获取当前线程的线程组及其子线程组中活动线程数量
        int noThreads = currentGroup.activeCount();
        Thread[] lstThreads = new Thread[noThreads];
        currentGroup.enumerate(lstThreads);//将活动线程复制到线程数组
//遍历这些活动线程，符合指定线程名的则声明关闭
        for (int i = 0; i < noThreads; i++) {
            if (lstThreads[i].getName().equals(threadName)) {
                lstThreads[i].interrupt();//声明线程关闭
            }
        }
    }

  public PacketCaptureFX() throws IOException {
  }

  @Override
  public void start(Stage primaryStage) {
    BorderPane mainPane = new BorderPane();

    VBox vBox = new VBox();
    vBox.setSpacing(10);
    vBox.setPadding(new Insets(10, 20, 10, 20));
    vBox.getChildren().addAll(new Label("信息显示区："),
      infoDisplay);
    VBox.setVgrow(infoDisplay, Priority.ALWAYS);


    HBox btnBox = new HBox();
    btnBox.setSpacing(10);
    btnBox.setPadding(new Insets(10,20,10,20));
    btnBox.setAlignment(Pos.CENTER);
    btnBox.getChildren().addAll(btnStart, btnStop, btnClear, btnSet, btnExit);
    mainPane.setCenter(vBox);
    mainPane.setBottom(btnBox);


    Scene scene = new Scene(mainPane, 700, 400);

    primaryStage.setScene(scene);
    primaryStage.show();

    primaryStage.setOnCloseRequest(event -> {

    });



    btnSet.setOnAction(event -> {
        if(configDialog == null) {
            configDialog = new ConfigDialog(primaryStage);
        }

        //阻塞式显示，等待设置窗体完成设置
        configDialog.showAndWait();

        //获取设置后的JpcapCaptor对象实例
        jpcapCaptor = configDialog.getJpcapCaptor();
    });

    btnStart.setOnAction(event -> {
        if(jpcapCaptor == null) {
            configDialog = new ConfigDialog(primaryStage);

            //阻塞式显示，等待设置窗体完成设置
            configDialog.showAndWait();

            //获取设置后的JpcapCaptor对象实例
            jpcapCaptor = configDialog.getJpcapCaptor();
            return ;
        }
        //停止还没结束的抓包线程
        if( packetCaptureThread != null) {
            // 先关闭这个线程
            interrupt("captureThread");
        }

        packetCaptureThread= new Thread(() -> {
            while(true) {
                //如果声明了本线程被中断，则退出循环
                if(Thread.currentThread().isInterrupted())
                    break;

                //每次抓一个包，交给内部类PacketHandler的实例处理
                jpcapCaptor.processPacket(1, new PacketHandler());
            }
        }, "captureThread");

        //降低线程优先级，避免抓包线程卡住资源
        packetCaptureThread.setPriority(Thread.MIN_PRIORITY);
        packetCaptureThread.start();

    });

    btnStop.setOnAction(event -> {
        interrupt("captureThread");
    });

    btnClear.setOnAction(event -> {
        index = 0;
        infoDisplay.clear();
    });

    btnExit.setOnAction(event -> {
        interrupt("captureThread");
        System.exit(0);
    });


    }
}

