/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.operation;

import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.widgets.dialog.StateListAndTitleDialog;
import org.eclipse.osee.ats.ide.workflow.review.NewDecisionReviewJob;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class CreateDecisionReviewAction extends Action {

   private final WorkflowEditor editor;

   public CreateDecisionReviewAction(WorkflowEditor editor) {
      super("Create Decision Review");
      this.editor = editor;
      setToolTipText(getText());
   }

   @Override
   public void run() {
      try {
         IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) editor.getWorkItem();
         StateListAndTitleDialog dialog = new StateListAndTitleDialog("Create Decision Review",
            "Select state to that review will be associated with (optional).",
            AtsApiService.get().getWorkDefinitionService().getStateNames(teamWf.getWorkDefinition()));
         if (dialog.open() == Window.OK) {
            if (!Strings.isValid(dialog.getReviewTitle())) {
               AWorkbench.popup("ERROR", "Must enter review title");
               return;
            }
            NewDecisionReviewJob job =
               new NewDecisionReviewJob(teamWf, null, dialog.getReviewTitle(), dialog.getSelectedState(), null,
                  AtsApiService.get().getReviewService().getDefaultDecisionReviewOptions(), null, new Date(),
                  AtsApiService.get().getUserService().getCurrentUser());
            job.setUser(true);
            job.setPriority(Job.LONG);
            job.schedule();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.DECISION_REVIEW);
   }

}
