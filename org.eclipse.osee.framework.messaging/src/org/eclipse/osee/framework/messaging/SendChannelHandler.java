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
public interface SendChannelHandler {
   public void start(ExecutorService executor, SendListenerStore store);
   public void addSendListener(MessageId messageId, SendListener recieveListener);
}
