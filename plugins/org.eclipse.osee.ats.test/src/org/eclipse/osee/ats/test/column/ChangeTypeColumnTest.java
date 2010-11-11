/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ChangeTypeColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests ChangeTypeColumn
 * @author Donald G Dunne
 */
public class ChangeTypeColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      reqArt.setSoleAttributeValue(AtsAttributeTypes.ChangeType, ChangeType.Problem.name());
      reqArt.persist(transaction);
      transaction.execute();
   }

   @org.junit.Test
   public void getChangeTypeStrAndImage() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      Assert.assertEquals(ChangeType.Problem, ChangeTypeColumn.getChangeType(codeArt));
      Assert.assertNotNull(ChangeTypeColumn.getInstance().getColumnImage(codeArt, ChangeTypeColumn.getInstance(), 0));

      ActionArtifact actionArt = codeArt.getParentActionArtifact();
      Assert.assertEquals(ChangeType.Problem, ChangeTypeColumn.getChangeType(actionArt));

      // clear our req change type
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      ChangeTypeColumn.setChangeType(reqArt, ChangeType.None);
      reqArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals(ChangeType.None, ChangeTypeColumn.getChangeType(reqArt));
      Assert.assertNull(ChangeTypeColumn.getInstance().getColumnImage(reqArt, ChangeTypeColumn.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeColumn.getChangeType(actionArt));
      Assert.assertEquals("Problem",
         ChangeTypeColumn.getInstance().getColumnText(actionArt, ChangeTypeColumn.getInstance(), 0));

      // set change type to Improvement
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      ChangeTypeColumn.setChangeType(reqArt, ChangeType.Improvement);
      reqArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals(ChangeType.Improvement, ChangeTypeColumn.getChangeType(reqArt));
      Assert.assertNotNull(ChangeTypeColumn.getInstance().getColumnImage(reqArt, ChangeTypeColumn.getInstance(), 0));

      Assert.assertEquals(ChangeType.Problem, ChangeTypeColumn.getChangeType(actionArt));
      Assert.assertEquals("Problem; Improvement",
         ChangeTypeColumn.getInstance().getColumnText(actionArt, ChangeTypeColumn.getInstance(), 0));
   }

}
