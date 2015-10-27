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
package org.eclipse.osee.ats.client.integration.tests.ats.navigate;

import static org.eclipse.osee.client.demo.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.client.demo.DemoBranches.SAW_Bld_2;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.client.demo.DemoArtifactToken;
import org.eclipse.osee.ats.client.demo.DemoArtifactTypes;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.ats.config.AtsBranchConfigurationTest;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.NavigateTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.WorldEditorUtil;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.state.TeamState;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.navigate.TeamWorkflowSearchWorkflowSearchItem;
import org.eclipse.osee.ats.navigate.UserSearchWorkflowSearchItem;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.ats.world.search.UserWorldSearchItem.UserSearchOption;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.support.test.util.TestUtil;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToWorldViewTest {

   @org.junit.Test
   public void testDemoDatabase() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      VisitedItems.clearVisited();
      DemoTestUtil.setUpTest();
      assertTrue(DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones) != null);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testAttributeDeletion() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      Collection<Artifact> arts = runGeneralLoadingTest("My Favorites", AtsArtifactTypes.TeamWorkflow, 3, null);
      arts.clear();
      NavigateTestUtil.getAllArtifactChildren(getXViewer().getTree().getItems(), arts);
      // delete an artifact, look for expected !Errors in the XCol
      deleteAttributesForXColErrorTest(arts, AtsAttributeTypes.ChangeType);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testMyWorld() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      runGeneralLoadingTest("My World", AtsArtifactTypes.AbstractWorkflowArtifact, 11, null);
      runGeneralXColTest(28, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   private XNavigateItem openUserSearchEditor() throws Exception {
      WorldEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("User Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof UserSearchWorkflowSearchItem);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);
      return item;
   }

   @org.junit.Test
   public void testSearchMyFavorites() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      UserSearchWorkflowSearchItem dwl =
         (UserSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      dwl.setSelectedUser(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      dwl.setSelected(UserSearchOption.Favorites, true);
      runGeneralUserSearchTest(item, 3);
      runGeneralXColTest(20, false);
      // test the task tab - this is being done via open ats task editor
      runGeneralXColTest(20, true);
      // Open all three favorites
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      for (Artifact artifact : arts) {
         SMAEditor.editArtifact(artifact);
      }
      // Test that recently visited returns all three
      Collection<Artifact> artsLoaded = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), artsLoaded, AtsArtifactTypes.TeamWorkflow, 3);
      runGeneralXColTest(20, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchMySubscribed() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Subscribed, true);
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), 1, arts.size());
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchState() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Assignee, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelectedState(TeamState.Implement.getName());
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.AbstractWorkflowArtifact, 7);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchMyReviews() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeReviews, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTeamWorkflows, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTasks, false);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 2);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 2);
      runGeneralXColTest(4, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchMyReviewsAll() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeReviews, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTeamWorkflows, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTasks, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeCompleted, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeCancelled, true);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 3);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 3);
      runGeneralXColTest(6, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchMyOriginator() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Originator, true);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.Task,
         DemoTestUtil.getNumTasks());
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.TeamWorkflow, 22);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 2);
      // Only 2 decision reviews should have been created by Joe, rest are Rule reviews created by OseeSystem user
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 2);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchMyOriginatorAll() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Originator, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeCompleted, true);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.Task,
         DemoTestUtil.getNumTasks());
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.TeamWorkflow, 25);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 2);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 3);
      runGeneralXColTest(70, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   private void runGeneralUserSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      // validate
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   @org.junit.Test
   public void testSearchMyCompleted() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      UserSearchWorkflowSearchItem dwl =
         (UserSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      dwl.setSelectedUser(AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Joe_Smith));
      dwl.setSelected(UserSearchOption.IncludeCompleted, true);
      dwl.setSelected(UserSearchOption.IncludeTasks, false);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.TeamWorkflow, 7);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 3);
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.DecisionReview, 3);
      runGeneralXColTest(30, false);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testMyRecentlyVisited() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      // Load Recently Visited
      runGeneralLoadingTest("My Recently Visited", AtsArtifactTypes.TeamWorkflow, 3, null);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testOtherUsersWorld() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      OseeLog.log(AtsBranchConfigurationTest.class, Level.INFO,
         "Testing User's items relating to " + DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("User's World").iterator().next();
      runGeneralLoadingTest(item, AtsArtifactTypes.AbstractWorkflowArtifact, 12,
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchOtherUserReviews() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeReviews, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTeamWorkflows, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTasks, false);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 1);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchOtherUserAllReviews() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeReviews, true);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTeamWorkflows, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeTasks, false);
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.IncludeCompleted, false);
      editor.getWorldXWidgetActionPage().reSearch(true);

      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, AtsArtifactTypes.PeerToPeerReview, 1);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchOtherUserFavorites() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Favorites, true);
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), 0, arts.size());
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testSearchOtherUserSubscribed() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = openUserSearchEditor();
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((UserSearchWorkflowSearchItem) dwl).setSelectedUser(
         AtsClientService.get().getUserServiceClient().getUserFromToken(DemoUsers.Kay_Jones));
      ((UserSearchWorkflowSearchItem) dwl).setSelected(UserSearchOption.Subscribed, true);
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), 0, arts.size());
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testGroupsSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      WorldEditor.closeAll();
      Artifact groupArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.UniversalGroup, "Test Group",
         AtsUtilCore.getAtsBranch());
      assertTrue(groupArt != null);
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Group Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof GroupWorldSearchItem);
      ((GroupWorldSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedGroup(groupArt);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);
      WorldEditor worldEditor = WorldEditorUtil.getSingleEditorOrFail();
      Collection<Artifact> arts = worldEditor.getLoadedArtifacts();

      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Actions", arts, AtsArtifactTypes.Action, 2);
      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Teams", arts, AtsArtifactTypes.TeamWorkflow, 7);
      NavigateTestUtil.testExpectedVersusActual(item.getName() + " Tasks", arts, AtsArtifactTypes.Task,
         DemoTestUtil.getNumTasks());
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testTeamWorkflowSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      List<IAtsTeamDefinition> selectedTeamDefs =
         TeamDefinitions.getTeamTopLevelDefinitions(Active.Active, AtsClientService.get().getConfig());
      WorldEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Team Workflow Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TeamWorkflowSearchWorkflowSearchItem);
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI);
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedTeamDefs, 1);
      runGeneralTeamWorkflowSearchOnCompletedCancelledTest(item, true, 2);
      runGeneralTeamWorkflowSearchOnCompletedCancelledTest(item, false, 1);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Joe Smith", 0);
      selectedTeamDefs.clear();
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedTeamDefs, 7);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.UnReleased, 7);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Kay Jones", 10);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.Released, 0);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.Both, 10);
      List<String> teamDefs = new ArrayList<>();
      teamDefs.add("SAW Test");
      teamDefs.add("SAW Design");
      Set<IAtsTeamDefinition> tda = TeamDefinitions.getTeamDefinitions(teamDefs, AtsClientService.get().getConfig());
      runGeneralTeamWorkflowSearchOnTeamTest(item, tda, 3);
      runGeneralTeamWorkflowSearchOnVersionTest(item, SAW_Bld_1.getName(), 0);
      runGeneralTeamWorkflowSearchOnVersionTest(item, SAW_Bld_2.getName(), 3);
      selectedTeamDefs.clear();
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedTeamDefs, 10);
      TestUtil.severeLoggingEnd(monitor);
   }

   private void runGeneralTeamWorkflowSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      editor.getWorldXWidgetActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      // validate
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   private void runGeneralTeamWorkflowSearchOnAssigneeTest(XNavigateItem item, String assignee, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener()).setSelectedUser(
         AtsClientService.get().getUserService().getUserByName(assignee));
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   private void runGeneralTeamWorkflowSearchOnTeamTest(XNavigateItem item, Collection<IAtsTeamDefinition> selectedTeamDefs, int expectedNum) throws Exception {
      // need to set team selected users
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener();
      ((TeamWorkflowSearchWorkflowSearchItem) dwl).setSelectedTeamDefinitions(selectedTeamDefs);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   private void runGeneralTeamWorkflowSearchOnReleasedTest(XNavigateItem item, ReleasedOption released, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener()).setSelectedReleased(
         released);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   private void runGeneralTeamWorkflowSearchOnVersionTest(XNavigateItem item, String versionString, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener()).setVersion(
         versionString);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   private void runGeneralTeamWorkflowSearchOnCompletedCancelledTest(XNavigateItem item, boolean selected, int expectedNum) throws Exception {
      WorldEditor editor = WorldEditorUtil.getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener()).setIncludeCompletedCheckbox(
         selected);
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getWorldXWidgetActionPage().getDynamicWidgetLayoutListener()).setIncludeCancelledCheckbox(
         selected);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   @org.junit.Test
   public void testActionableItemSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Actionable Item Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ActionableItemWorldSearchItem);
      ((ActionableItemWorldSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedActionItems(
         ActionableItems.getActionableItems(Arrays.asList("SAW Code"), AtsClientService.get().getConfig()));
      // normal searches copy search item which would clear out the set value above; for this test, don't copy item
      runGeneralLoadingTest(item, AtsArtifactTypes.TeamWorkflow, 3, null, TableLoadOption.DontCopySearchItem);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testTargetedForVersionTeamSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      Collection<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof VersionTargetedForTeamSearchItem);
      IAtsVersion version = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      ((VersionTargetedForTeamSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedVersionArt(
         version);
      runGeneralLoadingTest(item, AtsArtifactTypes.TeamWorkflow, 14, null, TableLoadOption.DontCopySearchItem);
      TestUtil.severeLoggingEnd(monitor);
   }

   @org.junit.Test
   public void testTargetedForTeamSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      Collection<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Next Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof NextVersionSearchItem);
      ((NextVersionSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedTeamDef(
         TeamDefinitions.getTeamDefinitions(Arrays.asList("SAW SW"),
            AtsClientService.get().getConfig()).iterator().next());
      runGeneralLoadingTest(item, AtsArtifactTypes.TeamWorkflow, 14, null, TableLoadOption.DontCopySearchItem);
      TestUtil.severeLoggingEnd(monitor);
   }

   private Collection<Artifact> runGeneralLoadingTest(String xNavigateItemName, IArtifactType artifactType, int numOfType, IAtsUser user) throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem(xNavigateItemName);
      return runGeneralLoadingTest(item, artifactType, numOfType, user);
   }

   private Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, IArtifactType artifactType, int numOfType, IAtsUser user) throws Exception {
      return runGeneralLoadingTest(item, artifactType, numOfType, user, TableLoadOption.None);
   }

   private Collection<Artifact> runGeneralLoadingTest(XNavigateItem item, IArtifactType artifactType, int numOfType, IAtsUser user, TableLoadOption tableLoadOption) throws Exception {
      WorldEditor.closeAll();
      // Find the correct navigate item
      if (user != null && item instanceof SearchNavigateItem) {
         if (((SearchNavigateItem) item).getWorldSearchItem() instanceof UserSearchItem) {
            ((UserSearchItem) ((SearchNavigateItem) item).getWorldSearchItem()).setSelectedUser(user);
         }
      }
      // Simulate double-click of navigate item
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI, tableLoadOption);

      WorldEditor worldEditor = WorldEditorUtil.getSingleEditorOrFail();
      Collection<Artifact> arts = worldEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, artifactType, numOfType);
      return arts;
   }

   private void runGeneralXColTest(int itemCount, boolean testTaskTab) throws Exception {
      int itemCnt, beforeSize, afterSize = 0;
      XViewer xv = getXViewer();
      xv.expandAll();
      itemCnt = xv.getVisibleItemCount(xv.getTree().getItems());
      NavigateTestUtil.testExpectedVersusActual("Item Count - ", itemCount, itemCnt);
      beforeSize = getXViewer().getCustomizeMgr().getCurrentVisibleTableColumns().size();
      // show all columns
      handleTableCustomization();
      xv.expandAll(); // necessary for linux cause customization change collapsesAll
      afterSize = getXViewer().getCustomizeMgr().getCurrentVisibleTableColumns().size();
      NavigateTestUtil.testExpectedVersusActual("Column Count - ", true, (afterSize >= beforeSize));
      runGeneralXColTest(itemCount, false, null, testTaskTab);
   }

   private void runGeneralXColTest(int expected, boolean isErrorCheck, IAttributeType attributeTypeToDelete, boolean testTaskTab) throws OseeCoreException {
      List<Artifact> arts = new ArrayList<>();
      List<Artifact> taskArts = new ArrayList<>();
      List<XViewerColumn> columns = getXViewer().getCustomizeMgr().getCurrentTableColumns();
      ITableLabelProvider labelProv = (ITableLabelProvider) getXViewer().getLabelProvider();
      // want to check all valid children
      TreeItem[] treeItem = getXViewer().getTree().getItems();
      NavigateTestUtil.getAllArtifactChildren(treeItem, arts);
      NavigateTestUtil.testExpectedVersusActual("Number of Artifacts - ", expected, arts.size());
      // are we running the fault case?
      if (testTaskTab) {
         getXViewer().expandAll();
         arts.clear();
         // grab the Task Artifacts and set them as selected
         this.getAllTreeItems(getXViewer().getTree().getItems(), taskArts);
         // open the task in the Task Editor
         TaskEditor.open(new TaskEditorSimpleProvider("ATS Tasks", getXViewer().getSelectedTaskArtifacts()));
         handleTableCustomization();
         getXViewer().expandAll(); // necessary for linux cause customization change collapsesAll
         columns = getXViewer().getCustomizeMgr().getCurrentTableColumns();
         verifyXColumns(labelProv, arts, columns);
      } else if (isErrorCheck) {
         verifyXColumnsHasErrors(labelProv, arts, columns, attributeTypeToDelete);
      } else {
         verifyXColumns(labelProv, arts, columns);
      }
   }

   private void getAllTreeItems(TreeItem[] treeItem, List<Artifact> taskArts) throws OseeCoreException {
      for (TreeItem item : treeItem) {
         if (item.getData() instanceof Artifact) {
            if (((Artifact) item.getData()).isOfType(AtsArtifactTypes.Task)) {
               getXViewer().getTree().setSelection(item);
               taskArts.add((Artifact) item.getData());
            }
         }
         if (item.getExpanded()) {
            getAllTreeItems(item.getItems(), taskArts);
         }
      }
   }

   private WorldXViewer getXViewer() {
      return WorldEditorUtil.getSingleEditorOrFail().getWorldComposite().getXViewer();
   }

   private void handleTableCustomization() {
      // add all columns
      CustomizeTableDialog cdialog = new CustomizeTableDialog(getXViewer());
      cdialog.createDialogArea(WorldEditorUtil.getSingleEditorOrFail().getWorldComposite());
      cdialog.handleAddAllItemButtonClick();
   }

   private void deleteAttributesForXColErrorTest(Collection<Artifact> arts, IAttributeType attributeTypeToDelete) throws Exception {
      Map<Artifact, Object> attributeValues = new HashMap<>();
      handleTableCustomization();
      getXViewer().expandAll(); // necessary after table customization for linux cause customization change collapsesAll
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Navigate Test");
      // select a workflow artifact; get its attributes; delete an attribute
      for (Artifact art : arts) {
         attributeValues.put(art, art.getSoleAttributeValue(attributeTypeToDelete));
         art.deleteAttribute(attributeTypeToDelete, art.getSoleAttributeValue(attributeTypeToDelete));
         art.persist(transaction);
      }
      transaction.execute();
      try {
         runGeneralXColTest(20, true, attributeTypeToDelete, false);
      } finally {
         transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), "Navigate Test");
         // restore the attribute to leave the demo db back in its original state
         for (Artifact art : arts) {
            art.setSoleAttributeValue(attributeTypeToDelete, attributeValues.get(art));
            art.persist(transaction);
         }
         transaction.execute();
      }
   }

   private void verifyXColumnsHasErrors(ITableLabelProvider labelProv, List<Artifact> arts, List<XViewerColumn> columns, IAttributeType attributeTypeToDelete) {
      List<String> actualErrorCols = new ArrayList<>();
      for (XViewerColumn xCol : columns) {
         verifyArtifactsHasErrors(labelProv, arts, xCol,
            getXViewer().getCustomizeMgr().getColumnNumFromXViewerColumn(xCol), actualErrorCols);
      }
      if (!AtsAttributeTypes.CurrentState.equals(attributeTypeToDelete) && !AtsAttributeTypes.PriorityType.equals(
         attributeTypeToDelete)) {
         verifyXCol1HasErrors(actualErrorCols);
      } else {
         verifyXCol2HasErrors(actualErrorCols);
      }
   }

   private void verifyXCol1HasErrors(List<String> actualErrorCols) {
      int index = 0;
      for (String col : actualErrorCols) {
         NavigateTestUtil.testExpectedVersusActual("Expected xCol " + col + " errors", true,
            NavigateTestUtil.expectedErrorCols1[index++].contains(col));
      }
   }

   private void verifyXCol2HasErrors(List<String> actualErrorCols) {
      int index = 0;
      NavigateTestUtil.testExpectedVersusActual("Expected number of xCol errors",
         NavigateTestUtil.expectedErrorCols2.length, actualErrorCols.size());
      for (String col : actualErrorCols) {
         NavigateTestUtil.testExpectedVersusActual("Expected xCol errors", true,
            NavigateTestUtil.expectedErrorCols2[index++].equals(col));
      }
   }

   private void verifyXColumns(ITableLabelProvider labelProv, Collection<Artifact> arts, List<XViewerColumn> columns) {
      for (XViewerColumn xCol : columns) {
         verifyArtifact(xCol, labelProv, arts, getXViewer().getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
      }
   }

   private void verifyArtifact(XViewerColumn xCol, ITableLabelProvider labelProv, Collection<Artifact> arts, int colIndex) {
      for (Artifact art : arts) {
         String colText = getColumnText(labelProv, xCol, colIndex, art);
         NavigateTestUtil.testExpectedVersusActual(
            "No Error expected in XCol [" + xCol.getName() + "] but got [" + colText + "]", true,
            !colText.contains("!Error"));
      }
   }

   private void verifyArtifactsHasErrors(ITableLabelProvider labelProv, Collection<Artifact> arts, XViewerColumn xCol, int colIndex, List<String> actualErrorCols) {
      for (Artifact art : arts) {
         String colText = getColumnText(labelProv, xCol, colIndex, art);
         if (art.isOfType(DemoArtifactTypes.DemoCodeTeamWorkflow)) {
            if (colText.contains("!Error")) {
               if (!actualErrorCols.contains(xCol.getId())) {
                  actualErrorCols.add(xCol.getId());
               }
            }
         }
      }
   }

   private String getColumnText(ITableLabelProvider labelProv, XViewerColumn xCol, int colIndex, Artifact art) {
      String colText = "";
      if (xCol instanceof IXViewerValueColumn) {
         try {
            StyledString styledText = ((IXViewerValueColumn) xCol).getStyledText(art, xCol, colIndex);
            if (styledText != null) {
               colText = styledText.getString();
            }
         } catch (XViewerException ex) {
            throw new OseeStateException(ex);
         }
      } else {
         colText = labelProv.getColumnText(art, colIndex);
      }
      if (colText == null) {
         colText = "";
      }
      return colText;
   }

}
