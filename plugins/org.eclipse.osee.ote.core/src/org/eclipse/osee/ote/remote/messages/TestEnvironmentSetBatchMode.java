package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.BooleanElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;

public class TestEnvironmentSetBatchMode extends OteEventMessage {

   public static String TOPIC = "ote/message/setbatchmode";
   
   private static int SIZE = 1;
   
   public BooleanElement SET_BATCH_MODE;
   
	public TestEnvironmentSetBatchMode() {
		super("TestEnvironmentSetBatchMode", TOPIC, SIZE);
		SET_BATCH_MODE = new BooleanElement(this, "SET_BATCH_MODE", getDefaultMessageData(), 0, 0, 7);
	}

   public TestEnvironmentSetBatchMode(byte[] bytes) {
      this();
      setBackingBuffer(bytes);
   }

}
