/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.integration.tests.ats.column;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.InjectionActivity;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.ide.column.ReviewAuthorColumn;
import org.eclipse.osee.ats.ide.column.ReviewModeratorColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumIssuesColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumMajorDefectsColumn;
import org.eclipse.osee.ats.ide.column.ReviewNumMinorDefectsColumn;
import org.eclipse.osee.ats.ide.column.ReviewReviewerColumn;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.ide.workflow.review.defect.ReviewDefectManager;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests CancelledDateColumn
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewColumnsTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(PeerToPeerReviewColumnsTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(PeerToPeerReviewColumnsTest.class.getSimpleName());

      TeamWorkFlowArtifact teamArt =
         (TeamWorkFlowArtifact) DemoTestUtil.createSimpleAction(PeerToPeerReviewColumnsTest.class.getSimpleName(),
            changes).getStoreObject();
      PeerToPeerReviewArtifact peerArt = PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt,
         getClass().getSimpleName(), teamArt.getStateMgr().getCurrentStateName(), changes);
      changes.add(peerArt);
      changes.execute();

      Assert.assertEquals("0", ReviewNumIssuesColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("0", ReviewNumMajorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("0", ReviewNumMinorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewAuthorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewModeratorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("", ReviewReviewerColumn.getInstance().getColumnText(peerArt, null, 0));

      changes.clear();
      ReviewDefectItem item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(),
         Severity.Issue, Disposition.None, InjectionActivity.Code, "description", "resolution", "location", new Date());
      ReviewDefectManager defectManager = new ReviewDefectManager(peerArt);
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Issue,
         Disposition.None, InjectionActivity.Code, "description 2", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Issue,
         Disposition.None, InjectionActivity.Code, "description 3", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Issue,
         Disposition.None, InjectionActivity.Code, "description 34", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Major,
         Disposition.None, InjectionActivity.Code, "description 4", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Minor,
         Disposition.None, InjectionActivity.Code, "description 5", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Minor,
         Disposition.None, InjectionActivity.Code, "description 6", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      item = new ReviewDefectItem(AtsClientService.get().getUserService().getCurrentUser(), Severity.Minor,
         Disposition.None, InjectionActivity.Code, "description 6", "resolution", "location", new Date());
      defectManager.addOrUpdateDefectItem(item);
      defectManager.saveToArtifact(peerArt);

      UserRole role =
         new UserRole(Role.Author, AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Alex_Kay));
      IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) peerArt).getRoleManager();
      roleMgr.addOrUpdateUserRole(role);

      role = new UserRole(Role.Moderator,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Jason_Michael));
      roleMgr.addOrUpdateUserRole(role);

      role = new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      roleMgr.addOrUpdateUserRole(role);
      role = new UserRole(Role.Reviewer,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      roleMgr.addOrUpdateUserRole(role);
      roleMgr.saveToArtifact(changes);
      changes.add(peerArt);
      changes.execute();

      Assert.assertEquals("4", ReviewNumIssuesColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("1", ReviewNumMajorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals("3", ReviewNumMinorDefectsColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals(DemoUsers.Alex_Kay.getName(),
         ReviewAuthorColumn.getInstance().getColumnText(peerArt, null, 0));
      Assert.assertEquals(DemoUsers.Jason_Michael.getName(),
         ReviewModeratorColumn.getInstance().getColumnText(peerArt, null, 0));
      List<String> results = Arrays.asList(DemoUsers.Kay_Jones_And_Joe_Smith, DemoUsers.Joe_Smith_And_Kay_Jones);
      Assert.assertTrue(results.contains(ReviewReviewerColumn.getInstance().getColumnText(peerArt, null, 0)));

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
