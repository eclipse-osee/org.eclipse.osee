/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.message;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class MessageProviderVersion {
	Set<String> versions = new ConcurrentSkipListSet<>();
	
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
