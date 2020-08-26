/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.Collection;
import org.eclipse.jface.action.IAction;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class AddTaskAction extends AbstractAtsAction {
   private final ISelectedTeamWorkflowArtifacts selectedTeamWfs;

   public AddTaskAction(ISelectedTeamWorkflowArtifacts selectedTeamWfs) {
      super("Add Task", IAction.AS_PUSH_BUTTON);
      this.selectedTeamWfs = selectedTeamWfs;
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.NEW_TASK));
      setToolTipText(getText());
   }

   public void updateEnablement() {
      Collection<TeamWorkFlowArtifact> teamWfs = selectedTeamWfs.getSelectedTeamWorkflowArtifacts();
      setEnabled(teamWfs.size() == 1 && teamWfs.iterator().next().isInWork());
   }

   @Override
   public void run() {
      AtsApiService.get().getTaskServiceIde().createNewTaskWithDialog(
         selectedTeamWfs.getSelectedTeamWorkflowArtifacts().iterator().next());
   }

}
