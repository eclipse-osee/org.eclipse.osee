package org.eclipse.osee.ote.endpoint;

import java.nio.ByteBuffer;

public interface EndpointDataProcessor {

   int getTypeId();

   void processBuffer(ByteBuffer buffer);

}
