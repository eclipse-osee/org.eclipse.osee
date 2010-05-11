/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.util.Properties;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ConnectionNode {

   void subscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);
   void subscribe(MessageID messageId, OseeMessagingListener listener, String selector, final OseeMessagingStatusCallback statusCallback);
   void unsubscribe(MessageID messageId, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);

   boolean subscribeToReply(MessageID messageId, OseeMessagingListener listener);
   boolean unsubscribteToReply(MessageID messageId, OseeMessagingListener listener);
   
   void send(MessageID topic, Object body, final OseeMessagingStatusCallback statusCallback) throws OseeCoreException;
   void send(MessageID topic, Object body, Properties properties, OseeMessagingStatusCallback statusCallback) throws OseeCoreException;
   
   void addConnectionListener(ConnectionListener connectionListener);
   void removeConnectionListener(ConnectionListener connectionListener);

   void stop();

   String getSummary();
   String getSubscribers();
   String getSenders();

}
