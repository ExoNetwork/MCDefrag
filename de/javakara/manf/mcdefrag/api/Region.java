package de.javakara.manf.mcdefrag.api;

import org.bukkit.Location;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class Region {
	double minX;
	double maxX;
	double minY;
	double maxY;
	double minZ;
	double maxZ;
	String name;
	String world;
	
	public Region(Location a, Location b){
		//Minimal Values
		minX = Math.min(a.getX(),b.getX());
		minY = Math.min(a.getY(),b.getY());
		minZ = Math.min(a.getZ(),b.getZ());
		//Max Values
		maxX = Math.max(a.getX(),b.getX());
		maxY = Math.max(a.getY(),b.getY());
		maxZ = Math.max(a.getZ(),b.getZ());
	}
	
	public Region(double minX,double maxX,double minY,double maxY,double minZ,double maxZ){
		//Minimal Values
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		//Max Values
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}
	
	public boolean playerIsInStartRegion(double x,double y, double z) {
		if(!between(minX,maxX,x)){
			return false;
		}
		if(!between(minZ,maxZ,z)){
			return false;
		}
		if(!between(minY,maxY,y)){
			return false;
		}
		return true;
	}
	
	public boolean playerIsInStartRegion(Location pos) {
		return playerIsInStartRegion(pos.getBlockX(),pos.getBlockY(),pos.getBlockZ());
	}
	
	public static final boolean between(double min,double max,double number){
		return (number >= min) && (number <= max);
	}

	public Region setWorld(String world) {
		this.world = world;
		return this;
	}

	public String getWorld() {
		return world;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getFileString() {
		return minX + "+" + maxX + "+" + minY + "+" + maxY + "+" + minZ + "+" + maxZ;
	}

	public static Region parse(String[] ri) {
		double minX = Double.parseDouble(ri[0]);
		double maxX = Double.parseDouble(ri[1]);
		double minY = Double.parseDouble(ri[2]);
		double maxY = Double.parseDouble(ri[3]);
		double minZ = Double.parseDouble(ri[4]);
		double maxZ = Double.parseDouble(ri[5]);
		return new Region(minX,maxX,minY,maxY,minZ,maxZ);
	}
}
