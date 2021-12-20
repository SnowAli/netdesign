package chapter14;

import com.mysql.jdbc.Driver;

import java.sql.*;
import java.util.ArrayList;

/**
 * description:
 * author :zfh
 * date: 2021/12/13
 */
public class DBOperate2 {
    public static void main(String[] args) throws Exception {
        //加载MySQL驱动器，其中com.mysql.jdbc.Driver就是由下载的mysql驱动包提供
        Class jdbcDriver =  Class.forName("com.mysql.jdbc.Driver");

        //注册MySQL驱动器
        java.sql.DriverManager.registerDriver((Driver)jdbcDriver.newInstance());

        //指定数据库所在位置，先用本地地址测试，访问本地的数据库
        String dbUrl = "jdbc:mysql://202.116.195.71:3306/mypeopledb?characterEncoding=utf8&useSSL=false";
        //指定用户名和密码
        String dbUser="student";
        String dbPwd="student";

        //创建数据库连接对象
        Connection con = java.sql.DriverManager.getConnection(dbUrl,dbUser,dbPwd);

        // 返回数据库的一些元信息
        DatabaseMetaData metaData = con.getMetaData();

        /*
        以下语句调用会返回一个ResultSet结果集，该结果集含4列，其中有一列含表名（该列名称为TABLE_NAME，可以通过rsTables.getString("TABLE_NAME")获得）
        可通过遍历rsTables得到包含的表名称
        */
        ResultSet rsTables = metaData.getTables(null, null, null, new String[]{"TABLE"});

        //用于保存表名的数组列表，供之后遍历访问
        ArrayList<String> tablesName = new ArrayList<>();
        System.out.println("该数据库中包含的表：");
        while (rsTables.next())
        {
            String tableName = rsTables.getString("TABLE_NAME");
            tablesName.add(tableName);
            System.out.println(tableName);
        }
        System.out.println("-----------------------------------");

        PreparedStatement stmt = null;
        ResultSet rs = null;

        for(String tableName:tablesName) {
            ArrayList<String> fieldsNames = new ArrayList<>(); //保存字段名
            String sql = "select * from " + tableName;
            stmt = con.prepareStatement(sql);
            rs = stmt.executeQuery();

            ResultSetMetaData fields = rs.getMetaData(); //会返回该表的字段信息
            int n = fields.getColumnCount(); // 字段数量
            for(int i=1;i<=n;i++)
            {
                //getColumnName可以获得字段名
                String fieldName = fields.getColumnName(i);
                fieldsNames.add(fieldName);
                System.out.print(fieldName + " ");
            }
            System.out.println();
            System.out.println(tableName + "数据：");
            //如果有必要，还可以循环遍历列表结果，获取有价值信息
            while (rs.next())
            {
                System.out.print(rs.getInt(1)+"\t");
                System.out.print(rs.getString(2)+"\t");
                System.out.print(rs.getString(3)+"\t");
                System.out.print(rs.getString(4)+"\n");
                System.out.print(rs.getString(5)+"\t");
                System.out.print(rs.getString(6)+"\t");
            }
            System.out.println("-----------------------------------");
        }
    }

}
