/*
 * Created on Nov 29, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;

public class OteEventMessage extends Message<MessageSystemTestEnvironment, OteEventMessageData, OteEventMessage>{

   private final OteEventMessageData data;
   
   public OteEventMessage(String name, String topic, int defaultByteSize) {
      super(name, defaultByteSize, 0, false, 0, 0);
      data = new OteEventMessageData(this, topic, defaultByteSize);
      setDefaultMessageData(data);
      setMemSource(OteEventMessageType.OTE_EVENT_MESSAGE);
   }
   
   public OteEventMessage(byte[] bytedata) {
	  super("holder", 0, 0, false, 0, 0);
	  data = new OteEventMessageData(this, bytedata.length);
      data.getMem().setData(bytedata);
	  setDefaultMessageData(data);
      setMemSource(OteEventMessageType.OTE_EVENT_MESSAGE);
   }

   public OteEventMessageHeader getHeader(){
	   return ((OteEventMessageData)getDefaultMessageData()).getMsgHeader();
   }
   
}
