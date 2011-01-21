/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Arrays;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ActionableItemsColumn;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @tests AssigneeColumn
 * @author Donald G. Dunne
 */
public class AssigneeColumnTest {

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals("Joe Smith",
         AssigneeColumn.getInstance().getColumnText(codeArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      List<String> results = Arrays.asList("Kay Jones; Joe Smith", "Joe Smith; Kay Jones");
      Assert.assertTrue(results.contains(AssigneeColumn.getInstance().getColumnText(actionArt,
         AssigneeColumn.getInstance(), 0)));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }

   public void testGetColumnImage() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(codeArt, AssigneeColumn.getInstance(), 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(actionArt, AssigneeColumn.getInstance(),
         0));

      Assert.assertNull(ActionableItemsColumn.getInstance().getColumnImage("String", AssigneeColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
