package de.javakara.manf.mcdefrag.api.manager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import de.javakara.manf.mcdefrag.MCDefrag;
import de.javakara.manf.mcdefrag.api.Region;
import de.javakara.manf.mcdefrag.api.SelectedBlock;
import de.javakara.manf.util.LanguageComplete;
import de.javakara.manf.util.SpacerReplace;

@LanguageComplete
public class PlayerManager {
	static HashMap<String,String> players = new HashMap<String,String>();
	static ArrayList<String> selection = new ArrayList<String>();
	static HashMap<String, SelectedBlock> sel = new HashMap<String,SelectedBlock>();

	/* Selection Methods*/
	public static String toogleSelection(String name){
		if (selection.contains(name)) {
			selection.remove(name);
			return "toggleselection.remove";
		} else {
			selection.add(name);
			return "toggleselection.add";
		}
	}
	
	public static Region playerSelectionToRegion(String name){
		if (selection.contains(name)) {
			SelectedBlock sb = sel.get(name);
			if(sb.getA() != null && sb.getB() != null){
				return new Region(sb.getA(), sb.getB()).setWorld(sb.getWorld());
			}
		}
		return null;
	}
	
	public static String[] updateSelection(String name,int type,Location loc){
		if (selection.contains(name)) {
			if (sel.containsKey(name)) {
				sel.get(name).set(type,loc);
			} else {
				sel.put(name, new SelectedBlock().set(type,loc));
			}
			SpacerReplace sr = new SpacerReplace();
			sr.addSpacer("%location%", loc.toString());
			sr.addSpacer("%type%", (type+1)+"");
			return MCDefrag.getArgumentString("selection.update", sr);
		}
		return null;
	}
	
	public static void clearSelection(String name) {
		selection.remove(name);
		sel.remove(name);
	}
	
	/* Players Methods */
	public static boolean isPlaying(String name) {
		return players.containsKey(name);
	}

	protected static String removePlayingPlayer(String name){
		return players.remove(name);
	}

	protected static String addPlayingPlayer(String name,String region) {
		return players.put(name,region);
	}

	public static String getPlayerRoute(String name) {
		return players.get(name);
	}


}
