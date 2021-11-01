package chapter08;

/**
 * description:
 * author :zfh
 * date: 2021/10/31
 */
public interface Client {
    public void send(String msg);
    public void send(StringBuffer msg);
    public String receive();
    public void close();
}
