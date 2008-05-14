/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions.DemoUsers;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamVersionWorldSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
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

   private Map<String, XNavigateItem> nameToNavItem = new HashMap<String, XNavigateItem>();

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
      // This test should only be run on test db
      assertFalse(AtsPlugin.isProductionDb());
      // Confirm test setup with demo data
      assertTrue(PopulateDemoActions.isDbPopulatedWithDemoData().isTrue());
      // Setup hash if navigate items to names
      for (XNavigateItem item : AtsNavigateViewItems.getInstance().getSearchNavigateItems())
         createNameToNavItemMap(item, nameToNavItem);
      // Confirm user is Joe Smith
      assertTrue(SkynetAuthentication.getUser().getUserId().equals("Joe Smith"));
   }

   public void testMySearches() throws Exception {
      runGeneralLoadingTest("My World", ActionArtifact.class, 8, null);
      runGeneralLoadingTest("My Reviews", PeerToPeerReviewArtifact.class, 2, null);
      // TODO Transition Review to Completed and another to Cancelled and test My Reviews - All returns them
      runGeneralLoadingTest("My Reviews - All", PeerToPeerReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Subscribed", TeamWorkFlowArtifact.class, 1, null);
      runGeneralLoadingTest("My Team Workflows", TeamWorkFlowArtifact.class, 11, null);
      runGeneralLoadingTest("My Task (WorldView)", TaskArtifact.class, PopulateDemoActions.getNumTasks(), null);
      // TODO Add test for and My Task (Editor)
      runGeneralLoadingTest("My Originator - InWork", ActionArtifact.class, 9, null);
      runGeneralLoadingTest("My Originator - All", ActionArtifact.class, 16, null);
      runGeneralLoadingTest("My Completed", TeamWorkFlowArtifact.class, 23, null);
      // Load My Favorites (test My Favorites and use results to test My Recently Visited
      Collection<Artifact> arts = runGeneralLoadingTest("My Favorites", TeamWorkFlowArtifact.class, 3, null);
      assertTrue(arts.size() == 3);
      for (Artifact artifact : arts)
         SMAEditor.editArtifact(artifact);
      // Clear WorldView
      WorldView.loadIt("", new ArrayList<Artifact>(), TableLoadOption.ForcePend);
      assertTrue(WorldView.getLoadedArtifacts().size() == 0);
      // Load Recently Visited
      runGeneralLoadingTest("My Recently Visited", TeamWorkFlowArtifact.class, 3, null);
   }

   public void testOtherUsersSearches() throws Exception {
      User kayJones = PopulateDemoActions.getDemoUser(DemoUsers.Kay_Jones);
      assertTrue(kayJones != null);
      runGeneralLoadingTest("User's World", ActionArtifact.class, 5, kayJones);
      runGeneralLoadingTest("User's Reviews - InWork", PeerToPeerReviewArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Reviews - All", PeerToPeerReviewArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Subscribed", TeamWorkFlowArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Tasks (WorldView)", TaskArtifact.class,
            PopulateDemoActions.getTaskTitles(true).size(), kayJones);
      runGeneralLoadingTest("User's Favorites", TeamWorkFlowArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Team Workflows", TeamWorkFlowArtifact.class, 8, kayJones);
      runGeneralLoadingTest("User's Originator - InWork", ActionArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Originator - All", ActionArtifact.class, 0, kayJones);
      runGeneralLoadingTest("User's Completed", ActionArtifact.class, 0, kayJones);
   }

   public void testGroupsSearch() throws Exception {
      Artifact groupArt =
            ArtifactQuery.getArtifactFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME,
                  PopulateDemoActions.TEST_GROUP_NAME, AtsPlugin.getAtsBranch());
      assertTrue(groupArt != null);
      XNavigateItem item = nameToNavItem.get("Groups Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof GroupWorldSearchItem);
      ((GroupWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedGroup(groupArt);
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();

      testExpectedVersusActual(item.getName(), arts, ActionArtifact.class, 2);
      testExpectedVersusActual(item.getName(), arts, TeamWorkFlowArtifact.class, 4);
      testExpectedVersusActual(item.getName(), arts, TaskArtifact.class, PopulateDemoActions.getNumTasks());
   }

   public void testUserCommunitySearch() throws Exception {
      XNavigateItem item = nameToNavItem.get("User Community Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof UserCommunitySearchItem);
      ((UserCommunitySearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUserComm("Program 2");
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      testExpectedVersusActual(item.getName(), arts, ActionArtifact.class, 6);
   }

   public void testActionableItemSearch() throws Exception {
      XNavigateItem item = nameToNavItem.get("Actionable Item Actions");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ActionableItemWorldSearchItem);
      ((ActionableItemWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedActionItems(ActionableItemArtifact.getActionableItems(Arrays.asList(new String[] {"SAW Code"})));
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      testExpectedVersusActual(item.getName(), arts, ActionArtifact.class, 7);
   }

   public void testTeamDefinitionSearch() throws Exception {
      XNavigateItem item = nameToNavItem.get("Team Actions");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TeamWorldSearchItem);
      ((TeamWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDefs(TeamDefinitionArtifact.getTeamDefinitions(Arrays.asList(new String[] {"SAW Code"})));
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      testExpectedVersusActual(item.getName(), arts, ActionArtifact.class, 7);
   }

   public void testTeamDefinitionByVersionSearch() throws Exception {
      XNavigateItem item = nameToNavItem.get("Team Actions by Version");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TeamVersionWorldSearchItem);
      ((TeamVersionWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDefs(TeamDefinitionArtifact.getTeamDefinitions(Arrays.asList(new String[] {"SAW Code"})));
      ((TeamVersionWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedVersion(VersionArtifact.getVersions(
            Arrays.asList(new String[] {"SAW_Bld_2"})).iterator().next());
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      testExpectedVersusActual(item.getName(), arts, TeamWorkFlowArtifact.class, 6);
   }

   // TODO Add test for "Teams"-"Show Team Versions"

   public Collection<Artifact> runGeneralLoadingTest(String xNavigateItemName, Class<?> clazz, int numOfType, User user) throws Exception {
      XNavigateItem item = nameToNavItem.get(xNavigateItemName);
      if (user != null && (item instanceof SearchNavigateItem)) {
         if (((SearchNavigateItem) item).getWorldSearchItem() instanceof UserSearchItem) {
            ((UserSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUser(user);
         }
      }
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      Collection<Artifact> arts = WorldView.getLoadedArtifacts();
      testExpectedVersusActual(xNavigateItemName, arts, clazz, numOfType);
      return WorldView.getLoadedArtifacts();
   }

   public void testExpectedVersusActual(String name, Collection<? extends Artifact> arts, Class<?> clazz, int expectedNumOfType) {
      int actualNumOfType = numOfType(arts, clazz);
      String expectedStr = "\"" + name + "\"   Expected: " + expectedNumOfType + "   Found: " + actualNumOfType;
      if (expectedNumOfType != actualNumOfType)
         System.err.println(expectedStr);
      else
         System.out.println(expectedStr);
      assertTrue(actualNumOfType == expectedNumOfType);
   }

   public int numOfType(Collection<? extends Artifact> arts, Class<?> clazz) {
      int num = 0;
      for (Artifact art : arts)
         if (clazz.isAssignableFrom(art.getClass())) num++;
      return num;
   }

   public void createNameToNavItemMap(XNavigateItem item, Map<String, XNavigateItem> nameToItemMap) {
      nameToItemMap.put(item.getName(), item);
      for (XNavigateItem child : item.getChildren()) {
         createNameToNavItemMap(child, nameToItemMap);
      }
   }

}
