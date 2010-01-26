/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import org.eclipse.osee.framework.messaging.MessageName;

/**
 * @author b1528444
 *
 */
public enum BaseMessages implements MessageName {
	ServiceHealth("topic:osee.message.core.ServiceHealth"),
	ServiceHealthRequest("topic:osee.message.core.ServiceHealthRequest");

	private String name;
	
	BaseMessages(String name){
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}

}
