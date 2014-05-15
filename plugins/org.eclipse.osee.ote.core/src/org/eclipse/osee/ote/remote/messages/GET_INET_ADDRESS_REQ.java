package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class GET_INET_ADDRESS_REQ extends OteEventMessage {

   public static String TOPIC = "ote/message/addressreq";
   
   public final EnumeratedElement<SOCKET_ID> SOCKET_ID;   
   
	public GET_INET_ADDRESS_REQ() {
		super("GET_INET_ADDRESS_REQ", TOPIC, 1);
		SOCKET_ID = createEnumeratedElement("SOCKET_ID", 1, SOCKET_ID.class);
		
		getDefaultMessageData().getMsgHeader().RESPONSE_TOPIC.setValue(GET_INET_ADDRESS_RESP.TOPIC);
	}
	
}
