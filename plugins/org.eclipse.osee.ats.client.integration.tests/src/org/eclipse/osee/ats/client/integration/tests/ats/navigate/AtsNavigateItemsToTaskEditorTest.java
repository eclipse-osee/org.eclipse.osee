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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.client.demo.DemoSawBuilds;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.ats.client.integration.tests.util.DemoTestUtil;
import org.eclipse.osee.ats.client.integration.tests.util.NavigateTestUtil;
import org.eclipse.osee.ats.core.client.config.AtsBulkLoad;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskXViewer;
import org.eclipse.osee.ats.world.search.TaskSearchWorldSearchItem;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.util.IDynamicWidgetLayoutListener;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToTaskEditorTest {

   @org.junit.Test
   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   @org.junit.Test
   public void testMyTasksEditor() throws Exception {
      TaskEditor.closeAll();
   }

   @org.junit.Test
   public void testTaskSearch() throws Exception {
      SevereLoggingMonitor monitor = TestUtil.severeLoggingStart();

      AtsBulkLoad.reloadConfig(true);
      Collection<IAtsTeamDefinition> selectedUsers =
         TeamDefinitions.getTeamTopLevelDefinitions(Active.Active, AtsClientService.get().getConfig());
      TaskEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Task Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TaskSearchWorldSearchItem);
      handleGeneralDoubleClickAndTestResults(item, CoreArtifactTypes.Artifact, 0, TableLoadOption.DontCopySearchItem);
      runGeneralTaskSearchOnCompletedCancelledTest(item, true, 14);
      runGeneralTaskSearchOnCompletedCancelledTest(item, false, 0);
      runGeneralTaskSearchOnTeamTest(item, selectedUsers, 0);
      selectedUsers.clear();
      List<String> teamDefs = new ArrayList<>();
      teamDefs.add("SAW Code");
      Set<IAtsTeamDefinition> tda = TeamDefinitions.getTeamDefinitions(teamDefs, AtsClientService.get().getConfig());
      runGeneralTaskSearchOnTeamTest(item, tda, 14);
      runGeneralTaskSearchOnAssigneeTest(item, "Joe Smith", 14);
      runGeneralTaskSearchOnVersionTest(item, DemoSawBuilds.SAW_Bld_1.getName(), 0);
      runGeneralTaskSearchOnVersionTest(item, DemoSawBuilds.SAW_Bld_2.getName(), 14);
      selectedUsers.clear();
      runGeneralTaskSearchOnTeamTest(item, selectedUsers, 14);
      runGeneralTaskSearchOnAssigneeTest(item, "Kay Jones", 8);

      TestUtil.severeLoggingEnd(monitor);
   }

   public void runGeneralTaskSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      editor.getTaskActionPage().getTaskComposite().getTaskXViewer().setForcePend(true);
      editor.getTaskActionPage().reSearch();
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   public void runGeneralTaskSearchOnAssigneeTest(XNavigateItem item, String assignee, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setSelectedUser(UserManager.getUserByName(assignee));
      runGeneralTaskSearchTest(item, expectedNum);
   }

   public void runGeneralTaskSearchOnTeamTest(XNavigateItem item, Collection<IAtsTeamDefinition> selectedUsers, int expectedNum) throws Exception {
      // need to set team selected users
      TaskEditor editor = getSingleEditorOrFail();
      IDynamicWidgetLayoutListener dwl = editor.getTaskActionPage().getDynamicWidgetLayoutListener();
      ((TaskSearchWorldSearchItem) dwl).setSelectedTeamDefinitions(selectedUsers);
      runGeneralTaskSearchTest(item, expectedNum);
   }

   public void runGeneralTaskSearchOnVersionTest(XNavigateItem item, String versionString, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setVersion(versionString);
      runGeneralTaskSearchTest(item, expectedNum);
   }

   public void runGeneralTaskSearchOnCompletedCancelledTest(XNavigateItem item, boolean selected, int expectedNum) throws Exception {
      Artifact groupArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.UniversalGroup, "Test Group",
            AtsUtilCore.getAtsBranch());
      Set<Artifact> selectedUsers = new HashSet<>();
      TaskEditor editor = getSingleEditorOrFail();
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setIncludeCompletedCheckbox(selected);
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setIncludeCancelledCheckbox(selected);
      if (selected) {
         // select the group
         selectedUsers.add(groupArt);
         ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setSelectedGroups(selectedUsers);
      } else {
         // clear the group selected
         ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).handleSelectedGroupsClear();
      }
      runGeneralTaskSearchTest(item, expectedNum);
   }

   public void handleGeneralDoubleClickAndTestResults(XNavigateItem item, IArtifactType artifactType, int numOfType, TableLoadOption tableLoadOption) throws Exception {
      item.run(TableLoadOption.ForcePend, TableLoadOption.NoUI, tableLoadOption);
      TaskEditor taskEditor = getSingleEditorOrFail();
      assertTrue(taskEditor != null);
      Collection<Artifact> arts = taskEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, artifactType, numOfType);
   }

   public TaskEditor getSingleEditorOrFail() {
      // Retrieve results from opened editor and test
      Collection<TaskEditor> editors = TaskEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);

      return editors.iterator().next();
   }

   public TaskXViewer getXViewer() {
      return getSingleEditorOrFail().getTaskActionPage().getTaskComposite().getTaskXViewer();
   }

   public void handleTableCustomization() {
      // add all columns
      CustomizeTableDialog cdialog = new CustomizeTableDialog(getXViewer());
      cdialog.createDialogArea(getSingleEditorOrFail().getTaskActionPage().getTaskComposite());
      cdialog.handleAddAllItemButtonClick();
   }

}
