package org.eclipse.osee.ote.jms.bridge;

import org.eclipse.osee.framework.messaging.MessageID;


public enum BridgeMessages implements MessageID {
	
	BYTE_MESSAGE("OTE_EVENT_MESSAGE");
	
	private String id;

	BridgeMessages(String id){
		this.id = id;
	}
	
	@Override
	public Class<?> getSerializationClass() {
		return null;
	}

	@Override
	public boolean isReplyRequired() {
		return false;
	}

	@Override
	public boolean isTopic() {
		return true;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return id;
	}

}
