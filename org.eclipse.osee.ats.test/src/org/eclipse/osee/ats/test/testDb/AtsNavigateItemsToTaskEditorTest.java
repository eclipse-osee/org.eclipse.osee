/*
 * Created on May 12, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.test.testDb;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.editor.TaskEditor;
import org.eclipse.osee.ats.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.navigate.SearchNavigateItem;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.ats.world.search.EditTasksByTeamVersionSearchItem;
import org.eclipse.osee.ats.world.search.UserSearchItem;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
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

   public void testEditTasksTeamVersion() throws Exception {
      closeTaskEditors();
      XNavigateItem item = nameToNavItem.get("Edit Tasks by Team Version");
      assertTrue(((SearchNavigateItem) item).getWorldSearchItem() instanceof EditTasksByTeamVersionSearchItem);
      ((EditTasksByTeamVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedTeamDefs(TeamDefinitionArtifact.getTeamDefinitions(Arrays.asList(new String[] {"SAW Code"})));
      ((EditTasksByTeamVersionSearchItem) (((SearchNavigateItem) item).getWorldSearchItem())).setSelectedVersion(VersionArtifact.getVersions(
            Arrays.asList(new String[] {"SAW_Bld_2"})).iterator().next());
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      TaskEditor taskEditor = getTaskEditor();
      assertTrue(taskEditor != null);
      Collection<Artifact> arts = taskEditor.getLoadedArtifacts();
      testExpectedVersusActual(item.getName(), arts, TaskArtifact.class, PopulateDemoActions.getNumTasks());
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
            page.closeEditor((TaskEditor) editor, false);
         }
      }
   }

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
