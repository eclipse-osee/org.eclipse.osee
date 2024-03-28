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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.world.WorldResults;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.members.WfeMembersTab;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldXViewer;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class PublishWebViewJsonAction extends AbstractAtsAction {

   private final GoalArtifact goalArt;
   private final WorkflowEditor editor;

   public PublishWebViewJsonAction(GoalArtifact goalArt, WorkflowEditor editor) {
      super("Publish Web Json Data");
      this.goalArt = goalArt;
      this.editor = editor;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.REPORT));
   }

   @Override
   public void runWithException() {
      if (MessageDialog.openConfirm(Displays.getActiveShell(), getText(),
         "Are you sure you want to publish the current view/data?")) {
         WfeMembersTab membersTab = editor.getMembersTab();
         WorldXViewer worldXViewer = membersTab.getWorldXViewer();
         String custGuid = worldXViewer.getCustomizeMgr().getCurrentCustomizeData().getGuid();

         CustomizeData customization = AtsApiService.get().getStoreService().getCustomizationByGuid(custGuid);
         if (customization == null) {
            AWorkbench.popup("No customization found with id " + custGuid);
            return;
         }

         WorldResults wr =
            AtsApiService.get().getServerEndpoints().getWorldEndpoint().getCollectionJsonCustomizedPublish(
               goalArt.getArtifactToken(), custGuid);
         ResultsEditor.open("Results", getText(), AHTML.simpleJsonPage(JsonUtil.toJson(wr)));
      }
   }

}
