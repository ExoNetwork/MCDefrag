package de.javakara.manf.mcdefrag.api;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class ShedulerFactory {
	private static JavaPlugin plugin;
	private static HashMap<String,Integer> registered = new HashMap<String,Integer>();
	
	public static void initialize(JavaPlugin plugin){
		ShedulerFactory.plugin = plugin;
	}
	
	public static int newSheduler(Runnable r,long start,long interval){
		return Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, r, start, interval);
	} 

	public static void register(int i, String name) {
		registered.put(name, i);		
	}
	
	public static void unregister(String name){
		if(registered.containsKey(name)){
			int i = registered.get(name);
			Bukkit.getScheduler().cancelTask(i);
		}
	}
	
	public static void finish(){
		Bukkit.getScheduler().cancelTasks(plugin);
	}
}
