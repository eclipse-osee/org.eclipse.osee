/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.res.event.BranchEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkBranchEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NetworkBranchEventTest {

   @Test
   public void testNetworkBroadcastEventNetworkSender() {
      for (BranchEventModificationType modType : BranchEventModificationType.getTypes()) {
         String branchGuid = GUID.create();
         NetworkBranchEvent event = new NetworkBranchEvent(modType, branchGuid, NetworkSenderTest.networkSender);

         Assert.assertNotNull(event);
         Assert.assertEquals(modType, event.getModType());
         Assert.assertEquals(branchGuid, event.getBranchGuid());
         Assert.assertEquals(NetworkSenderTest.networkSender, event.getNetworkSender());

         NetworkBranchEvent newEvent = new NetworkBranchEvent(event.toXml());
         Assert.assertNotNull(newEvent);
         Assert.assertEquals(modType, newEvent.getModType());
         Assert.assertEquals(branchGuid, newEvent.getBranchGuid());
         Assert.assertEquals(NetworkSenderTest.networkSender, newEvent.getNetworkSender());

      }
   }

}
