package chapter12.server;

import rmi.RmiKitService;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * 远程服务接口的实现类
 */
public class RmiKitServiceImpl extends UnicastRemoteObject implements RmiKitService {
    private String name;

    public RmiKitServiceImpl() throws RemoteException {
    }
    public RmiKitServiceImpl(String name) throws RemoteException {
        this.name = name;
    }

    @Override
    public long ipToLong(String ip) throws RemoteException {//x
//        Long ipNum = Long.parseLong(ip);
//        return ipNum;
        String[] ipArray = ip.split("\\.");
        long num = 0;
        for(int i=0;i<ipArray.length;i++){
            long valueOfSection = Long.parseLong(ipArray[i]);
            num = (valueOfSection << 8 * (3 - i)) | num;
        }
        return num;
    }

    @Override
    public String longToIp(long ipNum) throws RemoteException {//x
//        String ip = Long.toString(ipNum);
//        String ip =  String.valueOf(ipNum);
//        return ip;
        return ((ipNum >> 24) & 0xFF) + "." +
                ((ipNum >> 16) & 0xFF) + "." +
                ((ipNum >> 8) & 0xFF) + "." +
                (ipNum & 0xFF);
    }

    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
//        String[] strArr;
//        strArr = macStr.split("-");
//        byte[] bytes = new byte[6];
//        for (int i = 0; i < strArr.length; i++) {
//            bytes[i] = (byte)Integer.parseInt(strArr[i], 16);
//        }
//        return bytes;
        String[] MacArray = macStr.split("[-]");
        byte[] ByteMac = new byte[6];
        int x = 0;
        for (String key: MacArray){
            ByteMac[x] = (byte)Integer.parseInt(MacArray[x],16);
            x = x+1;
        }
        return ByteMac;
    }

    @Override
    public String bytesToMACString(byte[] macBytes) throws RemoteException {//x
//        String macStr = "";
//        for (byte macByte : macBytes) {
//            String subStr = Integer.toHexString(macByte);
//            macStr += subStr + "-";
//        }
//        macStr = macStr.substring(0, macStr.lastIndexOf("-"));
//        return macStr;
        StringBuilder builder = new StringBuilder();
        for (byte macByte : macBytes) {
            builder.append(':').append(Integer.toHexString(0xFF & macByte));
        }
        return builder.substring(1);
    }
}