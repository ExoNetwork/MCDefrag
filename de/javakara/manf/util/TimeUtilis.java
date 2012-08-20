package de.javakara.manf.util;

public class TimeUtilis {
	public static String getFormattedMinutes(long timelong,long modify){
		return (timelong/modify) + "." + (timelong%modify);
	}
}
