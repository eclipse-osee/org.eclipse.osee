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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.CreateStoryResponse;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.jira.JiraSearch;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.agile.jira.AbstractJiraSyncColumnUI;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.JsonUtil;
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
public abstract class CreateStoryAction extends AbstractAtsAction {

   private final ISelectedAtsArtifacts selectedAtsArtifacts;
   private XResultData rd;

   public CreateStoryAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this("Create JIRA Story", selectedAtsArtifacts);
   }

   public CreateStoryAction(String name, ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText(name);
   }

   public boolean performCreateOps() {
      return true;
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
            if (performCreateOps()) {

               if (Strings.isValid(jiraStoryId)) {
                  AWorkbench.popup(
                     "JIRA Story " + jiraStoryId + " is already created and mapped to this Team Workflow");
                  continue;
               }
               if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create JIRA Story",
                  "JIRA Story and link to this Team Workflow\n\nAre you sure?")) {
                  continue;
               }

               // Search for existing JIRA story first
               JiraSearch srch = AbstractJiraSyncColumnUI.search(workItem);
               if (srch.issues != null && !srch.issues.isEmpty()) {
                  jiraStoryId = srch.issues.iterator().next().key;
                  if (Strings.isValid(jiraStoryId)) {
                     if (MessageDialog.openConfirm(Displays.getActiveShell(), "Already Exists", String.format(
                        "This workflow %s has story %s created, Link to ATS?", workItem.getAtsId(), jiraStoryId))) {
                        IAtsChangeSet changes = AtsApiService.get().createChangeSet("Link JIRA Story");
                        changes.setSoleAttributeValue(workItem, AtsAttributeTypes.JiraStoryId, jiraStoryId);
                        changes.execute();

                        AWorkbench.popup("Story Linked");
                        continue;
                     }
                  }
               }
            } else {
               if (Strings.isInvalid(jiraStoryId)) {
                  AWorkbench.popup("Workflow not mapped to JIRA Story; Skipping");
                  continue;
               }
            }

            final String fJiraStoryId = jiraStoryId;
            Job createJiraStory = new Job("Creating/Updating JIRA Story") {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  try {
                     String createJson = getCreateJson(workItem, rd);
                     if (rd.isErrors()) {
                        XResultDataUI.report(rd, getText());
                        return Status.OK_STATUS;
                     }
                     rd.logf("Json: \n\n%s\n\n", createJson);
                     String jiraIssue = null;
                     if (performCreateOps()) {
                        jiraIssue =
                           AtsApiService.get().getServerEndpoints().getJiraEndpoint().createJiraIssue(createJson);
                     } else {
                        jiraIssue = AtsApiService.get().getServerEndpoints().getJiraEndpoint().editJira(createJson,
                           fJiraStoryId);
                     }
                     if (jiraIssue.contains("errorMessages")) {
                        rd.errorf("%s", jiraIssue);
                     } else {
                        rd.log("\n\n" + jiraIssue);

                        String responseJiraId = fJiraStoryId;
                        if (performCreateOps()) {
                           CreateStoryResponse createResp = JsonUtil.readValue(jiraIssue, CreateStoryResponse.class);
                           responseJiraId = createResp.getKey();
                           wfArt.setSoleAttributeValue(AtsAttributeTypes.JiraStoryId, responseJiraId);
                           wfArt.persist(getText());
                        } else {
                           rd.log(jiraIssue);
                        }

                        String link =
                           AtsApiService.get().getJiraService().getJiraBasePath() + "browse/" + responseJiraId;
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
            Jobs.startJob(createJiraStory);

         } catch (Exception ex) {
            rd.log(Lib.exceptionToString(ex));
         }
         if (rd.isErrors()) {
            XResultDataUI.report(rd, getClass().getSimpleName());
         }
      }
   }

   abstract protected String getCreateJson(IAtsWorkItem workItem, XResultData rd);

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.JIRA_ADD);
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

}
