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
package org.eclipse.osee.ats.test.cases;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.navigate.TeamWorkflowSearchWorkflowSearchItem;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.test.util.CustomizeDemoTableTestUtil;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.test.util.NavigateTestUtil;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.search.ActionableItemWorldSearchItem;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.ShowOpenWorkflowsByArtifactType;
import org.eclipse.osee.ats.world.search.StateWorldSearchItem;
import org.eclipse.osee.ats.world.search.UserCommunitySearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.TeamWorldSearchItem.ReleasedOption;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToWorldViewTest  {

   @org.junit.Test
public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
      assertTrue(DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones) != null);
   }

   @org.junit.Test
public void testAttributeDeletion() throws Exception {
      Collection<Artifact> arts = runGeneralLoadingTest("My Favorites", TeamWorkFlowArtifact.class, 3, null);
      arts.clear();
      NavigateTestUtil.getAllArtifactChildren(getXViewer().getTree().getItems(), arts);
      // delete an artifact, look for expected !Errors in the XCol
      deleteAttributesForXColErrorTest(arts, "ats.Team Definition");
      deleteAttributesForXColErrorTest(arts, "ats.User Community");
      deleteAttributesForXColErrorTest(arts, "ats.Actionable Item");
      deleteAttributesForXColErrorTest(arts, "ats.Change Type");
   }

   @org.junit.Test
public void testMyWorld() throws Exception {
      runGeneralLoadingTest("My World", StateMachineArtifact.class, 11, null);
      runGeneralXColTest(28, false);
   }

   @org.junit.Test
public void testMyFavoritesAndMyRecentlyVisited() throws Exception {
      // Load My Favorites (test My Favorites and use results to test My Recently Visited
      Collection<Artifact> arts = runGeneralLoadingTest("My Favorites", TeamWorkFlowArtifact.class, 3, null);
      assertTrue(arts.size() == 3);
      runGeneralXColTest(20, false);
      // test the task tab - this is being done via open ats task editor
      runGeneralXColTest(20, true);
      // Open all three favorites
      for (Artifact artifact : arts)
         SMAEditor.editArtifact(artifact);
      // Test that recently visited returns all three
      runGeneralLoadingTest("My Recently Visited", TeamWorkFlowArtifact.class, 3, null);
      runGeneralXColTest(20, false);
   }

   @org.junit.Test
