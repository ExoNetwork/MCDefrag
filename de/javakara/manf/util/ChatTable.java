package de.javakara.manf.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class ChatTable {
	String[][] asdf;
	int columns,rows;
    int size = 1;
	private static String def;
	private static ArrayList<String> col = new ArrayList<String>();
	
	public static void initColors(String def,List<String> list){
		ChatTable.def = def;
		for(String s:list){
			col.add(s);
		}
	}
	
	public ChatTable(int columns,int rows){
		if(col.size() < columns){
			while(col.size()<columns){
				col.add(def);
			}
		}
		asdf = new String[columns][rows];
		this.columns = columns;
		this.rows = rows;
	}
	
	public String[] output(){
		ArrayList<String> output = new ArrayList<String>();
		ArrayList<Integer> maxlength = new ArrayList<Integer>();
		StringBuilder stripline = new StringBuilder(ChatColor.translateAlternateColorCodes('&', def) + "|");
		for(int c=0;c<rows;c++){
			int l = maxlength(c);
			maxlength.add(c,l);
			for(int i=0;i<l+2;i++){
				stripline.append("-");
			}
			stripline.append("|");
		}
		String s = stripline.toString();
		output.add(s);
		for(int y=0;y<columns;y++){
			output.add(column(y,maxlength));
			output.add(s);
		}
		return output.toArray(new String[0]);
	}
	
	private String column(int column, ArrayList<Integer> maxlength) {
		StringBuilder r = new StringBuilder(ChatColor.translateAlternateColorCodes('&', def) + "|");
		for(int row=0;row<rows;row++){
			String s = asdf[column][row].replace(" ", "+");
			int a = maxlength.get(row) - s.length();
			s = ChatColor.translateAlternateColorCodes('&',col.get(column)+s+def);
			for(int b=0;b<a+2;b++){
				s = s + "+";
			}
			r.append(s).append("|");
		}
		return r.toString();
	}

	public int maxlength(int row){
		int max = 0;
		for(int y=0;y<columns;y++){
			String s =asdf[y][row];
			if(!(s == "")){
				int a = s.length();
				if(a > max){
					max = a;
				}
			}
		}
		return max;
	}
	
	public void add(String string){
		asdf[size][0] = size + "";
		set(1,size,string);
		size++;
	}

	public void setHeader(String string) {
		set(0,0,string);
	}
	
	private void set(int start,int index,String string){
		String[] split = string.split("-");
		int i = start;
		
		for(String s:split){
			asdf[index][i] = s.trim();
			i++;
		}
	}
}
