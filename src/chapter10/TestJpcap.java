package chapter10;

import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

import java.io.IOException;

public class TestJpcap {
    public static void main(String[] args) throws IOException {
        NetworkInterface[] devices = JpcapCaptor.getDeviceList();
//        for (int i = 0; i < devices.length; i++) {
//            //打印 GUID information and description
//            System.out.println(i+": "+devices[i].name + " " +
//                    devices[i].description);
//            //打印 MAC address，各段用":"隔开
//            String mac = "";
//            for (byte b : devices[i].mac_address) {
//                //mac地址6段，每段是8位，而int转换的十六进制是4个字节，所以和0xff相与，这样就只保留低 8位
//                mac = mac + Integer.toHexString(b & 0xff) + ":";
//            }
//            System.out.println("MAC address:" + mac.substring(0, mac.length()   - 1));
//
////print out its IP address, subnet mask and broadcast address
//            for (NetworkInterfaceAddress addr : devices[i].addresses) {
//                System.out.println(" address:"+addr.address + " " + addr.subnet + " "+ addr.broadcast );
//            }
//        }



        JpcapCaptor jpcapCaptor = JpcapCaptor.openDevice(devices[4], 1514, true, 20);

//        jpcapCaptor.loopPacket(-1, new PacketHandler());






    }
}
