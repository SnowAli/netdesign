package chapter11;

import jpcap.JpcapSender;
import jpcap.packet.EthernetPacket;
import jpcap.packet.IPPacket;
import jpcap.packet.TCPPacket;

import java.net.InetAddress;

public class PacketSender {
 public static void sendTCPPacket(JpcapSender sender, int srcPort,
                                     int dstPort, String srcHost, String dstHost, String data,
                                     String srcMAC, String dstMAC,
                                     boolean syn, boolean ack, boolean rst, boolean fin) {
        try {
            //构造一个TCP包
            TCPPacket tcp = new TCPPacket    (8000,80,56,78,false,false,
                    false,false,true,false,true,true,200,10);
            //设置IPv4报头参数，ip地址可以伪造
            tcp.setIPv4Parameter(0,false,false,false,0,false,
                    false,false,0,1010101,100,
                    IPPacket.IPPROTO_TCP, InetAddress.getByName(srcHost),
                    InetAddress.getByName (dstHost));

            //填充TCP包中的数据
            tcp.data = data.getBytes("utf-8");//字节数组型的填充数据
            //构造相应的MAC帧
            EthernetPacket ether = new EthernetPacket();
            ether.frametype = EthernetPacket.ETHERTYPE_IP;
            tcp.datalink = ether;
//            ether.src_mac = new byte[]{(byte)00,(byte)27,(byte)185,(byte)177,(byte)74,(byte)70};

//根据实际情况设置目的MAC地址， arp -a 可以查看相关的MAC地址
//            ether.dst_mac = new byte[]{(byte)00,(byte)17,(byte)93,(byte)157,(byte)128,(byte)00};

            ether.src_mac = convertMacFormat(srcMAC);
            ether.dst_mac = convertMacFormat(dstMAC);
            if(ether.src_mac == null || ether.dst_mac==null)
                throw new Exception("MAC地址输入错误");

            sender.sendPacket(tcp);
            System.out.println("发包成功！");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            //重新抛出异常，调用者可以捕获处理
            throw new RuntimeException(e);
        }
    }

    public static byte[] convertMacFormat(String MAC) {
     if (! (MAC.contains("-")) || MAC.contains(":")){
        return null;
     }
     String[] strArr = new String[]{};
     strArr = MAC.split("-");
     byte[] bytes = new byte[6];
     for (int i = 0; i < strArr.length; i++) {
       bytes[i] =  (byte)Integer.parseInt(strArr[i],16);
     }
     return bytes;

//（1）首先判断参数MAC中是否包含"-"或":";
//(2)通过split方法将MAC切分为字符串数组;
//(3)定义一个6字节的字节数组，循环将十六进制形式的字符串转为字节，赋值给字节数组;
//提示：利用Integer.parseInt(字符串,进制)将16进制表示的字符串转为字节，
//例如：(byte)Integer.parseInt("0F",16)
    }

}
