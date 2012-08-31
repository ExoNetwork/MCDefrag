package de.javakara.manf.mcdefrag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.plugin.java.JavaPlugin;

import de.javakara.manf.mcdefrag.listener.PlayerListener;
import de.javakara.manf.mcdefrag.api.ShedulerFactory;
import de.javakara.manf.mcdefrag.api.manager.RegionManager;
import de.javakara.manf.util.ChatTable;
import de.javakara.manf.util.LanguageComplete;
import de.javakara.manf.util.SpacerReplace;

@LanguageComplete
public class MCDefrag extends JavaPlugin{
	private DefragCommands DefragCMD;

	public static boolean METRICS_ENABLED = true;
	
	@Override
	public void onEnable(){
		instance = this;
		try {
			Config.initialise(getResource("config.yml"), getDataFolder());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Language.init(getJarLang(this));
		ShedulerFactory.initialize(this);
		RegionManager.initialise(getDataFolder());
		ChatTable.initColors(Config.getString("default-color"), Config.getStringList("table.colors"));
		PlayerListener.addWorld(Config.getStringList("worlds"));
		registerCommands();
		if(Config.getBoolean("use-mysql")){
			MySQL.initialize(Config.getString("mysql.host"),
							 Config.getInt("mysql.port") + "",
							 Config.getString("mysql.database"),
							 Config.getString("mysql.user"),
							 Config.getString("mysql.password"));
		}
		
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
			
		if(METRICS_ENABLED){
			try {
			    Metrics metrics = new Metrics(this);
			    metrics.start();
			} catch (IOException e) {
			    // Failed to submit the stats :-(
				System.out.println("Metrics failed to submit stats! D:");
				System.out.println("Execption: " + e);
			}
		}
	}
	
	@Override
	public void onDisable(){ 
		ShedulerFactory.finish();
		try {
			RegionManager.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void registerCommands() {
		DefragCMD = new DefragCommands();
		getCommand("defrag").setExecutor(DefragCMD);
	}
	
	public static final String[] getArgumentString(String node,SpacerReplace sr){
		String msg = Language.getRaw(node);
		return sr.repl(msg).split("<n>");
	}
	
	public static final InputStream getJarLang(){
		return getJarLang(instance);
	}
	
	public static final InputStream getJarLang(MCDefrag jarfile){
		return jarfile.getResource(Config.getString("localisation") + "_locale.yml");
	}
	
	private static MCDefrag instance;
}