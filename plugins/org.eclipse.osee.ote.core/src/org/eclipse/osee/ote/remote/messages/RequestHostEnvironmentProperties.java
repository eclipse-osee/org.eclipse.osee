package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public class RequestHostEnvironmentProperties extends OteEventMessage {

   public static String TOPIC = "ote/message/propertiesreq";
   
	public RequestHostEnvironmentProperties() {
		super("RequestHostEnvironmentProperties", TOPIC, 1);
		
		getDefaultMessageData().getMsgHeader().RESPONSE_TOPIC.setValue(SerializedEnhancedProperties.EVENT);
	}
	
}
