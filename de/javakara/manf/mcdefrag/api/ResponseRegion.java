package de.javakara.manf.mcdefrag.api;

import de.javakara.manf.util.LanguageComplete;

@LanguageComplete
public class ResponseRegion {
	private Region response;
	
	public void setResponse(Region r){
		response = r;
	}
	public Region getResponse() {
		return response;
	}
}
