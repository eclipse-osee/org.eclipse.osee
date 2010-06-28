/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;
import org.eclipse.osee.framework.messaging.services.messages.Synch;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public enum BaseMessages implements MessageID {
	ServiceHealth(true, "ABjyjamBQRvvAGcWpRQA", "osee.message.core.ServiceHealth", ServiceHealth.class, false),
	ServiceHealthRequest(true, "ABkAHOSFQ3VUZcfzsAgA", "osee.message.core.ServiceHealthRequest", ServiceHealthRequest.class, true),
	Synch(true, "ABkweOSFQ3yuZcfzsAgA", "osee.message.core.ServiceHealthRequest", Synch.class, true);
	
	private String name;
	private Class<?> clazz;
	boolean isReplyRequired;
   private boolean isTopic;
   private String guid;
	
	BaseMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired){
		this.guid = guid;
		this.name = name;
		this.clazz = clazz;
		this.isReplyRequired = isReplyRequired;
		this.isTopic = isTopic;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getSerializationClass() {
		return clazz;
	}

	@Override
	public boolean isReplyRequired() {
		return isReplyRequired;
	}

	@Override
	public boolean isTopic(){
	   return isTopic;
	}

   @Override
   public String getId() {
      return guid;
   }
}
