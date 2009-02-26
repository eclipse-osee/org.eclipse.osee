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
package org.eclipse.osee.ats.test.testDb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskXViewer;
import org.eclipse.osee.ats.world.search.TaskSearchWorldSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.IDynamicWidgetLayoutListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToTaskEditorTest extends TestCase {

   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   public void testMyTasksEditor() throws Exception {
      TaskEditor.closeAll();
      // Place holder for future navigate items opening TaskEditor
      //      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("My Tasks (Editor)");
      //      handleGeneralDoubleClickAndTestResults(item, TaskArtifact.class, DemoDbTasks.getNumTasks());
   }

   public void testTaskSearch() throws Exception {
      Set<TeamDefinitionArtifact> selectedUsers = TeamDefinitionArtifact.getTeamTopLevelDefinitions(Active.Active);
      TaskEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Task Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TaskSearchWorldSearchItem);
      handleGeneralDoubleClickAndTestResults(item, TaskSearchWorldSearchItem.class, 0,
            TableLoadOption.DontCopySearchItem);
      runGeneralTaskSearchOnCompletedCancelledTest(item, true, 14);
      runGeneralTaskSearchOnCompletedCancelledTest(item, false, 0);
      runGeneralTaskSearchOnTeamTest(item, selectedUsers, 0);
      selectedUsers.clear();
      List<String> teamDefs = new ArrayList<String>();
      teamDefs.add("SAW Code");
      Set<TeamDefinitionArtifact> tda = TeamDefinitionArtifact.getTeamDefinitions(teamDefs);
      runGeneralTaskSearchOnTeamTest(item, tda, 14);
      runGeneralTaskSearchOnAssigneeTest(item, "Joe Smith", 14);
      runGeneralTaskSearchOnVersionTest(item, "SAW_Bld_1", 0);
      runGeneralTaskSearchOnVersionTest(item, "SAW_Bld_2", 14);
      selectedUsers.clear();
      runGeneralTaskSearchOnTeamTest(item, selectedUsers, 14);
      runGeneralTaskSearchOnAssigneeTest(item, "Kay Jones", 8);

   }

   public void runGeneralTaskSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      editor.getTaskActionPage().reSearch();
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   public void runGeneralTaskSearchOnAssigneeTest(XNavigateItem item, String assignee, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setSelectedUser(UserManager.getUserByName(assignee));
      runGeneralTaskSearchTest(item, expectedNum);
   }

   public void runGeneralTaskSearchOnTeamTest(XNavigateItem item, Set<TeamDefinitionArtifact> selectedUsers, int expectedNum) throws Exception {
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
            ArtifactQuery.getArtifactFromTypeAndName(UniversalGroup.ARTIFACT_TYPE_NAME, "Test Group",
                  AtsPlugin.getAtsBranch());
      Set<Artifact> selectedUsers = new HashSet<Artifact>();
      TaskEditor editor = getSingleEditorOrFail();
      ((TaskSearchWorldSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setIncludeCompletedCancelledCheckbox(selected);
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

   public void handleGeneralDoubleClickAndTestResults(XNavigateItem item, Class<?> clazz, int numOfType, TableLoadOption tableLoadOption) throws OseeCoreException {
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI,
            tableLoadOption);
      TaskEditor taskEditor = getSingleEditorOrFail();
      assertTrue(taskEditor != null);
      Collection<Artifact> arts = taskEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, clazz, numOfType);
   }

   public TaskEditor getSingleEditorOrFail() throws OseeCoreException {
      // Retrieve results from opened editor and test
      Collection<TaskEditor> editors = TaskEditor.getEditors();
      assertTrue("Expecting 1 editor open, currently " + editors.size(), editors.size() == 1);

      return editors.iterator().next();
   }

   public TaskXViewer getXViewer() throws OseeCoreException {
      return getSingleEditorOrFail().getTaskActionPage().getTaskComposite().getTaskXViewer();
   }

   public void handleTableCustomization() throws OseeCoreException {
      // add all columns
      CustomizeDemoTableTestUtil cdialog = new CustomizeDemoTableTestUtil(getXViewer());
      cdialog.createDialogArea(getSingleEditorOrFail().getTaskActionPage().getTaskComposite());
      cdialog.handleAddAllItemButtonClick();
   }

}
