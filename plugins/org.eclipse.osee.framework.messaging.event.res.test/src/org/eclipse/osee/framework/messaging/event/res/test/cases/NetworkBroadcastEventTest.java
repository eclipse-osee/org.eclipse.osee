/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkBroadcastEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NetworkBroadcastEventTest {

   public static String EVENT_TYPE = "Event Type";
   public static List<String> USER_IDS = Arrays.asList("23423", "Don Dunne", "hello world");
   public static String MESSAGE = "Now is the ; time for < all good () men to come";

   public static NetworkBroadcastEvent event =
         new NetworkBroadcastEvent(EVENT_TYPE, MESSAGE, USER_IDS, NetworkSenderTest.networkSender);

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkBroadcastEvent#NetworkBroadcastEvent(org.eclipse.osee.framework.messaging.event.res.event.NetworkSender, java.util.List)}
    * .
    */
   @Test
   public void testNetworkBroadcastEventNetworkSender() {
      Assert.assertNotNull(event);
      Assert.assertEquals(EVENT_TYPE, event.getBroadcastEventTypeName());
      Assert.assertEquals(MESSAGE, event.getMessage());
      Assert.assertEquals(USER_IDS, event.getUserIds());
      Assert.assertEquals(NetworkSenderTest.networkSender, event.getNetworkSender());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.messaging.event.res.event.NetworkBroadcastEvent#NetworkBroadcastEvent(java.lang.String)}
    * .
    */
   @Test
   public void testNetworkBroadcastEventString() {
      NetworkBroadcastEvent newEvent = new NetworkBroadcastEvent(event.toXml());
      Assert.assertEquals(EVENT_TYPE, newEvent.getBroadcastEventTypeName());
      Assert.assertEquals(MESSAGE, newEvent.getMessage());
      Assert.assertEquals(USER_IDS, newEvent.getUserIds());
      Assert.assertEquals(NetworkSenderTest.networkSender, newEvent.getNetworkSender());
   }

}
