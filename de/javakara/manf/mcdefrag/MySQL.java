package de.javakara.manf.mcdefrag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Vector;

import de.javakara.manf.mcdefrag.api.Highscore;
import de.javakara.manf.mcdefrag.api.Region;

public class MySQL {
	static String url;
	static String user;
	static String pass;
	
	String table;
	String regionname;
	String filestring;
	String maxtime;
	String disabled;
	String world;
	String endzone;
	
	public MySQL(){
		
	}
	
	static void initialize(String host, String port, String database, String user, String password) {
		MySQL.url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		MySQL.user = user;
		MySQL.pass = password;
	}

	static Connection getConnection() throws SQLException {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, user, pass);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return con;
	}
	
	public Vector<HashMap<String, String>> load() throws SQLException{
		Statement stmt = getConnection().createStatement();
		String query = "SELECT * FROM " + table;
		System.out.println("{DEBUG} " + query);
		ResultSet rs = stmt.executeQuery(query);
		Vector<HashMap<String,String>> vec = new Vector<HashMap<String,String>>();
		while(rs.next()){
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("regionname", rs.getString("regionname"));
			map.put("filestring",rs.getString("filestring"));
			map.put("maxtime",rs.getLong("maxtime") + "");
			map.put("disabled",rs.getBoolean("disabled") + "");
			map.put("world",rs.getString("world"));
			map.put("endzone",rs.getString("endzone"));
			vec.add(map);
		}
		rs.close();
		return vec;
	}
	
	public HashMap<String,Long> loadHighscore(int id,String scoretable,String name) throws SQLException{
		Statement stmt = getConnection().createStatement();
		String query = "SELECT playername, amount FROM " + scoretable + " WHERE routeid='" + id + "'";
		HashMap<String,Long> map = new HashMap<String,Long>();
		System.out.println("{DEBUG} " + query);
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()){
			map.put(rs.getString("playername"), rs.getLong("amount"));
		}
		rs.close();
		return map;
	}
	
	public HashMap<String,Long> loadHighscore(String scoretable,String name) throws SQLException{
		return loadHighscore(getRegionID(name),scoretable,name);
	}
	
	public int getRegionID(String regionname) throws SQLException{
		Statement stmt = getConnection().createStatement();
		String query = "SELECT regionid FROM " + table + " WHERE regionname='" + regionname + "'";
		System.out.println("{DEBUG} " + query);
		ResultSet rs = stmt.executeQuery(query);
		if(rs.next()){
			int id = rs.getInt("regionid");
			rs.close();
			return id;
		}
		rs.close();
		return -1;
		
	}
	
	public void saveHighscore(String scoretable,String name,Highscore highscore) throws SQLException {
		int id = getRegionID(name);
		if(id == -1){
			return;
		}
		HashMap<String,Long> oldScore = loadHighscore(id,scoretable,name);
		Statement stmt = getConnection().createStatement();
		for(String[] score:highscore.getScores()){
			String player = score[Highscore.type_name];
			Long amount = Long.parseLong(score[Highscore.type_score]);	
			if(!player.equalsIgnoreCase("")){
				if(oldScore.containsKey(player)){
					//Player exist
					if(oldScore.get(player) == amount){
						//Value does not need to be modified
						oldScore.remove(player);
					}else{
						//Player has different amount
						StringBuilder sb = new StringBuilder();
						sb.append("UPDATE " + scoretable + " ");
						sb.append("SET amount='" + amount + "'");
						sb.append("WHERE playername='" + player + "'");
						sb.append("AND amount='" + oldScore.get(player) + "'");
						System.out.println("{DEBUG} " + sb.toString());
						stmt.executeUpdate(sb.toString());
						oldScore.remove(player);
					}
				}else{
					StringBuilder sb = new StringBuilder();
					sb.append("INSERT INTO " + scoretable + "(");
					sb.append("scoreid,");
					sb.append("routeid,");
					sb.append("playername,");
					sb.append("amount) ");
					sb.append("VALUES (NULL");
					sb.append(fetch(id + ""));
					sb.append(fetch(player));
					sb.append(fetch(amount + "") + ")");
					System.out.println("{DEBUG} " + sb.toString());
					stmt.executeUpdate(sb.toString());
				}
			}				
		}
		
		for(String p:oldScore.keySet()){
			//remove players score
			//DELETE FROM Store_Information
			//WHERE store_name = "Los Angeles"
			StringBuilder sb = new StringBuilder();
			sb.append("DELETE FROM" + scoretable + " ");
			sb.append("WHERE playername='" + p + "'");
			sb.append("AND amount='" + oldScore.get(p) + "'");
			System.out.println("{DEBUG} " + sb.toString());
			stmt.executeUpdate(sb.toString());
		}
		
		stmt.close();
	}
	
	public void saveInformation() throws SQLException {
		Statement stmt = getConnection().createStatement();
		String query = "SELECT regionid FROM " + table + " WHERE regionname='" + regionname + "'";
		System.out.println("{DEBUG} " + query);
		ResultSet rs = stmt.executeQuery(query);
		StringBuilder sb = new StringBuilder();
		System.out.println("rs.next()" + rs.isBeforeFirst());
		if(!rs.next()){
			sb.append("INSERT INTO " + table + "(");
			sb.append("regionid,");
			sb.append("regionname,");
			sb.append("filestring,");
			sb.append("maxtime,");
			sb.append("disabled,");
			sb.append("world,");
			sb.append("endzone)");
			sb.append("VALUES (NULL");
			sb.append(fetch(regionname));
			sb.append(fetch(filestring));
			sb.append(fetch(maxtime));
			sb.append(fetch(disabled));
			sb.append(fetch(world));
			sb.append(fetch(endzone) + ")");
			System.out.println("{DEBUG} " + sb.toString());
			stmt.executeUpdate(sb.toString());
		}else{
			sb.append("UPDATE " + table + " ");
			sb.append("SET filestring='" + filestring + "'");
			sb.append(update_fetch("maxtime",maxtime));
			sb.append(update_fetch("disabled",disabled));
			sb.append(update_fetch("world",world));
			sb.append(update_fetch("endzone",endzone));
			sb.append("WHERE regionid='" + rs.getInt("regionid") + "'");
			System.out.println("{DEBUG} " + sb.toString());
			stmt.executeUpdate(sb.toString());
		}

		stmt.close();
	}
	
	private static String fetch(String value){
		return ", '" + value + "'";
	}
	
	private static String update_fetch(String key,String value){
		return ", " + key + "='" + value+ "'";
	}
	
	public void setTable(String table){
		this.table = table;
	}
	
	public void setRegion(Region r){
		this.regionname = r.getName();
		this.filestring = r.getFileString();
		this.world = r.getWorld();
	}
	
	public void setFlags(boolean disabled ,Long maxtime,Region end){
		if(disabled){
			this.disabled = "1";
		}else{
			this.disabled = "0";
		}
		this.endzone = end.getFileString();
		this.maxtime = maxtime + "";
	}
}