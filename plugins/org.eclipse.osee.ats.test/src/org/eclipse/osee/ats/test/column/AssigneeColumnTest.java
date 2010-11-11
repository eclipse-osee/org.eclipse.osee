/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ActionableItemsColumn;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.support.test.util.DemoWorkType;

/**
 * @tests AssigneeColumn
 * @author Donald G Dunne
 */
public class AssigneeColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      TeamWorkFlowArtifact codeArt = (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("Joe Smith",
         AssigneeColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals("Kay Jones; Joe Smith",
         AssigneeColumn.getInstance().getColumnText(actionArt, AssigneeColumn.getInstance(), 0));

   }

   public void testGetColumnImage() throws Exception {
      TeamWorkFlowArtifact codeArt = (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(codeArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(actionArt, AssigneeColumn.getInstance(),
         0));

      Assert.assertNull(ActionableItemsColumn.getInstance().getColumnImage("String", AssigneeColumn.getInstance(), 0));
   }
}
