/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.navigate.TeamWorkflowSearchWorkflowSearchItem;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.task.TaskXViewer;
import org.eclipse.osee.ats.world.search.TaskSearchWorldSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
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
      TaskEditor.closeAll();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Task Search");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof TaskSearchWorldSearchItem);
      handleGeneralDoubleClickAndTestResults(item, TaskSearchWorldSearchItem.class, 0);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Joe Smith", 7);
      runGeneralTeamWorkflowSearchOnAssigneeTest(item, "Kay Jones", 6);
   }

   public void runGeneralTeamWorkflowSearchTest(XNavigateItem item, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      // needed to force for test...
      editor.getTaskEditorProvider().setTableLoadOptions(TableLoadOption.ForcePend);
      editor.getTaskActionPage().reSearch();
      Collection<Artifact> arts = editor.getLoadedArtifacts();
      // validate
      NavigateTestUtil.testExpectedVersusActual(item.getName(), expectedNum, arts.size());
   }

   public void runGeneralTeamWorkflowSearchOnAssigneeTest(XNavigateItem item, String assignee, int expectedNum) throws Exception {
      TaskEditor editor = getSingleEditorOrFail();
      ((TeamWorkflowSearchWorkflowSearchItem) editor.getTaskActionPage().getDynamicWidgetLayoutListener()).setSelectedUser(UserManager.getUserByName(assignee));
      runGeneralTeamWorkflowSearchTest(item, expectedNum);
   }

   public void handleGeneralDoubleClickAndTestResults(XNavigateItem item, Class<?> clazz, int numOfType) throws OseeCoreException {
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
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
