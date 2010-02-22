/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.core;

import org.eclipse.osee.framework.messaging.MessageID;

/**
 * @author b1528444
 *
 */
public enum OteBaseMessages implements MessageID {
	RequestOteHost(true, "ABjyjamBQRvvgsdgwers", "topic:lba.ote.get.host", null, true);
	
	private String name;
	private Class<?> clazz;
	boolean isReplyRequired;
	private String guid;	
	private String destination;
	
	OteBaseMessages(boolean isTopic, String guid, String name, Class<?> clazz, boolean isReplyRequired){
		this.guid = guid;
		this.name = name;
		this.clazz = clazz;
		this.isReplyRequired = isReplyRequired;
		if(isTopic){
			destination = "topic:"+guid;
		} else {
			destination = guid;
		}
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
	public String getGuid() {
		return guid;
	}

	@Override
	public String getMessageDestination() {
		return destination;
	}

   @Override
   public boolean isTopic() {
      return true;
   }
}
