package chapter06;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Date;


public class Multicast {
    DatagramSocket datagramSocket;


    public Multicast() throws IOException {
        datagramSocket = new DatagramSocket(6006);
        System.out.println("服务器启动监听在 " + 6006+ " 端口");
    }

    public void Service() {
        while (true) {
            try {
                byte[] buf = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
                System.out.println(receivePacket.getLength());
                datagramSocket.receive(receivePacket);
                byte receiveData[] = receivePacket.getData();// 接收的数据
                InetAddress address = receivePacket.getAddress();// 接收的地址
                System.out.println("接收的文本:" + new String(receiveData));
                System.out.println("接收的ip地址:" + address.toString());
                System.out.println("接收的端口:" + receivePacket.getPort());


                byte[] sendData = ("20191003149&曾繁浩&" + new Date().toString() + "&" + new String(receiveData)).getBytes();;
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                        receivePacket.getSocketAddress());
                datagramSocket.send(sendPacket);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
//                    if(datagramSocket != null)
//                        datagramSocket.close();

            }
        }
    }

    public static void main(String[] args) throws IOException{
        new Multicast().Service();
    }
}
