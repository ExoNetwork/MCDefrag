package de.javakara.manf.mcdefrag.api;

import org.bukkit.Location;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class SelectedBlock {
	private Location a;
	private Location b;
	
	public SelectedBlock setB(Location b) {
		this.b = b;
		return this;
	}
	public Location getB() {
		return b;
	}
	public SelectedBlock setA(Location a) {
		this.a = a;
		return this;
	}
	public Location getA() {
		return a;
	}
	public SelectedBlock set(int type, Location loc) {
		switch(type){
		case 1:
			return setA(loc);
		case 0:
			return setB(loc);
		}
		return null;
	}
	
	public String getWorld() {
		return a.getWorld().getName();
	}
	

}
