package org.eclipse.osee.ote.remote.messages;

import org.eclipse.osee.ote.message.elements.StringElement;
import org.eclipse.osee.ote.message.event.OteEventMessage;
import org.eclipse.osee.ote.message.event.SOCKET_ADDRESS_RECORD;

public class TestEnvironmentTransferFile extends OteEventMessage {

   public static String TOPIC = "ote/message/transferfile";
   
   private static int SIZE = SOCKET_ADDRESS_RECORD.SIZE + 1024;
   
   public SOCKET_ADDRESS_RECORD ADDRESS;
   public StringElement FILE_PATH;
   
	public TestEnvironmentTransferFile() {
		super("TestEnvironmentSetBatchMode", TOPIC, SIZE);
		ADDRESS = new SOCKET_ADDRESS_RECORD(this, "ADDRESS", getDefaultMessageData(), 0, 0, SOCKET_ADDRESS_RECORD.SIZE*8-1);
		FILE_PATH = new StringElement(this, "FILE_PATH", getDefaultMessageData(), SOCKET_ADDRESS_RECORD.SIZE, 0, 1024*8-1);
	}

   public TestEnvironmentTransferFile(byte[] bytes) {
      this();
      setBackingBuffer(bytes);
   }

}
