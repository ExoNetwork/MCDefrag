package de.javakara.manf.mcdefrag;

import java.io.InputStream;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class Language {
	private static FileConfiguration language;
	
	public static boolean init(InputStream jarlang){
		language = YamlConfiguration.loadConfiguration(jarlang);
		return true;
	}
	
	public static String[] get(String node){
		return getRaw(node).split("<n>");
	}

	public static String getRaw(String node) {
		return ChatColor.translateAlternateColorCodes('&', language.getString(node));

	}

	public static void reload(InputStream jarlang) {
		language = YamlConfiguration.loadConfiguration(jarlang);
	}
}
