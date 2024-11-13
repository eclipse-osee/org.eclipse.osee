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

package org.eclipse.osee.ats.ide.actions.jira;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.jira.CreateStoryResponse;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.actions.AbstractAtsAction;
import org.eclipse.osee.ats.ide.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
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

   public CreateStoryAction(ISelectedAtsArtifacts selectedAtsArtifacts) {
      this.selectedAtsArtifacts = selectedAtsArtifacts;
      setToolTipText("Create JIRA Story");
   }

   @Override
   public void runWithException() {
      XResultData rd = new XResultData();
      rd.log(getClass().getSimpleName() + "\n");

      Artifact wfArt = this.selectedAtsArtifacts.getSelectedWorkflowArtifacts().iterator().next();
      IAtsWorkItem workItem = AtsApiService.get().getWorkItemService().getWorkItem(wfArt);
      rd.log("Team Workflow: " + workItem.toStringWithAtsId() + "\n");
      try {
         if (!workItem.isTeamWorkflow()) {
            AWorkbench.popup("Must be Team Workflow");
            return;
         }
         String jiraStoryId = wfArt.getSoleAttributeValue(AtsAttributeTypes.JiraStoryId, "");
         if (Strings.isValid(jiraStoryId)) {
            AWorkbench.popup("JIRA Story " + jiraStoryId + " is already created and mapped to this Team Workflow");
            return;
         }
         if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Create JIRA Story",
            "Create JIRA Story and link to this Team Workflow\n\nAre you sure?")) {
            return;
         }

         String createJson = getCreateJson(workItem, rd);
         if (rd.isSuccess()) {
            rd.logf("Json: \n\n%s\n\n", createJson);
            String jiraIssue = AtsApiService.get().getServerEndpoints().getJiraEndpoint().createJiraIssue(createJson);
            if (jiraIssue.contains("errorMessages")) {
               rd.errorf("%s", jiraIssue);
            } else {
               rd.log("\n\n" + jiraIssue);
               CreateStoryResponse createResp = JsonUtil.readValue(jiraIssue, CreateStoryResponse.class);
               wfArt.setSoleAttributeValue(AtsAttributeTypes.JiraStoryId, createResp.getKey());
               wfArt.persist(getText());

               String link = JiraUtil.getJiraBasePath() + "browse/" + createResp.getKey();
               rd.log(link);
               Program.launch(link);
            }
         }
      } catch (Exception ex) {
         rd.log(Lib.exceptionToString(ex));
      }
      if (rd.isErrors()) {
         XResultDataUI.report(rd, getClass().getSimpleName());
      }
   }

   abstract protected String getCreateJson(IAtsWorkItem workItem, XResultData rd);

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.JIRA_ADD);
   }

}
