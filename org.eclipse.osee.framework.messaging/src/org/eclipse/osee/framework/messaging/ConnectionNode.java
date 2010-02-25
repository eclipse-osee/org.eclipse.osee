/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author b1122182
 */
public interface ConnectionNode {

   public void subscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);
   public void unsubscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);

   public boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener);
   public boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener);
   
   public void send(MessageID topic, Object body, final OseeMessagingStatusCallback statusCallback) throws OseeCoreException;

   public void addConnectionListener(ConnectionListener connectionListener);
   public void removeConnectionListener(ConnectionListener connectionListener);

   public void stop();

   String getSummary();
   String getSubscribers();
   String getSenders();
}
