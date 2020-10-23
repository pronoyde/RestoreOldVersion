package com.vc.helper;

import java.util.HashMap;
import java.util.PropertyResourceBundle;

public class ReadCommits {
	
	private PropertyResourceBundle propertyResourceBundle; 
	private String commitTimestamp;
	private HashMap<String,String> commitMap;
	
	public ReadCommits(PropertyResourceBundle propertyResourceBundle) {
		this.propertyResourceBundle=propertyResourceBundle;
	}
	public ReadCommits(PropertyResourceBundle propertyResourceBundle, String commitTimestamp) {
		this.propertyResourceBundle=propertyResourceBundle;
		this.commitTimestamp=commitTimestamp;
	}

	public String read() {
		for(String key : propertyResourceBundle.keySet())
			if(key.equals(commitTimestamp))
				return propertyResourceBundle.getString(key);
		
		return "";
	}
	public HashMap<String,String> readAll() {
		commitMap= new HashMap<>();
		for(String key : propertyResourceBundle.keySet())
			commitMap.put(key, propertyResourceBundle.getString(key));
		
		return commitMap;
	}
	
}
