package de.javakara.manf.mcdefrag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public final class Config {
	private static FileConfiguration config;
	private static File configFile;
	
	static boolean initialise(InputStream jarConfig,File dataFolder) throws IOException{
		Config.configFile = new File(dataFolder + File.separator + "config.yml");
		if(!configFile.exists()){
			copy(jarConfig, configFile);
		}
		Config.config = YamlConfiguration.loadConfiguration(configFile);
		return true;		
	}
	
	public static int getInt(String node){
		return config.getInt(node);
	}
	
	public static String getString(String node){
		if(!config.isSet(node)){
			config.set(node, "asdf");
			try {
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return config.getString(node);
	}

	public static boolean getBoolean(String node) {
		return config.getBoolean(node);
	}
	
	public static double getDouble(String node) {
		return config.getDouble(node);
	}
	
	public static List<String> getStringList(String node){
		return config.getStringList(node);
	}
	
	public static Long getLong(String node) {
		return config.getLong(node);
	}
	
	static void reload(){
		Config.config = YamlConfiguration.loadConfiguration(configFile);
	}
	
	static String getPath() {
		return configFile.getParent();
	}
	
	static void save() throws IOException {
		config.set("defrag.version", 1);
		config.save(configFile);
	}
	
	static void copy(InputStream in, File file) {
	    try {
	        OutputStream out = new FileOutputStream(file);
	        byte[] buf = new byte[1024];
	        int len;
	        while((len=in.read(buf))>0){
	            out.write(buf,0,len);
	        }
	        out.close();
	        in.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
