/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.concurrent.ExecutorService;

/**
 * @author b1528444
 *
 */
public interface ReceiveChannelHandler {
   public void start(ExecutorService executor, ReceiveListenerStore store);
   public void addRecieveListener(MessageId messageId, ReceiveListener recieveListener);
}
