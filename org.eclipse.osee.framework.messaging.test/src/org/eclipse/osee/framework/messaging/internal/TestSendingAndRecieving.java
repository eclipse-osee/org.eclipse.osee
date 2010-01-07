/*
 * Created on Jul 30, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.eclipse.osee.framework.messaging.Component;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;


/**
 * @author b1528444
 */
public class TestSendingAndRecieving extends BaseMessagingTesting {

   private static String JMS_TOPIC = "topic:test.topic.Mynewthing.removeme";
   private static String VM_TOPIC = "inThisJVM";
   private final int messagesToSend = 10;
   private int messagesReceived = 0;
   
   @org.junit.Test
   public void testSendingAndRecievingUsingJMS() {
	  startBroker();
      try {
         messagesReceived = 0;
         MessageStatusTest status1 = new MessageStatusTest(true);
         getMessaging().addListener(Component.JMS, JMS_TOPIC, new OseeMessagingListener() {
            public void process(Properties e) {
               messagesReceived++;
            }
         }, status1);
         status1.waitForStatus(500);
         Thread sending = new Thread(new Runnable() {
            @Override
            public void run() {
               for (int i = 0; i < messagesToSend; i++) {
                	  MessageStatusTest status2 = new MessageStatusTest(true);
                	  getMessaging().sendMessage(Component.JMS, JMS_TOPIC, "TestMessage " + i, status2);
                	  status2.waitForStatus(500);
               }
            }
         });
         sending.start();
         Thread.sleep(10000);

         assertTrue(String.format("sent[%d] != recieved[%d]", messagesToSend, messagesReceived),
               messagesToSend == messagesReceived);

      } catch (Exception ex) {
    	 fail(ex.getMessage());
      } 
      stopBroker();
   }

   @org.junit.Test
   public void testSendingAndRecievingUsingVM() {
      try {
         messagesReceived = 0;
         MessageStatusTest status1 = new MessageStatusTest(true);
         getMessaging().addListener(Component.VM, VM_TOPIC, new OseeMessagingListener() {
            public void process(Properties e) {
               messagesReceived++;
            }
         }, status1);
         Thread sending = new Thread(new Runnable() {
            @Override
            public void run() {
               for (int i = 0; i < messagesToSend; i++) {
                	  MessageStatusTest status2 = new MessageStatusTest(true);
                	  getMessaging().sendMessage(Component.VM, VM_TOPIC, "TestMessage " + i,status2);
                	  status2.waitForStatus(500);
               }
            }
         });
         sending.start();
         Thread.sleep(10000);

         assertTrue(String.format("sent[%d] != recieved[%d]", messagesToSend, messagesReceived),
               messagesToSend == messagesReceived);

      } catch (Exception ex) {
         assertFalse(ex.getMessage(), true);
      }
   }
}
