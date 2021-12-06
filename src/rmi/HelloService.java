package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;

/**
 * description:
 * author :zfh
 * date: 2021/11/29
 */
public interface HelloService extends Remote {
    public String echo(String msg) throws RemoteException;
    public Date getTime() throws RemoteException;
    public ArrayList<Integer> sort(ArrayList<Integer> list) throws RemoteException;
}