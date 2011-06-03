package org.eclipse.osee.ote.core.framework.saxparse.elements;

public class PropertyElementHandlerData {

	private final String key;

	protected PropertyElementHandlerData(String key){
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
