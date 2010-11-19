/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Date;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.AssigneeColumn;
import org.eclipse.osee.ats.column.CompletedDateColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.TeamState;
import org.eclipse.osee.ats.util.TransitionOption;
import org.eclipse.osee.ats.workflow.TransitionManager;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests CompletedDateColumn
 * @author Donald G Dunne
 */
public class CompletedDateColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(CompletedDateColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CompletedDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(CompletedDateColumnTest.class.getSimpleName(), transaction);
      transaction.execute();

      Assert.assertEquals("", CompletedDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));
      Date date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CompletedDateColumn.getDateStr(teamArt));

      TransitionManager transitionMgr = new TransitionManager(teamArt);
      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), CompletedDateColumnTest.class.getSimpleName());
      transitionMgr.transitionToCompleted("reason", transaction, TransitionOption.OverrideTransitionValidityCheck);
      transaction.execute();

      date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CompletedDateColumn.getDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CompletedDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), CompletedDateColumnTest.class.getSimpleName());
      transitionMgr.transition(TeamState.Endorse, UserManager.getUser(), transaction,
         TransitionOption.OverrideTransitionValidityCheck);
      transaction.execute();

      Assert.assertEquals("Cancelled date should be blank again", "",
         CompletedDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));
      date = CompletedDateColumn.getDate(teamArt);
      Assert.assertNull(date);

   }
}
