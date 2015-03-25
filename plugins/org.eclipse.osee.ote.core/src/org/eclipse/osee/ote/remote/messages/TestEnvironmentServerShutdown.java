package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class TestEnvironmentServerShutdown extends OteEventMessage {

   public static String TOPIC = "ote/message/servershutdown";
   
   private static int SIZE = 512;
   
   public StringElement SERVER_ID;
   
	public TestEnvironmentServerShutdown() {
		super("TestEnvironmentSetBatchMode", TOPIC, SIZE);
		SERVER_ID = new StringElement(this, "SERVER_ID", getDefaultMessageData(), 0, 0, 512*8-1);
	}

   public TestEnvironmentServerShutdown(byte[] bytes) {
      this();
      setBackingBuffer(bytes);
   }

}
