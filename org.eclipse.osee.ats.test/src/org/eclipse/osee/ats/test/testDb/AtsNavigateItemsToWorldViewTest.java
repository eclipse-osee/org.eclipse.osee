/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDbGroups;
import org.eclipse.osee.ats.config.demo.config.DemoDbTasks;
import org.eclipse.osee.ats.config.demo.util.DemoUsers;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.ats.world.search.StateWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToWorldViewTest extends TestCase {

   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
      assertTrue(DemoUsers.getDemoUser(DemoUsers.Kay_Jones) != null);
   }

   public void testMyWorld() throws Exception {
      runGeneralLoadingTest("My World", ActionArtifact.class, 6, null);
   }

   public void testMyFavoritesAndMyRecentlyVisited() throws Exception {
      // Load My Favorites (test My Favorites and use results to test My Recently Visited
      Collection<Artifact> arts = runGeneralLoadingTest("My Favorites", TeamWorkFlowArtifact.class, 3, null);
      assertTrue(arts.size() == 3);
      // Open all three favorites
      for (Artifact artifact : arts)
         SMAEditor.editArtifact(artifact);
      // Test that recently visited returns all three
      runGeneralLoadingTest("My Recently Visited", TeamWorkFlowArtifact.class, 3, null);
   }

   public void testMyReviews() throws Exception {
      runGeneralLoadingTest("My Reviews", PeerToPeerReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Reviews", DecisionReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Reviews - All", PeerToPeerReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Reviews - All", DecisionReviewArtifact.class, 3, null);
   }

   public void testMySubscribed() throws Exception {
      runGeneralLoadingTest("My Subscribed", TeamWorkFlowArtifact.class, 1, null);
   }

   public void testMyOriginator() throws Exception {
      runGeneralLoadingTest("My Originator - InWork", TaskArtifact.class, DemoDbTasks.getNumTasks(), null);
      runGeneralLoadingTest("My Originator - InWork", TeamWorkFlowArtifact.class, 18, null);
      runGeneralLoadingTest("My Originator - InWork", PeerToPeerReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - InWork", DecisionReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - All", TaskArtifact.class, DemoDbTasks.getNumTasks(), null);
      runGeneralLoadingTest("My Originator - All", TeamWorkFlowArtifact.class, 25, null);
      runGeneralLoadingTest("My Originator - All", PeerToPeerReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - All", DecisionReviewArtifact.class, 8, null);
   }

   public void testMyCompleted() throws Exception {
      runGeneralLoadingTest("My Completed", TeamWorkFlowArtifact.class, 7, null);
      runGeneralLoadingTest("My Completed", PeerToPeerReviewArtifact.class, 1, null);
      runGeneralLoadingTest("My Completed", DecisionReviewArtifact.class, 1, null);
   }

   public void testMyRecentlyVisited() throws Exception {
      // Load Recently Visited
      runGeneralLoadingTest("My Recently Visited", TeamWorkFlowArtifact.class, 3, null);
   }

   public void testOtherUsersWorld() throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO,
            "Testing User's items relating to " + DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("User's World").iterator().next();
      runGeneralLoadingTest(item, ActionArtifact.class, 4, DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testOtherUsersReviews() throws Exception {
      runGeneralLoadingTest("User's Reviews - InWork", PeerToPeerReviewArtifact.class, 1,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
      runGeneralLoadingTest("User's Reviews - All", PeerToPeerReviewArtifact.class, 2,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testOtherUsersSubscribed() throws Exception {
      runGeneralLoadingTest("User's Subscribed", TeamWorkFlowArtifact.class, 0,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testOtherUsersFavorites() throws Exception {
      runGeneralLoadingTest("User's Favorites", TeamWorkFlowArtifact.class, 0,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testOtherUsersOriginator() throws Exception {
      runGeneralLoadingTest("User's Originator - InWork", PeerToPeerReviewArtifact.class, 0,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
      runGeneralLoadingTest("User's Originator - All", PeerToPeerReviewArtifact.class, 1,
            DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testOtherUsersCompleted() throws Exception {
      runGeneralLoadingTest("User's Completed", ActionArtifact.class, 0, DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
   }

   public void testGroupsSearch() throws Exception {
      WorldEditor.closeAll();
      Artifact groupArt =
            ArtifactQuery.getArtifactFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME, DemoDbGroups.TEST_GROUP_NAME,
                  AtsPlugin.getAtsBranch());
      assertTrue(groupArt != null);
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Group Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof GroupWorldSearchItem);
      ((GroupWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedGroup(groupArt);
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      WorldEditor worldEditor = getSingleEditorOrFail();
      Collection<Artifact> arts = worldEditor.getLoadedArtifacts();

      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Actions", arts, ActionArtifact.class, 2);
      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Teams", arts, TeamWorkFlowArtifact.class, 7);
      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Tasks", arts, TaskArtifact.class,
            DemoDbTasks.getNumTasks());
   }

   public void testUserCommunitySearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("User Community Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof UserCommunitySearchItem);
      ((UserCommunitySearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUserComm("Program 2");
      // normal searches copy search item which would clear out the set value above; for this test, don't copy item
      runGeneralLoadingTest(item, ActionArtifact.class, 6, null, TableLoadOption.DontCopySearchItem);
   }

   public void testActionableItemSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Actionable Item Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ActionableItemWorldSearchItem);
      ((ActionableItemWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedActionItems(ActionableItemArtifact.getActionableItems(Arrays.asList("SAW Code")));
      // normal searches copy search item which would clear out the set value above; for this test, don't copy item
      runGeneralLoadingTest(item, ActionArtifact.class, 3, null, TableLoadOption.DontCopySearchItem);
   }

   public void testTargetedForVersionTeamSearch() throws Exception {
      List<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof VersionTargetedForTeamSearchItem);
      ((VersionTargetedForTeamSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedVersionArt(VersionArtifact.getVersions(
            Arrays.asList("SAW_Bld_2")).iterator().next());
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 14, null, TableLoadOption.DontCopySearchItem);
   }

   public void testTargetedForTeamSearch() throws Exception {
      List<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Next Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof NextVersionSearchItem);
      ((NextVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDef(TeamDefinitionArtifact.getTeamDefinitions(
            Arrays.asList("SAW SW")).iterator().next());
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 14, null, TableLoadOption.DontCopySearchItem);
   }

   public void testShowOpenDecisionReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Open Decision Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, DecisionReviewArtifact.class, 7);
   }

   public void testShowWorkflowsWaitingForDecisionReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Workflows Waiting Decision Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 7);
   }

   public void testShowOpenPeerToPeerReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Open PeerToPeer Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, PeerToPeerReviewArtifact.class, 7);
   }

   public void testShowWorkflowsWaitingForPeerToPeerReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Workflows Waiting PeerToPeer Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 6);
   }

   public void testSearchByCurrentState() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Search by Current State");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof StateWorldSearchItem);
      ((StateWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedStateClass("Implement");
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 17, null, TableLoadOption.DontCopySearchItem);
   }

   public void testSearchForAuthorizeActions() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Search for Authorize Actions");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof StateWorldSearchItem);
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 0);
   }

   public Collection<Artifact> runGeneralLoadingTest(String xNavigateItemName, Class<?> clazz, int numOfType, User user) throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem(xNavigateItemName);
      return runGeneralLoadingTest(item, clazz, numOfType, user);
   }

   public Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, Class<?> clazz, int numOfType) throws Exception {
      return runGeneralLoadingTest(item, clazz, numOfType, null);
   }

   public Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, Class<?> clazz, int numOfType, User user) throws Exception {
      return runGeneralLoadingTest(item, clazz, numOfType, user, TableLoadOption.None);
   }

   public Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, Class<?> clazz, int numOfType, User user, TableLoadOption tableLoadOption) throws Exception {
      // Close all open world editors
      WorldEditor.closeAll();
      // Find the correct navigate item
      if (user != null && (item instanceof SearchNavigateItem)) {
         if (((SearchNavigateItem) item).getWorldSearchItem() instanceof UserSearchItem) {
            ((UserSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUser(user);
         }
      }
      // Simulate double-click of navigate item
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI,
            tableLoadOption);
      WorldEditor worldEditor = getSingleEditorOrFail();
      Collection<Artifact> arts = worldEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, clazz, numOfType);
      return arts;
   }

   public WorldEditor getSingleEditorOrFail() throws OseeCoreException {
      // Retrieve results from opened editor and test
      Collection<WorldEditor> editors = WorldEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);

      return editors.iterator().next();
   }
}
