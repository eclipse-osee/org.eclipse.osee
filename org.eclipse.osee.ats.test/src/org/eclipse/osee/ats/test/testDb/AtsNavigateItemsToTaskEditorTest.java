/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Arrays;
import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.config.DemoDbTasks;
import org.eclipse.osee.ats.config.demo.util.DemoUsers;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.search.EditTasksByTeamVersionSearchItem;
import org.eclipse.osee.ats.world.search.MyTaskSearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToTaskEditorTest extends TestCase {

   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   public void testMyTasksEditor() throws Exception {
      closeTaskEditors();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("My Tasks (Editor)");
      handleGeneralDoubleClickAndTestResults(item, TaskArtifact.class, DemoDbTasks.getNumTasks());
   }

   public void testUsersTasksEditor() throws Exception {
      closeTaskEditors();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("User's Tasks (Editor)");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof MyTaskSearchItem);
      ((UserSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUser(DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
      handleGeneralDoubleClickAndTestResults(item, TaskArtifact.class, DemoDbTasks.getTaskTitles(true).size());
   }

   public void testEditTasksTeamVersion() throws Exception {
      closeTaskEditors();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Edit Tasks by Team Version");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof EditTasksByTeamVersionSearchItem);
      ((EditTasksByTeamVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDefs(TeamDefinitionArtifact.getTeamDefinitions(Arrays.asList("SAW Code")));
      ((EditTasksByTeamVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedVersion(VersionArtifact.getVersions(
            Arrays.asList("SAW_Bld_2")).iterator().next());
      handleGeneralDoubleClickAndTestResults(item, TaskArtifact.class, DemoDbTasks.getNumTasks());
   }

   public void testEditTasksByUser() throws Exception {
      closeTaskEditors();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Edit Tasks by User");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof MyTaskSearchItem);
      ((UserSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedUser(DemoUsers.getDemoUser(DemoUsers.Kay_Jones));
      handleGeneralDoubleClickAndTestResults(item, TaskArtifact.class, DemoDbTasks.getTaskTitles(true).size());
   }

   public void handleGeneralDoubleClickAndTestResults(XNavigateItem item, Class<?> clazz, int numOfType) {
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      TaskEditor taskEditor = getTaskEditor();
      assertTrue(taskEditor != null);
      Collection<Artifact> arts = taskEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, clazz, numOfType);
   }

   private TaskEditor getTaskEditor() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof TaskEditor) {
            return (TaskEditor) editor.getPart(false);
         }
      }
      return null;
   }

   private void closeTaskEditors() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof TaskEditor) {
            page.closeEditor((TaskEditor) editor.getPart(false), false);
         }
      }
   }

}
