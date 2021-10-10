package chapter04.client;

import java.io.*;
import java.net.Socket;

/**
 * Description: 客户端数据传输进程，主要功能是：
            连接服务器数据端口、发送文件名、
            保存下载的文件，文件传输完成后关闭数据连接。
 * Param:
 * return:
 * Author: zengfanhao
 * Date: 2021/9/26
 */
public class FileDataClient {
    private Socket dataSocket;
    public FileDataClient(String ip, String port) throws IOException {
        dataSocket = new Socket(ip, Integer.parseInt(port));
    }

    public void getFile(File saveFile) throws IOException {

        if (dataSocket != null) { // dataSocket是Socket类型的成员变量


            FileOutputStream fileOut = new FileOutputStream(saveFile);//新建本地空文件
            byte[] buf = new byte[1024]; // 用来缓存接收的字节数据
            //网络字节输入流
            InputStream socketIn = dataSocket.getInputStream();
            //网络字节输出流
            OutputStream socketOut = dataSocket.getOutputStream();

            //(2)向服务器发送请求的文件名，字符串读写功能
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(socketOut, "utf-8"), true);
            pw.println(saveFile.getName());

            //(3)接收服务器的数据文件，字节读写功能
            int size = 0;
            while ((size = socketIn.read(buf)) != -1) {//读一块到缓存，读取结束返回-1
                fileOut.write(buf, 0, size); //写一块到文件
            }
            fileOut.flush();//关闭前将缓存的数据全部推出
            //文件传输完毕，关闭流
            fileOut.close();
            if (dataSocket != null) {
                dataSocket.close();
            }
        } else {
            System.err.println("连接ftp数据服务器失败");
        }
    }

}
