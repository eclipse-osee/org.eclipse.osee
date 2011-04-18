/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.test.review;

import static org.junit.Assert.assertTrue;
import java.util.Collection;
import org.eclipse.osee.ats.navigate.AtsXNavigateItemLauncher;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.review.GenerateReviewParticipationReport;
import org.eclipse.osee.ats.review.ReviewNavigateViewItems;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.test.util.MassEditorUtil;
import org.eclipse.osee.ats.test.util.NavigateTestUtil;
import org.eclipse.osee.ats.test.util.WorldEditorUtil;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.support.test.util.DemoUsers;

/**
 * Test case for @link {@link ReviewNavigateViewItems}
 * 
 * @author Donald G. Dunne
 */
public class ReviewNavigateItemsToWorldViewTest {

   @org.junit.Test
   public void testDemoDatabase() throws Exception {
      VisitedItems.clearVisited();
      DemoTestUtil.setUpTest();
      assertTrue(DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones) != null);
   }

   @org.junit.Test
   public void testMyReviews() throws Exception {
      // two exist cause ATS has one in it's list
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("My Reviews").iterator().next();
      runGeneralLoadingTest(item, AtsArtifactTypes.DecisionReview, 2, null);
      runGeneralLoadingTest(item, AtsArtifactTypes.PeerToPeerReview, 2, null);
   }

   @org.junit.Test
   public void testOtherUsersWorld_KayJones() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("User's Reviews").iterator().next();
      runGeneralLoadingTest(item, AtsArtifactTypes.PeerToPeerReview, 1, DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
   public void testOtherUsersWorld_JoeSmith() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("User's Reviews").iterator().next();
      runGeneralLoadingTest(item, AtsArtifactTypes.DecisionReview, 2, DemoTestUtil.getDemoUser(DemoUsers.Joe_Smith));
      runGeneralLoadingTest(item, AtsArtifactTypes.PeerToPeerReview, 2, DemoTestUtil.getDemoUser(DemoUsers.Joe_Smith));
   }

   @org.junit.Test
   public void testOpenDecisionReviews() throws Exception {
      runGeneralLoadingTest("Show Open Decision Reviews", AtsArtifactTypes.DecisionReview, 7, null);
   }

   @org.junit.Test
   public void testWorkflowsWaitingDecisionReviews() throws Exception {
      runGeneralLoadingTest("Show Workflows Waiting Decision Reviews", AtsArtifactTypes.TeamWorkflow, 7, null);
   }

   @org.junit.Test
   public void testOpenPeerReviews() throws Exception {
      runGeneralLoadingTest("Show Open PeerToPeer Reviews", AtsArtifactTypes.PeerToPeerReview, 7, null);
   }

   @org.junit.Test
   public void testWorkflowsWaitingPeerToPeerReviews() throws Exception {
      runGeneralLoadingTest("Show Workflows Waiting PeerToPeer Reviews", AtsArtifactTypes.TeamWorkflow, 6, null);
   }

   @org.junit.Test
   public void testReviewParticipationReport() throws Exception {
      XNavigateItem item =
         NavigateTestUtil.getAtsNavigateItems("Generate Review Participation Report").iterator().next();
      ((GenerateReviewParticipationReport) item).setSelectedUser(UserManager.getUser(DemoUsers.Joe_Smith));

      item.run(TableLoadOption.ForcePend);

      MassArtifactEditor editor = MassEditorUtil.getSingleEditorOrFail();
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 2);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 3);
   }

   private Collection<Artifact> runGeneralLoadingTest(String xNavigateItemName, IArtifactType artifactType, int numOfType, User user) throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem(xNavigateItemName);
      return runGeneralLoadingTest(item, artifactType, numOfType, user);
   }

   private Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, IArtifactType artifactType, int numOfType) throws Exception {
      return runGeneralLoadingTest(item, artifactType, numOfType, null);
   }

   private Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, IArtifactType artifactType, int numOfType, User user) throws Exception {
      return runGeneralLoadingTest(item, artifactType, numOfType, user, TableLoadOption.None);
   }

   private Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, IArtifactType artifactType, int numOfType, User user, TableLoadOption tableLoadOption) throws Exception {
      WorldEditor.closeAll();
      // Find the correct navigate item
      if (user != null && item instanceof SearchNavigateItem) {
         if (((SearchNavigateItem) item).getWorldSearchItem() instanceof UserSearchItem) {
            ((UserSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedUser(user);
         }
      }
      // Simulate double-click of navigate item
      AtsXNavigateItemLauncher.handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI, tableLoadOption);

      WorldEditor worldEditor = WorldEditorUtil.getSingleEditorOrFail();
      Collection<Artifact> arts = worldEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, artifactType, numOfType);
      return arts;
   }

   private WorldXViewer getXViewer() {
      return WorldEditorUtil.getSingleEditorOrFail().getWorldComposite().getXViewer();
   }

}
