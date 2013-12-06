package org.eclipse.osee.ote.message.event;

import org.eclipse.osee.ote.message.elements.StringElement;

public class FileAvailableRequest extends OteEventMessage {
   public static final int _BYTE_SIZE = 250;
   public static final String _TOPIC = "ote/server/FileAvailableRequest";
   public static final int _MESSAGE_ID = -1;

   public final StringElement FILE;

   public FileAvailableRequest() {
      super(FileAvailableRequest.class.getSimpleName(), _TOPIC, _BYTE_SIZE);

      int currentOffset = 0;
      int bitLength;
      FILE = new StringElement(this, "FILE", getDefaultMessageData(), currentOffset, bitLength=(8*250)); currentOffset+=bitLength;

      if (currentOffset > _BYTE_SIZE*8) {
         throw new IllegalStateException("Total size of elements exceeds defined message size");
      }
   }

}