public void testMyReviews() throws Exception {
      runGeneralLoadingTest("My Reviews", PeerToPeerReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Reviews", DecisionReviewArtifact.class, 2, null);
      runGeneralXColTest(4, false);
      runGeneralLoadingTest("My Reviews - All", PeerToPeerReviewArtifact.class, 2, null);
      runGeneralLoadingTest("My Reviews - All", DecisionReviewArtifact.class, 3, null);
      runGeneralXColTest(5, false);
   }

   @org.junit.Test
public void testMySubscribed() throws Exception {
      runGeneralLoadingTest("My Subscribed", TeamWorkFlowArtifact.class, 1, null);
      runGeneralXColTest(1, false);
   }

   @org.junit.Test
public void testMyOriginator() throws Exception {
      runGeneralLoadingTest("My Originator - InWork", TaskArtifact.class, DemoTestUtil.getNumTasks(), null);
      runGeneralXColTest(68, false);
      runGeneralLoadingTest("My Originator - InWork", TeamWorkFlowArtifact.class, 18, null);
      runGeneralLoadingTest("My Originator - InWork", PeerToPeerReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - InWork", DecisionReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - All", TaskArtifact.class, DemoTestUtil.getNumTasks(), null);
      runGeneralLoadingTest("My Originator - All", TeamWorkFlowArtifact.class, 25, null);
      runGeneralLoadingTest("My Originator - All", PeerToPeerReviewArtifact.class, 7, null);
      runGeneralLoadingTest("My Originator - All", DecisionReviewArtifact.class, 8, null);
      runGeneralXColTest(84, false);
   }

   @org.junit.Test
public void testMyCompleted() throws Exception {
      runGeneralLoadingTest("My Completed", TeamWorkFlowArtifact.class, 7, null);
      runGeneralXColTest(17, false);
      runGeneralLoadingTest("My Completed", PeerToPeerReviewArtifact.class, 1, null);
      runGeneralLoadingTest("My Completed", DecisionReviewArtifact.class, 1, null);

   }

   @org.junit.Test
public void testMyRecentlyVisited() throws Exception {
      // Load Recently Visited
      runGeneralLoadingTest("My Recently Visited", TeamWorkFlowArtifact.class, 3, null);
   }

   @org.junit.Test
public void testOtherUsersWorld() throws Exception {
      OseeLog.log(AtsPlugin.class, Level.INFO,
            "Testing User's items relating to " + DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItems("User's World").iterator().next();
      runGeneralLoadingTest(item, StateMachineArtifact.class, 8, DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testOtherUsersReviews() throws Exception {
      runGeneralLoadingTest("User's Reviews - InWork", PeerToPeerReviewArtifact.class, 1,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
      runGeneralLoadingTest("User's Reviews - All", PeerToPeerReviewArtifact.class, 2,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testOtherUsersSubscribed() throws Exception {
      runGeneralLoadingTest("User's Subscribed", TeamWorkFlowArtifact.class, 0,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testOtherUsersFavorites() throws Exception {
      runGeneralLoadingTest("User's Favorites", TeamWorkFlowArtifact.class, 0,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testOtherUsersOriginator() throws Exception {
      runGeneralLoadingTest("User's Originator - InWork", PeerToPeerReviewArtifact.class, 0,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
      runGeneralLoadingTest("User's Originator - All", PeerToPeerReviewArtifact.class, 1,
            DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testOtherUsersCompleted() throws Exception {
      runGeneralLoadingTest("User's Completed", ActionArtifact.class, 0, DemoTestUtil.getDemoUser(DemoUsers.Kay_Jones));
   }

   @org.junit.Test
public void testGroupsSearch() throws Exception {
      WorldEditor.closeAll();
      Artifact groupArt =
            ArtifactQuery.getArtifactFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME, "Test Group",
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
            DemoTestUtil.getNumTasks());
   }

   @org.junit.Test
public void testTeamWorkflowSearch() throws Exception {
      Set<TeamDefinitionArtifact> selectedUsers = TeamDefinitionArtifact.getTeamTopLevelDefinitions(Active.Active);
      WorldEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Team Workflow Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TeamWorkflowSearchWorkflowSearchItem);
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedUsers, 1);
      runGeneralTeamWorkflowSearchOnCompletedCancelledTest(item, true, 2);
      runGeneralTeamWorkflowSearchOnCompletedCancelledTest(item, false, 1);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Joe Smith", 0);
      selectedUsers.clear();
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedUsers, 7);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.UnReleased, 7);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Kay Jones", 6);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.Released, 0);
      runGeneralTeamWorkflowSearchOnReleasedTest(item, ReleasedOption.Both, 6);
      List<String> teamDefs = new ArrayList<String>();
      teamDefs.add("SAW Test");
      teamDefs.add("SAW Design");
      Set<TeamDefinitionArtifact> tda = TeamDefinitionArtifact.getTeamDefinitions(teamDefs);
      runGeneralTeamWorkflowSearchOnTeamTest(item, tda, 3);
      runGeneralTeamWorkflowSearchOnVersionTest(item, "SAW_Bld_1", 0);
      runGeneralTeamWorkflowSearchOnVersionTest(item, "SAW_Bld_2", 3);
      selectedUsers.clear();
      runGeneralTeamWorkflowSearchOnTeamTest(item, selectedUsers, 6);
   }

   public void runGeneralTeamWorkflowSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      WorldEditor editor = getSingleEditorOrFail();
      editor.getActionPage().reSearch(true);
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      // validate
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   public void runGeneralTeamWorkflowSearchOnAssigneeTest(XNavigateItem item, String assignee, int expectedNum) throws Exception {
      WorldEditor editor = getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getActionPage().getDynamicWidgetLayoutListener()).setSelectedUser(UserManager.getUserByName(assignee));
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   public void runGeneralTeamWorkflowSearchOnTeamTest(XNavigateItem item, Set<TeamDefinitionArtifact> selectedUsers, int expectedNum) throws Exception {
      // need to set team selected users
      WorldEditor editor = getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getActionPage().getDynamicWidgetLayoutListener();
      ((TeamWorkflowSearchWorkflowSearchItem) dwl).setSelectedTeamDefinitions(selectedUsers);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   public void runGeneralTeamWorkflowSearchOnReleasedTest(XNavigateItem item, ReleasedOption released, int expectedNum) throws Exception {
      WorldEditor editor = getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getActionPage().getDynamicWidgetLayoutListener()).setSelectedReleased(released);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   public void runGeneralTeamWorkflowSearchOnVersionTest(XNavigateItem item, String versionString, int expectedNum) throws Exception {
      WorldEditor editor = getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getActionPage().getDynamicWidgetLayoutListener()).setVersion(versionString);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   public void runGeneralTeamWorkflowSearchOnCompletedCancelledTest(XNavigateItem item, boolean selected, int expectedNum) throws Exception {
      WorldEditor editor = getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getActionPage().getDynamicWidgetLayoutListener()).includeCompletedCancelledCheckbox(selected);
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   @org.junit.Test
public void testUserCommunitySearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("User Community Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof UserCommunitySearchItem);
      ((UserCommunitySearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUserComm("Program 2");
      // normal searches copy search item which would clear out the set value above; for this test, don't copy item
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 4, null, TableLoadOption.DontCopySearchItem);
   }

   @org.junit.Test
public void testActionableItemSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Actionable Item Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ActionableItemWorldSearchItem);
      ((ActionableItemWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedActionItems(ActionableItemArtifact.getActionableItems(Arrays.asList("SAW Code")));
      // normal searches copy search item which would clear out the set value above; for this test, don't copy item
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 3, null, TableLoadOption.DontCopySearchItem);
   }

   @org.junit.Test
public void testTargetedForVersionTeamSearch() throws Exception {
      List<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof VersionTargetedForTeamSearchItem);
      ((VersionTargetedForTeamSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedVersionArt(VersionArtifact.getVersions(
            Arrays.asList("SAW_Bld_2")).iterator().next());
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 14, null, TableLoadOption.DontCopySearchItem);
   }

   @org.junit.Test
public void testTargetedForTeamSearch() throws Exception {
      List<XNavigateItem> items = NavigateTestUtil.getAtsNavigateItems("Workflows Targeted-For Next Version");
      // First one is the global one
      XNavigateItem item = items.iterator().next();
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof NextVersionSearchItem);
      ((NextVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDef(TeamDefinitionArtifact.getTeamDefinitions(
            Arrays.asList("SAW SW")).iterator().next());
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 14, null, TableLoadOption.DontCopySearchItem);
   }

   @org.junit.Test
public void testShowOpenDecisionReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Open Decision Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, DecisionReviewArtifact.class, 7);
   }

   @org.junit.Test
public void testShowWorkflowsWaitingForDecisionReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Workflows Waiting Decision Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 7);
   }

   @org.junit.Test
public void testShowOpenPeerToPeerReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Open PeerToPeer Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, PeerToPeerReviewArtifact.class, 7);
   }

   @org.junit.Test
public void testShowWorkflowsWaitingForPeerToPeerReviewsSearch() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Show Workflows Waiting PeerToPeer Reviews");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof ShowOpenWorkflowsByArtifactType);
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 6);
   }

   @org.junit.Test
public void testSearchByCurrentState() throws Exception {
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Search by Current State");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof StateWorldSearchItem);
      ((StateWorldSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedStateClass("Implement");
      runGeneralLoadingTest(item, TeamWorkFlowArtifact.class, 17, null, TableLoadOption.DontCopySearchItem);
   }

   @org.junit.Test
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

   public void runGeneralXColTest(int itemCount, boolean testTaskTab) throws Exception {
      int itemCnt, beforeSize, afterSize = 0;
      XViewer xv = getXViewer();
      xv.expandAll();
      itemCnt = xv.getVisibleItemCount(xv.getTree().getItems());
      NavigateTestUtil.testExpectedVersusActual("Item Count - ", itemCount, itemCnt);
      beforeSize = getXViewer().getCustomizeMgr().getCurrentVisibleTableColumns().size();
      // show all columns
      handleTableCustomization();
      afterSize = getXViewer().getCustomizeMgr().getCurrentVisibleTableColumns().size();
      NavigateTestUtil.testExpectedVersusActual("Column Count - ", true, (afterSize >= beforeSize));
      runGeneralXColTest(itemCount, false, "", testTaskTab);
   }

   public void runGeneralXColTest(int expected, boolean isErrorCheck, String attributeToDelete, boolean testTaskTab) throws OseeCoreException {
      List<Artifact> arts = new ArrayList<Artifact>();
      List<Artifact> taskArts = new ArrayList<Artifact>();
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
         columns = getXViewer().getCustomizeMgr().getCurrentTableColumns();
         verifyXColumns(labelProv, arts, columns);
      } else if (isErrorCheck)
         verifyXColumnsHasErrors(labelProv, arts, columns, attributeToDelete);
      else
         verifyXColumns(labelProv, arts, columns);
   }

   public void getAllTreeItems(TreeItem[] treeItem, List<Artifact> taskArts) throws OseeCoreException {
      for (TreeItem item : treeItem) {
         if (item.getData() instanceof Artifact) {
            if (((Artifact) item.getData()).getArtifactTypeName().equals("Task")) {
               getXViewer().getTree().setSelection(item);
               taskArts.add((Artifact) item.getData());
            }
         }
         if (item.getExpanded()) getAllTreeItems(item.getItems(), taskArts);
      }
   }

   public WorldEditor getSingleEditorOrFail() throws OseeCoreException {
      // Retrieve results from opened editor and test
      Collection<WorldEditor> editors = WorldEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);

      return editors.iterator().next();
   }

   public WorldXViewer getXViewer() throws OseeCoreException {
      return getSingleEditorOrFail().getWorldComposite().getXViewer();
   }

   public void handleTableCustomization() throws OseeCoreException {
      // add all columns      
      CustomizeDemoTableTestUtil cdialog = new CustomizeDemoTableTestUtil(getXViewer());
      cdialog.createDialogArea(getSingleEditorOrFail().getWorldComposite());
      cdialog.handleAddAllItemButtonClick();
   }

   public void deleteAttributesForXColErrorTest(Collection<Artifact> arts, String attributeToDelete) throws Exception {
      Map<Artifact, Object> attributeValues = new HashMap<Artifact, Object>();
      getXViewer().expandAll();
      handleTableCustomization();
      SkynetTransaction transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
      // select a workflow artifact; get its attributes; delete an attribute
      for (Artifact art : arts) {
         attributeValues.put(art, art.getSoleAttributeValue(attributeToDelete));
         art.deleteAttribute(attributeToDelete, art.getSoleAttributeValue(attributeToDelete));
         art.persistAttributes(transaction);
      }
      transaction.execute();
      try {
         runGeneralXColTest(20, true, attributeToDelete, false);
      } finally {
         transaction = new SkynetTransaction(AtsPlugin.getAtsBranch());
         // restore the attribute to leave the demo db back in its original state      
         for (Artifact art : arts) {
            art.setSoleAttributeValue(attributeToDelete, attributeValues.get(art));
            art.persistAttributes(transaction);
         }
         transaction.execute();
      }
   }

   public void verifyXColumnsHasErrors(ITableLabelProvider labelProv, List<Artifact> arts, List<XViewerColumn> columns, String attributeToDelete) throws OseeCoreException {
      List<String> actualErrorCols = new ArrayList<String>();
      for (XViewerColumn xCol : columns) {
         verifyArtifactsHasErrors(labelProv, arts, xCol, getXViewer().getCustomizeMgr().getColumnNumFromXViewerColumn(
               xCol), attributeToDelete, actualErrorCols);
      }
      if (!attributeToDelete.equals("ats.Current State") && !attributeToDelete.equals("ats.Priority"))
         verifyXCol1HasErrors(actualErrorCols);
      else
         verifyXCol2HasErrors(actualErrorCols);
   }

   public void verifyXCol1HasErrors(List<String> actualErrorCols) {
      int index = 0;
      for (String col : actualErrorCols) {
         NavigateTestUtil.testExpectedVersusActual("Expected xCol errors", true,
               NavigateTestUtil.expectedErrorCols1[index++].contains(col));
      }
   }

   public void verifyXCol2HasErrors(List<String> actualErrorCols) {
      int index = 0;
      NavigateTestUtil.testExpectedVersusActual("Expected number of xCol errors",
            NavigateTestUtil.expectedErrorCols2.length, actualErrorCols.size());
      for (String col : actualErrorCols) {
         NavigateTestUtil.testExpectedVersusActual("Expected xCol errors", true,
               NavigateTestUtil.expectedErrorCols2[index++].equals(col));
      }
   }

   public void verifyXColumns(ITableLabelProvider labelProv, Collection<Artifact> arts, List<XViewerColumn> columns) throws OseeCoreException {
      for (XViewerColumn xCol : columns) {
         verifyArtifact(labelProv, arts, getXViewer().getCustomizeMgr().getColumnNumFromXViewerColumn(xCol));
      }
   }

   public void verifyArtifact(ITableLabelProvider labelProv, Collection<Artifact> arts, int colIndex) {
      for (Artifact art : arts) {
         String colText = labelProv.getColumnText(art, colIndex);
         NavigateTestUtil.testExpectedVersusActual("No Error in XCol expected", true, !colText.contains("!Error"));
      }
   }

   public void verifyArtifactsHasErrors(ITableLabelProvider labelProv, Collection<Artifact> arts, XViewerColumn xCol, int colIndex, String attributeToDelete, List<String> actualErrorCols) {
      for (Artifact art : arts) {
         String colText = labelProv.getColumnText(art, colIndex);
         if (art.getArtifactTypeName().equals("Demo Code Team Workflow")) {
            if (colText.contains("!Error")) if (!actualErrorCols.contains(xCol.getId())) actualErrorCols.add(xCol.getId());
         }
      }
   }

}
