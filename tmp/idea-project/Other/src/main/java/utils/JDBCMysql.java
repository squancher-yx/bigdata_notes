package utils;

import java.sql.*;

public class JDBCMysql {
//    private String url = "jdbc:mysql://127.0.0.1:3306/test";
    private String url = "jdbc:mysql://127.0.0.1:3306/test?rewriteBatchedStatements=true&useSSL=false";
    private String user = "root";
    private String password = "123456";

    public static void main(String[] args) {
        JDBCMysql tmp = new JDBCMysql();
//        tmp.Test();
//        tmp.Test2();
        tmp.Test3();
    }

    /**
     * 普通写入
     * 用时：74632
     * rewriteBatchedStatements=true 无效
     */
    public void Test(){
        Connection conn = null;
        PreparedStatement pstm =null;
        ResultSet rt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO test5 values(?,?,?)";
            pstm = conn.prepareStatement(sql);
            Long startTime = System.currentTimeMillis();
            for (int i = 1; i <= 100000; i++) {
                pstm.setInt(1, i);
                pstm.setInt(2, i);
                pstm.setInt(3, i);
                pstm.executeUpdate();
            }
            Long endTime = System.currentTimeMillis();
            System.out.println("用时：" + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally{
            if(pstm!=null){
                try {
                    pstm.close();
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

    /**
     * 事务提交
     * 用时：41846
     * rewriteBatchedStatements=true 无效
     */
    public void Test2(){
        Connection conn = null;
        PreparedStatement pstm =null;
        ResultSet rt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO test5 values(?,?,?)";
            pstm = conn.prepareStatement(sql);
            Long startTime = System.currentTimeMillis();
            conn.setAutoCommit(false);
            for (int i = 1; i <= 100000; i++) {
                pstm.setInt(1, i);
                pstm.setInt(2, i);
                pstm.setInt(3, i);
                pstm.executeUpdate();
            }
            conn.commit();
            Long endTime = System.currentTimeMillis();
            System.out.println("用时：" + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally{
            if(pstm!=null){
                try {
                    pstm.close();
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

    /**
     * 批量 + 事务
     * 不设置 rewriteBatchedStatements：
     * 设置 rewriteBatchedStatements=true：用时：1279~2250
     * 开启 rewriteBatchedStatements 时必须 setAutoCommit(false)
     */
    public void Test3(){
        Connection conn = null;
        PreparedStatement pstm =null;
        ResultSet rt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT INTO test5 values(?,?)";
            pstm = conn.prepareStatement(sql);
            Long startTime = System.currentTimeMillis();
            conn.setAutoCommit(false);
            for (int i = 1; i <= 100000; i++) {
                pstm.setInt(1, i);
                pstm.setInt(2, i);
//                pstm.setInt(3, i);
                pstm.addBatch();
            }
            pstm.executeBatch();
            conn.commit();
            Long endTime = System.currentTimeMillis();
            System.out.println("用时：" + (endTime - startTime));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally{
            if(pstm!=null){
                try {
                    pstm.close();
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
}
