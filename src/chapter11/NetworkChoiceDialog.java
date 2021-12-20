package chapter11;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jpcap.JpcapCaptor;
import jpcap.JpcapSender;
import jpcap.NetworkInterface;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class NetworkChoiceDialog {
    private JpcapSender jpcapSender;//用于返回给主窗体
    private TextField tfKeyword;
    //网卡列表
    private NetworkInterface[] devices = JpcapCaptor.getDeviceList();
    private Stage stage = new Stage();//对话框窗体

    //parentStage表示抓包主程序(PacketCaptureFX)的stage，传值可通过这种构造方法参数的方式
    public NetworkChoiceDialog(Stage parentStage) {
        //设置该对话框的父窗体为调用者的那个窗体
        stage.initOwner(parentStage);
        //设置为模态窗体，即不关闭就不能切换焦点
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setResizable(false);
        stage.setTitle("选择网卡并设置参数");

        //窗体主容器
        VBox vBox = new VBox();
        //省略

        //网卡选择列表，使用组合下拉框控件
        ComboBox<String> cob = new ComboBox<>();
        cob.setMaxWidth(800);
        for (int i = 0; i < devices.length; i++) {
            cob.getItems().add( i + " : " + devices[i].description);
        }
        //默认选择第一项
        cob.getSelectionModel().selectFirst();



        //底部确定和取消按钮
        HBox hBoxBottom = new HBox();
        //省略

        Button btnConfirm = new Button("确定");
        Button btnCancel = new Button("取消");
        hBoxBottom.getChildren().addAll(btnConfirm,btnCancel);

        //将各组件添加到主容器
        vBox.getChildren().addAll(new Label("请选择网卡："),cob, new Separator(),hBoxBottom);

        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        //stage.show(); //不要显示对话框，由主窗体调用显示

        //**************事件响应部分***************************

        //确定按钮的动作事件
        btnConfirm.setOnAction(event -> {
            try {
                int index = cob.getSelectionModel().getSelectedIndex();
                //选择的网卡接口
                NetworkInterface networkInterface = devices[index];
                jpcapSender = JpcapSender.openDevice(networkInterface);
                stage.hide();
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR,e.getMessage()).showAndWait();
            }
        });

        //取消按钮的动作事件
        btnCancel.setOnAction(event -> {
            stage.hide();
        });
    }

    //主程序调用，获取设置了参数的JpcapCaptor对象
    public JpcapSender getSender() {
        return jpcapSender;
    }

    // 主程序调用，获取关键字
    public String getKeyData() { return tfKeyword.getText(); }
    //该方法由主程序调用，阻塞式显示本对话框界面
    public void showAndWait() {
        stage.showAndWait();
    }
}
