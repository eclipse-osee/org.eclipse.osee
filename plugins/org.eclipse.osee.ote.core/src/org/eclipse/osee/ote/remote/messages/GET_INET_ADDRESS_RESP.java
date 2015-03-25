package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.EnumeratedElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.SOCKET_ADDRESS_RECORD;

public class GET_INET_ADDRESS_RESP extends OteEventMessage {

   public static String TOPIC = "ote/message/addressresp";
   
   public final EnumeratedElement<SOCKET_ID> SOCKET_ID;   
   public final SOCKET_ADDRESS_RECORD ADDRESS;   
   
	public GET_INET_ADDRESS_RESP() {
		super("GET_INET_ADDRESS_RESP", TOPIC, 22);
		SOCKET_ID = createEnumeratedElement("SOCKET_ID", 1, SOCKET_ID.class);
		ADDRESS = new SOCKET_ADDRESS_RECORD(this, "ADDRESS", getDefaultMessageData(), 1, 0, SOCKET_ADDRESS_RECORD.SIZE*8-1);
	}

}
