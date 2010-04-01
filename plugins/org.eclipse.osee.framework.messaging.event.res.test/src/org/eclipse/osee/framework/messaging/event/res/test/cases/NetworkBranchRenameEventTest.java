/*
 * Created on Mar 31, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.event.res.test.cases;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.res.event.BranchEventModificationType;
import org.eclipse.osee.framework.messaging.event.res.event.NetworkBranchRenameEvent;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class NetworkBranchRenameEventTest {

   public static String BRANCH_NAME = "New Branch Name";
   public static String SHORT_NAME = "Branch Short Name";

   @Test
   public void testNetworkBranchRenameEvent() {
      String branchGuid = GUID.create();
      NetworkBranchRenameEvent event =
            new NetworkBranchRenameEvent(branchGuid, BRANCH_NAME, SHORT_NAME, NetworkSenderTest.networkSender);

      Assert.assertNotNull(event);
      Assert.assertEquals(BranchEventModificationType.Renamed, event.getModType());
      Assert.assertEquals(branchGuid, event.getBranchGuid());
      Assert.assertEquals(BRANCH_NAME, event.getBranchName());
      Assert.assertEquals(SHORT_NAME, event.getShortName());
      Assert.assertEquals(NetworkSenderTest.networkSender, event.getNetworkSender());

      NetworkBranchRenameEvent newEvent = new NetworkBranchRenameEvent(event.toXml());
      Assert.assertNotNull(newEvent);
      Assert.assertEquals(BranchEventModificationType.Renamed, newEvent.getModType());
      Assert.assertEquals(BRANCH_NAME, newEvent.getBranchName());
      Assert.assertEquals(SHORT_NAME, newEvent.getShortName());
      Assert.assertEquals(branchGuid, newEvent.getBranchGuid());
      Assert.assertEquals(NetworkSenderTest.networkSender, newEvent.getNetworkSender());

   }

}
