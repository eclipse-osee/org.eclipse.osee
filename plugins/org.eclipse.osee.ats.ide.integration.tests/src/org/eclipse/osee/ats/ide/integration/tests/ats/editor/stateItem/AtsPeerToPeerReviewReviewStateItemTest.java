/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.api.review.ReviewRole;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.api.workdef.model.ReviewBlockType;
import org.eclipse.osee.ats.core.test.AtsTestUtilCore.AtsTestUtilState;
import org.eclipse.osee.ats.core.workflow.hooks.AtsPeerToPeerReviewReviewWorkItemHook;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.workflow.AtsTestUtil;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link AtsPeerToPeerReviewReviewWorkItemHook}
 *
 * @author Donald G. Dunne
 */
public class AtsPeerToPeerReviewReviewStateItemTest {

   public static PeerToPeerReviewArtifact peerRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsApiService.get().getStoreService().isProductionDb());
      if (peerRevArt == null) {
         AtsTestUtil.cleanupAndReset(getClass().getSimpleName());

         // setup fake review artifact with decision options set
         IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());
         peerRevArt = (PeerToPeerReviewArtifact) AtsTestUtil.getOrCreatePeerReview(ReviewBlockType.None,
            AtsTestUtilState.Analyze, changes);
         changes.execute();
      }
   }

   @AfterClass
   public static void testCleanup() throws Exception {
      AtsTestUtil.cleanup();
   }

   @Test
   public void testTransitioned() {
      Assert.assertNotNull(peerRevArt);

      // assignee should be user creating review
      Assert.assertEquals(1, peerRevArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getCurrentUser(),
         peerRevArt.getStateMgr().getAssignees().iterator().next());

      // set roles
      UserRole userRole = new UserRole(ReviewRole.Author, DemoUsers.Joe_Smith);
      IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) peerRevArt).getRoleManager();
      roleMgr.addOrUpdateUserRole(userRole);
      userRole = new UserRole(ReviewRole.Reviewer, DemoUsers.Alex_Kay);
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("test transition");
      roleMgr.addOrUpdateUserRole(userRole);
      roleMgr.saveToArtifact(changes);
      changes.execute();

      // assignee should be user roles
      Assert.assertEquals(2, peerRevArt.getStateMgr().getAssignees().size());

      // change assignees back to single user so can test transition
      peerRevArt.getStateMgr().setAssignee(AtsApiService.get().getUserService().getCurrentUser());
      peerRevArt.persist(getClass().getSimpleName());
      Assert.assertEquals(1, peerRevArt.getStateMgr().getAssignees().size());
      Assert.assertEquals(AtsApiService.get().getUserService().getCurrentUser(),
         peerRevArt.getStateMgr().getAssignees().iterator().next());

      IStateToken fromState = peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Prepare.getName());
      IStateToken toState = peerRevArt.getWorkDefinition().getStateByName(PeerToPeerReviewState.Review.getName());

      // make call to state item that should set options based on artifact's attribute value
      AtsPeerToPeerReviewReviewWorkItemHook stateItem = new AtsPeerToPeerReviewReviewWorkItemHook();
      changes.reset("test transition");
      stateItem.transitioned(peerRevArt, fromState, toState,
         Arrays.asList(AtsApiService.get().getUserService().getCurrentUser()),
         AtsApiService.get().getUserService().getCurrentUser(), changes, AtsApiService.get());
      changes.execute();

      // Joe and Alex should have been added to assignees
      Assert.assertEquals(2, peerRevArt.getStateMgr().getAssignees().size());
      boolean joeFound = false, alexFound = false;
      for (AtsUser user : peerRevArt.getStateMgr().getAssignees()) {
         if (user.getName().equals(DemoUsers.Joe_Smith.getName())) {
            joeFound = true;
         }
         if (user.getName().equals("Alex Kay")) {
            alexFound = true;
         }
      }
      Assert.assertTrue("Joe should have been added as assignee", joeFound);
      Assert.assertTrue("Alex should have been added as assignee", alexFound);
   }

}
