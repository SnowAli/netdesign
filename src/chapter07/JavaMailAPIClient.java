package chapter07;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * description:
 * author :zfh
 * date: 2021/10/25
 */
public class JavaMailAPIClient {
    protected Session session;
    protected Store store;
    protected Transport transport;

    //定义接收邮件服务器
    private String receiveHost = "pop.qq.com";
    //定义发送邮件服务器
    private String sendHost = "smtp.qq.com";
    //接收邮件协议
    private String receiveProtocol = "pop3";

    public void init() throws Exception {
        // 设置邮箱属性
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.store.protocol", receiveProtocol);
        props.put("mail.imap.class", "com.sun.mail.imap.IMAPStore");
        props.put("mail.smtp.class", "com.sun.mail.smtp.SMTPTransport");
        props.put("mail.smtp.host", sendHost);//设置发送邮件服务器

        // 创建session对象
        session = Session.getDefaultInstance(props);
        // 创建store对象
        store = session.getStore(receiveProtocol);
        // 连接到接收邮件服务器
        store.connect(receiveHost, "958259016@qq.com","henwfxouvsmzbajh");
    }

    public void close() throws Exception {
        store.close();
    }

    public void sendMail(String fromAddr, String toAddr) throws MessagingException{

        MimeMessage message = new MimeMessage(session);
        // 设置发送方地址
        message.setFrom(new InternetAddress(fromAddr));
        // 设置接收方地址
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toAddr));
        // 设置邮件主题
        message.setSubject("你好", "UTF-8");
        // 设置邮件正文
        message.setText("开心的一天！", "UTF-8");
        System.out.println(message);
        //发送（授权码不需要base64编码）
        Transport.send(message, fromAddr, "henwfxouvsmzbajh");

    }

    /**
     * 接收邮件
     * @throws Exception
     */
    public void receiveMail() throws Exception {
        //inbox邮件夹是邮件账号的保留邮件夹(IMAP不允许用户删除该邮件夹)，
        // 邮件服务器把所有接收到的新邮件都存在该邮件夹中
        browseMessagesFromFolder("inbox");
    }

    public void browseMessagesFromFolder(String folderName) throws Exception {
        Folder folder = store.getFolder(folderName);
        if (folder == null) {
            throw new Exception(folderName + "邮件夹不存在");
        }
        browseMessagesFromFolder(folder);
    }

    public void browseMessagesFromFolder(Folder folder) throws Exception {
        //Folder类代表邮件夹，邮件都放在邮件夹中
        folder.open(Folder.READ_ONLY);
        System.out.println("你的邮箱里有： " + folder.getMessageCount() + "封邮件");
        System.out.println("你的邮箱里有：  " + folder.getUnreadMessageCount() + "封未读邮件");

        //读邮件,Message类代表电子邮件。Message类提供了读取和设置邮件内容的方法
        for (int i = 1; i <= folder.getMessages().length; i++) {
            System.out.println("-------第" + i + "封邮件--------");
            System.out.print("其发送者是:");

            //Address类代表邮件地址，和Message 类一样，Address类也是个抽象类。
            // 常用的具体子类为javax.mail.internet.InternetAddress类
            InternetAddress[] addr = (InternetAddress[]) folder.getMessage(i).getFrom();

            //发送者地址
            System.out.println(addr[0].getAddress());
            System.out.print("发送的标题为：");
            String subject = folder.getMessage(i).getSubject();
            System.out.println(subject);
            System.out.print("发送日期为：");

            System.out.println(" 邮件内容为： ");
            System.out.println(folder.getMessage(i).getContent().toString());
        }

        folder.close(false);//关闭邮件夹，但是不要删除邮件夹中标记为"deleted"的邮件
    }

    public static void main(String[] args) throws Exception{
        JavaMailAPIClient mailClient = new JavaMailAPIClient();
        mailClient.init();
        mailClient.sendMail("958259016@qq.com","z_fhao@qq.com");

    mailClient.receiveMail();
        mailClient.close();
    }
}
