/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database_connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DB {
       static Connection conn = null;
       static String dbName = "EVENTSv1";
       static String serverip="localhost";
       static String serverport="1433";
       static String url = "jdbc:sqlserver://"+serverip+"\\SQLEXPRESS:"+serverport+";databaseName="+dbName+"";
       static Statement stmt = null;
       static ResultSet result = null;
       static String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
       static String databaseUserName = "miki";
       static String databasePassword = "matura";
        
        public static Connection initDB() throws SQLException {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn = DriverManager.getConnection(url,databaseUserName,databasePassword);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DB.class.getName()).log(Level.SEVERE, null, ex);
            }
            return conn;
         }    
     }
