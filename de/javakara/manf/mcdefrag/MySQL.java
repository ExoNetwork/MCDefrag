package de.javakara.manf.mcdefrag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
   static String url;
   static String user;
   static String pass;
    
   static void initialize(String host,String port,String database,String user,String password){
	   MySQL.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
	   MySQL.user = user;
	   MySQL.pass = password;
   }
   
   static Connection getConnection() throws SQLException {
	      Connection con = null;
	      try {
	         Class.forName("com.mysql.jdbc.Driver"); 
	         con = DriverManager.getConnection(url, user, pass);
	      }
	      catch(ClassNotFoundException e) {
	         System.out.println(e.getMessage());
	      }
	      return con;
	   }
}
