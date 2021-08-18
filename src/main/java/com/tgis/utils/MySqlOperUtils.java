package com.tgis.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author QiuMingJie
 * @date 2021/8/18 17:06
 * @description 192.168.43.96
 * port = 3306
 * db = 'wuyilin'
 * user = 'root'
 * password = 'SCAU17206'
 */
public class MySqlOperUtils {

    static Connection conn = null;



    public static void initConn() throws ClassNotFoundException, SQLException {

        String url = "jdbc:mysql://192.168.43.96:3306/wuyilin?"
                + "user=root&password=SCAU17206&useUnicode=true&characterEncoding=UTF8&useSSL=false&serverTimezone=UTC";

        try {
            // Dynamically load mysql driver
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Successfully loaded MySQL driver");
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String randomStr(int size) {
        // Define an empty string
        String result = "";
        for (int i = 0; i < size; ++i) {
            // Generate an int type integer between 97 ~ 122
            int intVal = (int) (Math.random() * 26 + 97);
            // Force conversion (char) intVal Convert the corresponding value to the corresponding character, and splicing the characters
            result = result + (char) intVal;
        }
        // Output string
        return result;
    }

//    public static void executeSql(String sql) {
//        try {
//            initConn();
//            // save the sql suffix
//            StringBuffer suffix = new StringBuffer();
//            // Set the transaction to non-automatic commit
//            conn.setAutoCommit(false);
//            PreparedStatement pst = conn.prepareStatement(sql);
//            pst.executeQuery();
//            // commit the transaction
//            conn.commit();
//            // close the connection
//            pst.close();
//            conn.close();
//        } catch (SQLException | ClassNotFoundException throwables) {
//            throwables.printStackTrace();
//        }
//    }

    public static void batchInsertSql(String sqls) {
        // open time
        Long begin = System.currentTimeMillis();
        System.out.println("Start Inserting Data...");
        // sql prefix
        String prefix = "INSERT INTO tb_data (id, user_name, create_time, random) VALUES ";

        try {
            initConn();
            // save the sql suffix
            StringBuffer suffix = new StringBuffer();
            // Set the transaction to non-automatic commit
            conn.setAutoCommit(false);
            Statement pst = conn.createStatement();
            // Add execution sql
            for (String sql : sqls.split(";")) {
                if (null != sql && !sql.equals("")&& !sql.replace("\r\n","").equals("")) {
                    pst.addBatch(sql.replace("\r\n",""));
                }
            }
            // perform the operation
            pst.executeBatch();
            // commit the transaction
            conn.commit();

            // close the connection
            pst.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        // End Time
        Long end = System.currentTimeMillis();
        System.out.println("Time-consuming : " + (end - begin) / 1000 + "seconds");
    }
}
