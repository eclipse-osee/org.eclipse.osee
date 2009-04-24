/*
 * Created on Feb 25, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;



/**
 * @author b1122182
 */
public interface MessageSendChannel {
   public void send(Message message, ExceptionHandler exceptionHandler);
   public void registerMessageSendHandler(MessageId messageId, SendHandler... sendHandler);
   public void unregisterMessageSendHandler(MessageId messageId, SendHandler... sendHandler);
}
