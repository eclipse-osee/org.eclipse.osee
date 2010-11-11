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
import org.eclipse.osee.ats.column.CategoryColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.DemoWorkType;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests CategoryColumn
 * @author Donald G Dunne
 */
public class CategoryColumnTest {

   @BeforeClass
   @AfterClass
   public static void cleanup() throws OseeCoreException {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      codeArt.deleteAttributes(AtsAttributeTypes.Category1);
      codeArt.persist(transaction);
      reqArt.deleteAttributes(AtsAttributeTypes.Category1);
      reqArt.persist(transaction);
      testArt.deleteAttributes(AtsAttributeTypes.Category1);
      testArt.persist(transaction);
      transaction.execute();
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      TeamWorkFlowArtifact codeArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Code);
      TeamWorkFlowArtifact reqArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Requirements);
      TeamWorkFlowArtifact testArt =
         (TeamWorkFlowArtifact) DemoTestUtil.getUncommittedActionWorkflow(DemoWorkType.Test);
      ActionArtifact actionArt = codeArt.getParentActionArtifact();

      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0));

      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      codeArt.addAttribute(AtsAttributeTypes.Category1, "this");
      codeArt.persist(transaction);
      reqArt.addAttribute(AtsAttributeTypes.Category1, "that");
      reqArt.persist(transaction);
      testArt.addAttribute(AtsAttributeTypes.Category1, "the other");
      testArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("this",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("that",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("the other",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));

      String actionArtStr =
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0);
      Assert.assertEquals(3, actionArtStr.split("; ").length);
      Assert.assertTrue(actionArtStr.contains("this"));
      Assert.assertTrue(actionArtStr.contains("that"));
      Assert.assertTrue(actionArtStr.contains("the other"));

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), CategoryColumnTest.class.getSimpleName());
      codeArt.deleteAttributes(AtsAttributeTypes.Category1);
      codeArt.persist(transaction);
      reqArt.deleteSoleAttribute(AtsAttributeTypes.Category1);
      reqArt.persist(transaction);
      testArt.deleteAttribute(AtsAttributeTypes.Category1, "the other");
      testArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(codeArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(reqArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(testArt, CategoryColumn.getCategory1Instance(), 0));
      Assert.assertEquals("",
         CategoryColumn.getCategory1Instance().getColumnText(actionArt, CategoryColumn.getCategory1Instance(), 0));

   }
}
