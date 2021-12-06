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
        Long ipNum = Long.parseLong(ip);
        return ipNum;
    }

    @Override
    public String longToIp(long ipNum) throws RemoteException {//x
        String ip = Long.toString(ipNum);
//        String ip =  String.valueOf(ipNum);
        return ip;
    }

    @Override
    public byte[] macStringToBytes(String macStr) throws RemoteException {
        String[] strArr;
        strArr = macStr.split("-");
        byte[] bytes = new byte[6];
        for (int i = 0; i < strArr.length; i++) {
            bytes[i] = (byte)Integer.parseInt(strArr[i], 16);
        }
        return bytes;
    }

    @Override
    public String bytesToMACString(byte[] macBytes) throws RemoteException {//x
        String macStr = "";
        for (byte macByte : macBytes) {
            String subStr = Integer.toHexString(macByte);
            macStr += subStr + "-";
        }
        macStr = macStr.substring(0, macStr.lastIndexOf("-"));
        return macStr;
    }
}