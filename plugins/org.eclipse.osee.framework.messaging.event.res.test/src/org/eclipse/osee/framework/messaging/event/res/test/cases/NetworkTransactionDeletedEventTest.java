/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkTransactionDeletedEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NetworkTransactionDeletedEventTest {

   public static List<Integer> transactionIds = Arrays.asList(1, 324, 543, 23);
   public static NetworkTransactionDeletedEvent event =
         new NetworkTransactionDeletedEvent(NetworkSenderTest.networkSender, transactionIds);

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkTransactionDeletedEvent#NetworkTransactionDeletedEvent(org.eclipse.osee.framework.messaging.event.res.event.NetworkSender, java.util.List)}
    * .
    */
   @Test
   public void testNetworkTransactionDeletedEventNetworkSenderListOfInteger() {
      Assert.assertNotNull(event);
      Assert.assertEquals(transactionIds, event.getTransactionIds());
      Assert.assertEquals(NetworkSenderTest.networkSender, event.getNetworkSender());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkTransactionDeletedEvent#NetworkTransactionDeletedEvent(java.lang.String)}
    * .
    */
   @Test
   public void testNetworkTransactionDeletedEventString() {
      NetworkTransactionDeletedEvent newEvent = new NetworkTransactionDeletedEvent(event.toXml());
      Assert.assertEquals(transactionIds, newEvent.getTransactionIds());
      Assert.assertEquals(NetworkSenderTest.networkSender, newEvent.getNetworkSender());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkTransactionDeletedEvent#getTransactionIds()}.
    */
   @Test
   public void testGetTransactionIds() {
      Assert.assertEquals(transactionIds, event.getTransactionIds());
   }

}
