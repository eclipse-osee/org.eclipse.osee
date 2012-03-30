package org.eclipse.osee.ote.ui.message.internal;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import org.eclipse.osee.ote.message.MessageDefinitionProvider;

public class MessageProviderVersion {
	Set<String> versions = new ConcurrentSkipListSet<String>();
	
	public void add(MessageDefinitionProvider provider){
		versions.add(generateVersion(provider));
	}
	
	public void remove(MessageDefinitionProvider provider){
		versions.remove(generateVersion(provider));
	}
	
	public String getVersion(){
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
}
