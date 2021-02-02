/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.workflow.duplicate;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.clone.CloneData;
import org.eclipse.osee.ats.core.workflow.duplicate.CloneTeamWorkflowOperation;
import org.eclipse.osee.ats.core.workflow.util.IDuplicateWorkflowListener;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CloneWorkflowAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final IDuplicateWorkflowListener duplicateListener;
   private CloneDialog dialog;
   private final AtsApi atsApi;

   public CloneWorkflowAction(IAtsTeamWorkflow teamWf, IDuplicateWorkflowListener duplicateListener) {
      super("Clone Workflow");
      this.teamWf = teamWf;
      this.duplicateListener = duplicateListener;
      atsApi = AtsApiService.get();
   }

   @Override
   public void run() {
      CloneData data = new CloneData();

      dialog = new CloneDialog(getText(), "Enter details for new cloned Team Workflow", teamWf, data);
      if (dialog.open() == Window.OK) {

         CloneTeamWorkflowOperation clone = new CloneTeamWorkflowOperation(teamWf, duplicateListener, data, atsApi);

         data = clone.run();
         if (data.getResults().isErrors()) {
            XResultDataUI.report(data.getResults(), "Clone Workflow");
         } else {
            ArtifactId newTeamWf = data.getNewTeamWf();
            IAtsWorkItem workItem = atsApi.getQueryService().getWorkItem(newTeamWf.getIdString());
            WorkflowEditor.edit(workItem);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
