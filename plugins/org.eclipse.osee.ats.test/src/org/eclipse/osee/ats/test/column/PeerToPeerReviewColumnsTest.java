/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.column;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.column.ReviewAuthorColumn;
import org.eclipse.osee.ats.column.ReviewModeratorColumn;
import org.eclipse.osee.ats.column.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.column.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.column.ReviewReviewerColumn;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.ReviewManager;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Disposition;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.InjectionActivity;
import org.eclipse.osee.ats.util.widgets.defect.DefectItem.Severity;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.junit.AfterClass;
import org.junit.BeforeClass;

/**
 * @tests CancelledDateColumn
 * @author Donald G Dunne
 */
public class PeerToPeerReviewColumnsTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      DemoTestUtil.cleanupSimpleTest(PeerToPeerReviewColumnsTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), PeerToPeerReviewColumnsTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(PeerToPeerReviewColumnsTest.class.getSimpleName(), transaction);
      PeerToPeerReviewArtifact peerArt =
         ReviewManager.createNewPeerToPeerReview(teamArt, getClass().getSimpleName(),
            teamArt.getStateMgr().getCurrentStateName(), transaction);
      peerArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("0", ReviewNumIssuesColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("0", ReviewNumMajorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("0", ReviewNumMinorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewAuthorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewModeratorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewReviewerColumn.getInstance().getColumnText(peerArt, null, 0));

      transaction = new SkynetTransaction(AtsUtil.getAtsBranch(), PeerToPeerReviewColumnsTest.class.getSimpleName());
      DefectItem item =
         new DefectItem(UserManager.getUser(), Severity.Issue, Disposition.None, InjectionActivity.Code, "description",
            "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Issue, Disposition.None, InjectionActivity.Code,
            "description 2", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Issue, Disposition.None, InjectionActivity.Code,
            "description 3", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Issue, Disposition.None, InjectionActivity.Code,
            "description 34", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Major, Disposition.None, InjectionActivity.Code,
            "description 4", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Minor, Disposition.None, InjectionActivity.Code,
            "description 5", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Minor, Disposition.None, InjectionActivity.Code,
            "description 6", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);
      item =
         new DefectItem(UserManager.getUser(), Severity.Minor, Disposition.None, InjectionActivity.Code,
            "description 6", "resolution", "location", new Date());
      peerArt.getDefectManager().addOrUpdateDefectItem(item, true, transaction);

      UserRole role = new UserRole(Role.Author, UserManager.getUser(DemoUsers.Alex_Kay));
      peerArt.getUserRoleManager().addOrUpdateUserRole(role, true, transaction);

      role = new UserRole(Role.Moderator, UserManager.getUser(DemoUsers.Jason_Michael));
      peerArt.getUserRoleManager().addOrUpdateUserRole(role, true, transaction);

      role = new UserRole(Role.Reviewer, UserManager.getUser(DemoUsers.Joe_Smith));
      peerArt.getUserRoleManager().addOrUpdateUserRole(role, true, transaction);
      role = new UserRole(Role.Reviewer, UserManager.getUser(DemoUsers.Kay_Jones));
      peerArt.getUserRoleManager().addOrUpdateUserRole(role, true, transaction);

      peerArt.persist(transaction);
      transaction.execute();

      Assert.assertEquals("4", ReviewNumIssuesColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("1", ReviewNumMajorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("3", ReviewNumMinorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals(DemoUsers.Alex_Kay.getName(),
         ReviewAuthorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals(DemoUsers.Jason_Michael.getName(),
         ReviewModeratorColumn.getInstance().getColumnText(peerArt, null, 0));
      List<String> results =
         Arrays.asList(DemoUsers.Kay_Jones.getName() + "; " + DemoUsers.Joe_Smith.getName(),
            DemoUsers.Joe_Smith.getName() + "; " + DemoUsers.Kay_Jones.getName());
      Assert.assertTrue(results.contains(ReviewReviewerColumn.getInstance().getColumnText(peerArt, null, 0)));
   }
}
