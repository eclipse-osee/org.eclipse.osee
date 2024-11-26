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

package org.eclipse.osee.ats.ide.agile.jira.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class ShowJiraStoryAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public ShowJiraStoryAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText("Show Related JIRA Story");
   }

   @Override
   public void runWithException() {
      XResultData rd = new XResultData();
      rd.log(getClass().getSimpleName() + "\n");

      Artifact wfArt = this.selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next();
      String jiraStoryId = wfArt.getSoleAttributeValue(AtsAttributeTypes.JiraStoryId, "");
      if (Strings.isValid(jiraStoryId)) {
         String link = AtsApiService.get().getJiraService().getJiraBasePath() + "browse/" + jiraStoryId;
         rd.log(link);
         Program.launch(link);
      } else {
         AWorkbench.popup("No JIRA Story Linked to this Team Workflow");
      }

   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      Artifact wfArt = this.selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next();
      String jiraStoryId = wfArt.getSoleAttributeValue(AtsAttributeTypes.JiraStoryId, "");
      if (Strings.isValid(jiraStoryId)) {
         return ImageManager.getImageDescriptor(AtsImage.JIRA_LINKED);
      }
      return ImageManager.getImageDescriptor(AtsImage.JIRA);
   }

}
