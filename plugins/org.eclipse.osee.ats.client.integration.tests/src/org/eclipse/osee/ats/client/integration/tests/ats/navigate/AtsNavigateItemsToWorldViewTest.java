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

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
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
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.navigate.VisitedItems;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskEditorSimpleProvider;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.ats.world.search.GroupWorldSearchItem;
import org.eclipse.osee.ats.world.search.NextVersionSearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
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
