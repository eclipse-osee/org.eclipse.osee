/*
 * Created on Jun 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.test.blam.operation;

import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.ui.skynet.replace.ReplaceUtil;
import org.junit.Assert;

/**
 * Test case for {@link ReplaceUtil}
 * 
 * @author Jeff C. Phillips
 */
public class ReplaceRelationsHelperTest {

   @org.junit.Test
   public void testAddArtifactGuidToAttrOrder() {
      String guid = GUID.create();
      String beforeGuid = "AY0qOOc1ayvBA5_UErAA";
      String relationOrder =
         "<OrderList><Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AY0qOOc1ayvBA5_UErAA\"/></OrderList>";
      String result = ReplaceUtil.addArtifactGuidBeforeToRelationOrder(guid, beforeGuid, relationOrder);
      Assert.assertTrue(result.contains(guid));
      System.out.println(result);
   }

   //   @org.junit.Test
   //   public void testAddArtifactGuidToAttrOrderFailure() {
   //      String guid = GUID.create();
   //      String beforeGuid = GUID.create();
   //      String relationOrder = "123344443";
   //      Assert.assertFalse(ReplaceUtil.addArtifactGuidBeforeToRelationOrder(guid, beforeGuid, relationOrder).contains(
   //         guid));
   //   }
   //
   @org.junit.Test
   public void testRemoveArtifactGuidFromAttrOrder() {
      String beforeGuid = "AY0qOOc1ayvBA5_UErAA";
      String relationOrder =
         "<OrderList><Order relType=\"Default Hierarchical\" side=\"SIDE_B\" orderType=\"AAT0xogoMjMBhARkBZQA\" list=\"AY0qOOc1ayvBA5_UErAA\"/></OrderList>";
      String result = ReplaceUtil.removeArtifactGuidFromRelationOrder(beforeGuid, relationOrder);
      Assert.assertFalse(result.contains(beforeGuid));
      System.out.println(result);
   }
   //
   //   @org.junit.Test
   //   public void testRemoveArtifactGuidFromAttrOrderStartComma() {
   //      String guid = GUID.create();
   //      String beforeGuid = GUID.create();
   //      String relationOrder = "," + beforeGuid + guid;
   //      String returnString = ReplaceUtil.removeArtifactGuidFromRelationOrder(guid, relationOrder);
   //      Assert.assertFalse(returnString.contains(guid));
   //      Assert.assertTrue(returnString.equals(beforeGuid));
   //   }
   //
   //   @org.junit.Test
   //   public void testRemoveArtifactGuidFromAttrOrderExtraMiddleComma() {
   //      String guid = GUID.create();
   //      String beforeGuid = GUID.create();
   //      String endGuid = GUID.create();
   //      String relationOrder = beforeGuid + guid + ", ," + endGuid;
   //      String returnString = ReplaceUtil.removeArtifactGuidFromRelationOrder(guid, relationOrder);
   //      Assert.assertFalse(returnString.contains(guid));
   //      Assert.assertTrue(returnString.equals(beforeGuid + "," + endGuid));
   //   }
   //
   //   @org.junit.Test
   //   public void testRemoveArtifactGuidFromAttrOrderEndComma() {
   //      String guid = GUID.create();
   //      String beforeGuid = GUID.create();
   //      String relationOrder = beforeGuid + "," + guid + ",";
   //      String returnString = ReplaceUtil.removeArtifactGuidFromRelationOrder(guid, relationOrder);
   //      Assert.assertFalse(returnString.contains(guid));
   //      Assert.assertTrue(returnString.equals(beforeGuid));
   //   }
   //
   //   @org.junit.Test
   //   public void testGetPreviousArtifactGuiOrder() {
   //      String guid = GUID.create();
   //      String beforeGuid = GUID.create();
   //      String relationOrder = beforeGuid + "," + guid + ",";
   //      String returnString = ReplaceUtil.getBeforeOrderGuid(relationOrder, guid).getFirst();
   //      Assert.assertTrue(returnString.equals(beforeGuid));
   //   }
}
