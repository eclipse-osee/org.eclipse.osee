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

import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.navigate.MassEditTeamVersionItem;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.task.TaskEditor;
import org.eclipse.osee.ats.test.util.DemoTestUtil;
import org.eclipse.osee.ats.test.util.NavigateTestUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateItemsToMassEditorTest {

   public AtsNavigateItemsToMassEditorTest() {
   }

   @org.junit.Test
   public void testDemoDatabase() throws Exception {
      DemoTestUtil.setUpTest();
   }

   @org.junit.Test
   public void testTeamVersions() throws Exception {
      closeTaskEditors();
      XNavigateItem item = NavigateTestUtil.getAtsNavigateItem("Team Versions");
      ((MassEditTeamVersionItem) item).setSelectedTeamDef(TeamDefinitionArtifact.getTeamDefinitions(
            Arrays.asList("SAW SW")).iterator().next());
      handleGeneralDoubleClickAndTestResults(item, VersionArtifact.class, 3);
   }

   public void handleGeneralDoubleClickAndTestResults(XNavigateItem item, Class<?> clazz, int numOfType) throws OseeCoreException {
      NavigateView.getNavigateView().handleDoubleClick(item, TableLoadOption.ForcePend, TableLoadOption.NoUI);
      MassArtifactEditor massEditor = getMassArtifactEditor();
      assertTrue(massEditor != null);
      Collection<Artifact> arts = massEditor.getLoadedArtifacts();
      NavigateTestUtil.testExpectedVersusActual(item.getName(), arts, clazz, numOfType);
   }

   private MassArtifactEditor getMassArtifactEditor() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      IEditorReference editors[] = page.getEditorReferences();
      for (int j = 0; j < editors.length; j++) {
         IEditorReference editor = editors[j];
         if (editor.getPart(false) instanceof MassArtifactEditor) {
            return (MassArtifactEditor) editor.getPart(false);
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
