package hudi;

import java.sql.*;

public class InsertMetrics {
    private String url = "jdbc:mysql://192.168.121.128:3306/test?useSSL=false";
//    private String url = "jdbc:mysql://test-tidb.duoyioa.com:3306/metric?useSSL=false";
    //    private String url = "jdbc:mysql://192.168.121.128:3306/test?rewriteBatchedStatements=true&useSSL=false";
    private String user = "root";
//    private String user = "dyuser";
    private String password = "123321";
//    private String password = "mxworld2006999";

    public static void main(String[] args) {

    }

    public void spark(String[] value, String timestamp) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT IGNORE INTO spark_metric values(?,?,?,?,?,?,?,?,?,?,?,?)";
            pstm = conn.prepareStatement(sql);

            pstm.setLong(1, Long.parseLong(value[0]));
            pstm.setDouble(2, Double.parseDouble(value[1]));
            pstm.setDouble(3, Double.parseDouble(value[2]));
            pstm.setLong(4, Long.parseLong(value[3]));
            pstm.setLong(5, Long.parseLong(value[4]));
            pstm.setLong(6, Long.parseLong(value[5]));
            pstm.setLong(7, Long.parseLong(value[6]));
            pstm.setLong(8, Long.parseLong(value[7]));
            pstm.setLong(9, Long.parseLong(value[8]));
            pstm.setLong(10, Long.parseLong(value[9]));
            pstm.setLong(11, Long.parseLong(value[10]));
            pstm.setString(12, timestamp);

            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void hudi(String[] value, String timestamp) {
        Connection conn = null;
        PreparedStatement pstm = null;
        ResultSet rt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(url, user, password);
            String sql = "INSERT IGNORE INTO hudi_metric values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstm = conn.prepareStatement(sql);

            for (int i = 1; i <=18; i++) {
                pstm.setLong(i, Long.parseLong(value[i-1]));
            }
            pstm.setString(19, timestamp);
            pstm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            if (pstm != null) {
                try {
                    pstm.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
