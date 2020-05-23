/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class DuplicateWorkflowViaWorldEditorAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public DuplicateWorkflowViaWorldEditorAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      super();
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setText("Duplicate Team Workflow");
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(FrameworkImage.DUPLICATE);
   }

   private Collection<TeamWorkFlowArtifact> getSelectedTeamWorkflowArtifacts() {
      List<TeamWorkFlowArtifact> teams = new ArrayList<>();
      for (Artifact art : selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         if (art instanceof TeamWorkFlowArtifact) {
            teams.add((TeamWorkFlowArtifact) art);
         }
      }
      return teams;
   }

   @Override
   public void runWithException() {
      if (getSelectedTeamWorkflowArtifacts().isEmpty()) {
         throw new OseeArgumentException("Must select one or more team workflows to duplicate");
      }
      DuplicateWorkflowBlam blamOperation = new DuplicateWorkflowBlam();
      blamOperation.setDefaultTeamWorkflows(getSelectedTeamWorkflowArtifacts());
      BlamEditor.edit(blamOperation);
   }
}
