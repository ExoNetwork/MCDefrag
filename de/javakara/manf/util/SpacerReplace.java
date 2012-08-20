package de.javakara.manf.util;

import java.util.HashMap;

public class SpacerReplace {
	HashMap<String,String> replacements;

	public SpacerReplace() {
		replacements = new HashMap<String,String>();
	}

	public void addSpacer(String replace, String to){
		replacements.put(replace, to);
	}
	
	public String repl(String main){
		String repl = main;
		for(String replace:replacements.keySet()){
			repl = repl.replace(replace, replacements.get(replace));
		}
		return repl;
	}
}
