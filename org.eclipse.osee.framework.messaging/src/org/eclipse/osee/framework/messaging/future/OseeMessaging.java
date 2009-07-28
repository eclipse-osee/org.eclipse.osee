/*
 * Created on Jul 27, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.future;


/**
 * @author b1528444
 *
 */
public interface OseeMessaging {
   
   public void addListener(Component component, String topic, OseeMessagingListener listener) throws Exception;
   
   public void sendMessage(Component component, String topic, Object body);
   
}
