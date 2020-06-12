package net.ahwater.ahwaterCloud.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * jdbc工具类
 * @author dell
 *
 */
public class JdbcUtil {
    private static String url = null;
    private static String user = null;
    private static String password = null;
    private static String driverClass = null;
    
    /**
     * 静态代码段
     */
    static{
        try {
            //读取db.properties文件
            Properties props = new Properties();
            InputStream in = JdbcUtil.class.getResourceAsStream("/config/database.properties");
            //加载文件
            props.load(in);
            //读取信息
            url = props.getProperty("url");
            user = props.getProperty("user");
            password = props.getProperty("password");
            driverClass = props.getProperty("driverClass");
            
            //注册驱动程序
            Class.forName(driverClass);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("驱程程序注册出错");
        }
    }

    /**
     * 抽取获取连接对象的方法
     * @return
     */
    public static Connection getConnection(){
        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 释放资源的方法
     * @param conn
     * @param stmt
     */
    public static void close(Connection conn,Statement stmt){
        if(stmt!=null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
    
    public static void close(Connection conn,Statement stmt,ResultSet rs){
        if(rs!=null)
            try {
                rs.close();
            } catch (SQLException e1) {
                e1.printStackTrace();
                throw new RuntimeException(e1);
            }
        if(stmt!=null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        if(conn!=null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
