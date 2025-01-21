/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.agile.jira.AbstractJiraSyncColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public abstract class UpdateStoryAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private XResultData rd;

   public UpdateStoryAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this("Update JIRA Story", selectedAtsArtifacts);
   }

   public UpdateStoryAction(String name, ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(name);
   }

   @Override
   public void runWithException() {
      rd = new XResultData();
      rd.log(getClass().getSimpleName() + "\n");

      for (Artifact wfArt : this.selectedAtsArtifacts.getSelectedWorkflowArtifacts()) {
         IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(wfArt);
         rd.log("Team Workflow: " + workItem.toStringWithAtsId() + "\n");
         try {
            if (!workItem.isTeamWorkflow()) {
               AWorkbench.popup("Must be Team Workflow");
               continue;
            }
            String jiraStoryId = wfArt.getSoleAttributeValue(AtsAttributeTypes.JiraStoryId, "");
            if (Strings.isValid(jiraStoryId)) {
               if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Update JIRA Story",
                  "Update JIRA Story\n\nAre you sure?")) {
                  continue;
               }
            } else {
               // Search for existing JIRA story first; Link and then update if not linked
               JiraSearch srch = AbstractJiraSyncColumnUI.search(workItem);
               if (srch.issues != null && !srch.issues.isEmpty()) {
                  jiraStoryId = srch.issues.iterator().next().key;
                  if (Strings.isValid(jiraStoryId)) {
                     if (MessageDialog.openConfirm(Displays.getActiveShell(), "Already Exists", String.format(
                        "This workflow %s has story %s created, Link to ATS?", workItem.getAtsId(), jiraStoryId))) {
                        IAtsChangeSet changes = AtsApiService.get().createChangeSet("Link JIRA Story");
                        changes.setSoleAttributeValue(workItem, AtsAttributeTypes.JiraStoryId, jiraStoryId);
                        changes.execute();
                     }
                  }
               }
            }

            updateJiraStory(workItem, jiraStoryId);
         } catch (Exception ex) {
            rd.log(Lib.exceptionToString(ex));
         }
         if (rd.isErrors()) {
            XResultDataUI.report(rd, getClass().getSimpleName());
         }
      }
   }

   private void updateJiraStory(IAtsWorkItem workItem, String jiraStoryId) {
      Job updateJiraStory = new Job("Updating JIRA Story") {

         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               String createJson = getUpdateJson(workItem, rd);
               if (rd.isErrors()) {
                  XResultDataUI.report(rd, getText());
                  return Status.OK_STATUS;
               }
               rd.logf("Json: \n\n%s\n\n", createJson);
               String jiraIssue =
                  AtsApiService.get().getServerEndpoints().getJiraEndpoint().editJira(createJson, jiraStoryId);
               if (jiraIssue.contains("errorMessages")) {
                  rd.errorf("%s", jiraIssue);
               } else {
                  rd.log("\n\n" + jiraIssue);

                  performAfterUpdate(workItem, jiraStoryId);

                  String link = AtsApiService.get().getJiraService().getJiraBasePath() + "browse/" + jiraStoryId;
                  rd.log(link);
                  Program.launch(link);
               }
               if (rd.isErrors()) {
                  XResultDataUI.report(rd, getText());
               }
            } catch (Exception ex) {
               rd.log(Lib.exceptionToString(ex));
               XResultDataUI.report(rd, getText());
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(updateJiraStory);
   }

   // Override to perform additional actions.  eg: Transition
   protected void performAfterUpdate(IAtsWorkItem workItem, String responseJiraId) {
      // do nothing
   }

   abstract protected String getUpdateJson(IAtsWorkItem workItem, XResultData rd);

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.JIRA_UPDATE);
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

}
