package chapter12.client;

import java.rmi.RemoteException;

/**
 * description:
 * author :zfh
 * date: 2021/11/29
 */
public class Test {
    public static String bytesToMACString(byte[] macBytes) throws RemoteException {
        String macStr = "";
        for (byte macByte : macBytes) {
            String subStr = Integer.toHexString(macByte);
            macStr += subStr + "-";
        }
        macStr = macStr.substring(0, macStr.lastIndexOf("-"));
        return macStr.toString();
    }
    public static byte[] macStringToBytes(String macStr) throws RemoteException {
        String[] strArr;
        strArr = macStr.split("-");
        byte[] bytes = new byte[6];
        for (int i = 0; i < strArr.length; i++) {
            System.out.println(strArr[i]);
            bytes[i] = (byte)Integer.parseInt(strArr[i], 16);
        }
        return bytes;
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[0];
        try {
            bytes = macStringToBytes("52-54-10-1f-44-18");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        String strarr = "";
        try {
            strarr = bytesToMACString(bytes);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println(strarr);
    }


}
