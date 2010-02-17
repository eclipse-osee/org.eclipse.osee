/*
 * Created on Feb 11, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.camel;

import java.util.HashMap;
import java.util.Map;

/**
 * @author b1528444
 *
 */
public abstract class SendMessage implements Runnable {

	private final String version;
	private final String sourceId;
	
	public SendMessage(String version, String sourceId){
		this.version = version;
		this.sourceId = sourceId;
	}
	
	public Map<String, Object> getBaseHeader(){
		Map<String, Object> headers = new HashMap<String, Object>();
		headers.put("OSEEversion", version);
		headers.put("OSEEsourceId", sourceId);
		return headers;
	}
}
