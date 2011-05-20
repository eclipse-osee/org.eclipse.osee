/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;

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

      Artifact actionArt = codeArt.getParentActionArtifact();
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

      Artifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertNotNull(ActionableItemsColumn.getInstance().getColumnImage(actionArt, AssigneeColumn.getInstance(),
         0));

      Assert.assertNull(ActionableItemsColumn.getInstance().getColumnImage("String", AssigneeColumn.getInstance(), 0));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
