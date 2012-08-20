package de.javakara.manf.mcdefrag;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class Language {
	private static File languageFile;;
	private static FileConfiguration language;
	
	public static boolean init(File dataFolder){
		languageFile = new File(dataFolder + File.separator + "language.yml");
		if(!languageFile.exists()){
			try {
				languageFile.createNewFile();
				regenerate();
				save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		language = YamlConfiguration.loadConfiguration(languageFile);
		return true;
	}
	
	static void regenerate() {
		language = new YamlConfiguration();
		language.set("permissionsdenied", "&3Permissions denied!");
		language.set("consoleunspported","This Plugin has no Console Commands");
		language.set("regions.header","ID-Name-Record");
		language.set("highscore.header", "ID-Name-Zeit");
		language.set("highscore.nonumber", "ID invalid!");
		language.set("time.changed","Time changed: %time% Sekunden");
		language.set("time.invalid", "Not a valid Number!");
		language.set("time.announce","Aktuelle Zeit: %time% Sekunden");
		language.set("finish.invalid", "No name for end!");
		language.set("finish.created","%track% created!");
		language.set("player.toolong", "Du hast leider zu lange gebraucht!");
		language.set("player.won", "Gewonnen. Deine Zeit ist: %time% Sekunden");
		language.set("player.newrecord","%playername% hat in der Strecke '%route%' einen Rekord aufgestellt: %time% Sekunden");
		language.set("player.nosave", "You have no Permissions to save your Stats!");
		language.set("create.invalid", "No name for create!");
		language.set("create.created","%track% created!");
		language.set("reset.route","Strecke %track% wurde zurückgesetzt");
		language.set("reset.user", "Du kannst nun neu Anfangen!");
		language.set("region.invalid","valid Section plz");
		language.set("region.protected","Protected!");
		language.set("region.blocked", "Currently not Avaible");
		language.set("region.start", "[Defrag] GO, GO, GO!");
		language.set("route.noworld", "Keine Strecke für diese Welt gefunden!");
		language.set("route.noroute", "Strecke nicht gefunden gefunden!");
		language.set("route.enabled", "Strecke aktiviert!");
		language.set("route.disabled","Stecke deaktiviert");
		language.set("selection.update","Block %type% set!: %location%");
		language.set("toggleselection.remove","You're now out of Selection!");
		language.set("toggleselection.add","You can now select a Region!");
		language.set("commands.help.time", "/defrag time <name> <time> <unit><n>HILFE");
		language.set("commands.help.lock", "/defrag lock <name>");
		language.set("commands.help.unlock","/defrag unlock <name>");
	}

	private static void save() {
		try {
			language.save(languageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String[] get(String node){
		return getRaw(node).split("<n>");
	}

	public static String getRaw(String node) {
		if(!language.isSet(node)){
			language.set(node, "asdf");
			save();
		}
		return ChatColor.translateAlternateColorCodes('&', language.getString(node));

	}
}
