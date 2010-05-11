/*
 * Created on Apr 5, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.Map;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.test.msg.TestMessage;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class BasicListener extends OseeMessagingListener {

   private int id;
   private boolean received = false;
   
   public BasicListener(int id) {
      this.id = id;
   }
   
   @Override
   public Class<?> getClazz() {
      return TestMessage.class;
   }
   
   @Override
   public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
      System.out.println(message + "  -  " + id);
      received = true;
   }

   public boolean isReceived(){
      return received;
   }
   
   public String toString(){
      return "BasicListener " + id;
   }
}
