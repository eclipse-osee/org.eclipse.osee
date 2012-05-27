package org.eclipse.osee.ote.message;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class MessageProviderVersion {
	Set<String> versions = new ConcurrentSkipListSet<String>();
	
	public synchronized void add(MessageDefinitionProvider provider){
		versions.add(generateVersion(provider));
	}
	
	public synchronized void remove(MessageDefinitionProvider provider){
		versions.remove(generateVersion(provider));
	}
	
	public synchronized String getVersion(){
		if(versions.size() == 0){
			return "no library detected";
		}
		StringBuilder sb = new StringBuilder();
		for(String ver:versions){
			sb.append(ver);
			sb.append("\n");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	private String generateVersion(MessageDefinitionProvider provider){
		return String.format("%s[%s.%s]", provider.singletonId(), provider.majorVersion(), provider.minorVersion());
	}
	
	public synchronized boolean isAnyAvailable(){
		return versions.size() > 0;
	}
}
