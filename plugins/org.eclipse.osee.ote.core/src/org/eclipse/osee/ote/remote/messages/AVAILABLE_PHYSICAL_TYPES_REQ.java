package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public class AVAILABLE_PHYSICAL_TYPES_REQ extends OteEventMessage {

   public static String TOPIC = "ote/message/availabletypesreq";
   
	public AVAILABLE_PHYSICAL_TYPES_REQ() {
		super("AVAILABLE_PHYSICAL_TYPES_REQ", TOPIC, 0);
		
	}

}
