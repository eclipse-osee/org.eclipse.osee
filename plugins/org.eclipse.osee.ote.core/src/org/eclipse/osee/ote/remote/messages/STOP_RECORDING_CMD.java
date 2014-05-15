package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.event.OteEventMessage;

public class STOP_RECORDING_CMD extends OteEventMessage {

   public static String TOPIC = "ote/message/recordstop";
   
	public STOP_RECORDING_CMD() {
		super("STOP_RECORDING_CMD", TOPIC, 0);
	}

}
