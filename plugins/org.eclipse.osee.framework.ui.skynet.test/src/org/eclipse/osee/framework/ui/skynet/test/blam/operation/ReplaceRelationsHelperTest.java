/*
 * Created on Jun 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.blam.operation;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.blam.operation.ReplaceRelationsHelper;
import org.junit.Assert;

/**
 * Test case for {@link ReplaceRelationsHelper}
 * 
 * @author Jeff C. Phillips
 */
public class ReplaceRelationsHelperTest {

   @org.junit.Test
   public void testAddArtifactGuidToAttrOrder() {
      String guid = GUID.create();
      String beforeGuid = GUID.create();
      String relationOrder = beforeGuid + "," + "123344443";
      Assert.assertTrue(ReplaceRelationsHelper.addArtifactGuidToOrder(guid, beforeGuid, relationOrder).contains(guid));
   }

   @org.junit.Test
   public void testAddArtifactGuidToAttrOrderFailure() {
      String guid = GUID.create();
      String beforeGuid = GUID.create();
      String relationOrder = "123344443";
      Assert.assertFalse(ReplaceRelationsHelper.addArtifactGuidToOrder(guid, beforeGuid, relationOrder).contains(guid));
   }

   @org.junit.Test
   public void testRemoveArtifactGuidFromAttrOrder() {
      String guid = GUID.create();
      String beforeGuid = GUID.create();
      String relationOrder = beforeGuid + "," + guid;
      Assert.assertFalse(ReplaceRelationsHelper.removeArtifactGuidFromRelationOrder(guid, relationOrder).contains(guid));
   }

   @org.junit.Test
   public void testRemoveArtifactGuidFromAttrOrderFailre() {
      String guid = GUID.create();
      String beforeGuid = GUID.create();
      String relationOrder = beforeGuid;
      Assert.assertTrue(true);
      ReplaceRelationsHelper.removeArtifactGuidFromRelationOrder(guid, relationOrder);
   }

}
