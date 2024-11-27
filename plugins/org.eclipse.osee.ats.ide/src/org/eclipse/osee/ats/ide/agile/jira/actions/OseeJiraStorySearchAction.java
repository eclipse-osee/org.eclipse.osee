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
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OseeJiraStorySearchAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;

   public OseeJiraStorySearchAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText("Search Related JIRA Story");
   }

   @Override
   public void runWithException() {
      XResultData rd = new XResultData();
      rd.log(getClass().getSimpleName() + "\n");

      Artifact wfArt = this.selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next();
      IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(wfArt);
      rd.log("Team Workflow: " + workItem.toStringWithAtsId() + "\n");
      if (!workItem.isTeamWorkflow()) {
         AWorkbench.popup("Must be Team Workflow");
         return;
      }

      try {
         JiraSearch srch = AtsApiService.get().getJiraService().search(workItem);
         rd.log("\n\n" + srch + "\n\n" + srch.getRd().toString());
      } catch (Exception ex) {
         rd.log(Lib.exceptionToString(ex));
      }
      XResultDataUI.report(rd, getClass().getSimpleName());
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.JIRA_SEARCH);
   }

}
