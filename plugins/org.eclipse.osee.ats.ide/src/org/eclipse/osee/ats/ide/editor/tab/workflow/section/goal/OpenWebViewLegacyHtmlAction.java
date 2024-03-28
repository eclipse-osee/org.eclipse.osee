/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.editor.tab.workflow.section.goal;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.members.WfeMembersTab;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class OpenWebViewLegacyHtmlAction extends AbstractAtsAction {

   private final GoalArtifact goalArt;
   private final WorkflowEditor editor;

   public OpenWebViewLegacyHtmlAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Open Legacy Web View - Live");
      this.goalArt = goalArt;
      this.editor = editor;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REPORT));
   }

   @Override
   public void runWithException() {
      WfeMembersTab membersTab = editor.getMembersTab();
      WorldXViewer worldXViewer = membersTab.getWorldXViewer();
      String custGuid = worldXViewer.getCustomizeMgr().getCurrentCustomizeData().getGuid();

      CustomizeData customization = AtsApiService.get().getStoreService().getCustomizationByGuid(custGuid);
      if (customization == null) {
         AWorkbench.popup("No customization found with id " + custGuid);
         return;
      }

      String url = String.format("%s/ats/world/coll/%s/ui/%s", AtsApiService.get().getApplicationServerBase(),
         goalArt.getIdString(), custGuid);
      Program.launch(url);
   }

}
