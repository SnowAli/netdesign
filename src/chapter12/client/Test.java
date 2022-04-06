package chapter12.client;

import java.rmi.RemoteException;

/**
 * description:
 * author :zfh
 * date: 2021/11/29
 */
public class Test {
    public static long ipToLong(String ip)  {
        String[] ipArray = ip.split("\\.");
        long num = 0;
        for(int i=0;i<ipArray.length;i++){
            long valueOfSection = Long.parseLong(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }


    public static String longToIp(long ipNum) {//x
        return ((ipNum >> 24) & 0xFF) + "." +
                ((ipNum >> 16) & 0xFF) + "." +
                ((ipNum >> 8) & 0xFF) + "." +
                (ipNum & 0xFF);
    }


    public static byte[] macStringToBytes(String macStr)  {
        String[] MacArray = macStr.split("[-]");
        byte[] ByteMac = new byte[6];
        int x = 0;
        for (String key: MacArray){
            ByteMac[x] = (byte)Integer.parseInt(MacArray[x],16);
            x = x+1;
        }
        return ByteMac;
    }


    public static String bytesToMACString(byte[] macBytes) {//x
        StringBuilder builder = new StringBuilder();
        for (byte macByte : macBytes) {
            builder.append(':').append(Integer.toHexString(0xFF & macByte));
        }
        return builder.substring(1);
    }

    public static void main(String[] args) throws RemoteException {
        byte[] bytes = new byte[0];

        bytes = macStringToBytes("52-54-10-1f-44-18");
        String strarr = "";
        strarr = bytesToMACString(bytes);

        System.out.println(longToIp(ipToLong("192.168.1.123")) );
    }


}
