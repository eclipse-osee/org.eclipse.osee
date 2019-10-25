/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions;

import java.util.Arrays;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.workflow.util.DuplicateWorkflowAsIsOperation;
import org.eclipse.osee.ats.core.workflow.util.IDuplicateWorkflowListener;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CloneWorkflowAction extends Action {

   private final IAtsTeamWorkflow teamWf;
   private final IDuplicateWorkflowListener duplicateListener;

   public CloneWorkflowAction(IAtsTeamWorkflow teamWf, IDuplicateWorkflowListener duplicateListener) {
      super("Clone Workflow");
      this.teamWf = teamWf;
      this.duplicateListener = duplicateListener;
   }

   @Override
   public void run() {
      EntryDialog dialog = new EntryDialog(getText(), "Enter name for new cloned Team Workflow");
      dialog.setEntry(teamWf.getName() + " (cloned)");
      if (dialog.open() == Window.OK) {
         String name = dialog.getEntry();
         DuplicateWorkflowAsIsOperation op = new DuplicateWorkflowAsIsOperation(Arrays.asList(teamWf), false, name,
            AtsClientService.get().getUserService().getCurrentUser(), AtsClientService.get(),
            "Clone from " + teamWf.toStringWithId(), duplicateListener);
         XResultData results = op.run();
         if (!results.isErrors()) {
            for (IAtsTeamWorkflow newTeamArt : op.getResults().values()) {
               WorkflowEditor.edit(newTeamArt);
            }
         } else {
            XResultDataUI.report(results, "Clone Workflow");
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

}
