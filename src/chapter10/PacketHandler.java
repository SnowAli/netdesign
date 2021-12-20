package chapter10;//package chapter10;
//
//import javafx.application.Platform;
//import javafx.scene.control.TextArea;
//import jpcap.PacketReceiver;
//import jpcap.packet.Packet;
//
//class PacketHandler implements PacketReceiver {
//    @Override
//    public void receivePacket(Packet packet) {
//        System.out.println(packet); //输出抓取的包的原始信息
//
//
//        Platform.runLater(() -> {
//            System.out.println(packet);
//        });
//    }
//}
