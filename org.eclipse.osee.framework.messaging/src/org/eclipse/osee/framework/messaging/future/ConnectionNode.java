/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.future;

import org.eclipse.osee.framework.messaging.MessageName;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author b1122182
 */
public interface ConnectionNode {

   public void subscribe(MessageName topic, OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback);

   public void send(MessageName topic, Object body, final OseeMessagingStatusCallback statusCallback);

}
