package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class RunTestsGetCommandResultReq extends OteEventMessage {

   public static String TOPIC = "ote/message/runtests/commandresultreq";
   
   private static int SIZE = 256;
   
   public StringElement GUID;
   
	public RunTestsGetCommandResultReq() {
		super("MESSAGE_META_DATA_STAT", TOPIC, SIZE);
		GUID = new StringElement(this, "MESSAGE", getDefaultMessageData(), 0, 0, OteEventMessage.sizeBytesBits(256));
		getDefaultMessageData().getMsgHeader().RESPONSE_TOPIC.setValue(BooleanResponse.TOPIC);
	}

   public RunTestsGetCommandResultReq(byte[] bytes) {
      this();
      setData(bytes);
   }

}
