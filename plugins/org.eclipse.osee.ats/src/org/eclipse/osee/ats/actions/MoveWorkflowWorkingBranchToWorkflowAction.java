/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.actions;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.WorkItemType;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class MoveWorkflowWorkingBranchToWorkflowAction extends AbstractAtsAction {

   private static final String MSG = "Move Workflow Branch to This Workflow";
   private final IAtsServices services;
   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public MoveWorkflowWorkingBranchToWorkflowAction(ISelectedAtsArtifacts selectedAtsArtifacts, IAtsServices services) {
      super(MSG, ImageManager.getImageDescriptor(FrameworkImage.BRANCH_WORKING));
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      this.services = services;
      setToolTipText(getText());
   }

   @Override
   public void runWithException()  {
      List<Long> associatedWfIds = new LinkedList<>();
      for (BranchId branch : services.getBranchService().getBranches(BranchArchivedState.UNARCHIVED,
         BranchType.WORKING)) {
         ArtifactId associatedArtifactId = services.getBranchService().getAssociatedArtifactId(branch);
         if (associatedArtifactId.isValid()) {
            associatedWfIds.add(associatedArtifactId.getId());
         }
      }
      Collection<IAtsTeamWorkflow> teamWfs = services.getQueryService().createQuery(WorkItemType.TeamWorkflow).andUuids(
         associatedWfIds.toArray(new Long[associatedWfIds.size()])).getItems();

      FilteredTreeDialog dialog = new FilteredTreeDialog(MSG, "Select Team Workflow to move branch from.",
         new ArrayTreeContentProvider(), new AtsObjectLabelProvider(false, true));
      dialog.setInput(teamWfs);
      dialog.setMultiSelect(false);
      if (dialog.open() == Window.OK) {

         IAtsTeamWorkflow fromTeamWf = dialog.getSelectedFirst();
         IAtsTeamWorkflow toTeamWf =
            (IAtsTeamWorkflow) selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next();
         String newBranchName = services.getBranchService().getBranchName(toTeamWf);

         String message = String.format(
            "Are you sure you wish to move Working Branch\nfrom\n%s\nto\n%s?\n\nBranch will be renamed to:",
            fromTeamWf.toStringWithId(), toTeamWf.toStringWithId());
         EntryDialog dialog2 = new EntryDialog(MSG, message);
         dialog2.setEntry(newBranchName);
         if (dialog2.open() == Window.OK) {
            newBranchName = dialog2.getEntry();
            Result result = services.getBranchService().moveWorkingBranch(fromTeamWf, toTeamWf, newBranchName);
            if (result.isFalse()) {
               AWorkbench.popup("Move Branch Failed", result.getText());
            } else {
               ArtifactExplorer.exploreBranch(services.getBranchService().getWorkingBranch(toTeamWf));
               AWorkbench.popup("Completed");
            }
         }
      }
   }

}
