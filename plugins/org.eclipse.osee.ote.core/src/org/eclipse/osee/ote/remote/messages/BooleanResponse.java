package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class BooleanResponse extends OteEventMessage {

   public static String TOPIC = "ote/message/boolresponse";
   
   private static int SIZE = 1;
   
   public BooleanElement VALUE;
   
	public BooleanResponse() {
		super("MESSAGE_META_DATA_STAT", TOPIC, SIZE);
		VALUE = new BooleanElement(this, "IS_SCHEDULED", getDefaultMessageData(), 0, 0, 7);
	}

}
