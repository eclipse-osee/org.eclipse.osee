/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ActionableItemsColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests ActionableItemsColumn
 * @author Donald G Dunne
 */
public class ActionableItemsColumnTest {

   @org.junit.Test
   public void testGetActionableItems() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Collection<ActionableItemArtifact> aias = ActionableItemsColumn.getActionableItems(codeArt);
      Assert.assertEquals(1, aias.size());
      Assert.assertEquals("SAW Code", aias.iterator().next().getName());

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      aias = ActionableItemsColumn.getActionableItems(actionArt);
      Assert.assertEquals(4, aias.size());

   }

   @org.junit.Test
   public void testGetActionableItemsStr() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("SAW Code", ActionableItemsColumn.getActionableItemsStr(codeArt));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();

      String results = ActionableItemsColumn.getActionableItemsStr(actionArt);
      Assert.assertTrue(results.contains("SAW Code"));
      Assert.assertTrue(results.contains("SAW SW Design"));
      Assert.assertTrue(results.contains("SAW Test"));
      Assert.assertTrue(results.contains("SAW Requirements"));
      Assert.assertEquals(4, results.split(", ").length);
   }

}
