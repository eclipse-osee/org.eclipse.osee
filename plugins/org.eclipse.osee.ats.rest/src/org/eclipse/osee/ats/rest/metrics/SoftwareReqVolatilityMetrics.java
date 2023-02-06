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
package org.eclipse.osee.ats.rest.metrics;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Stephen J. Molaro
 */
public final class SoftwareReqVolatilityMetrics implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private String programVersion;
   private final String targetVersion;
   private final boolean includeUnchangedCode;
   Collection<IAtsTask> tasksMissingChangeType = new ArrayList<>();

   private ExcelXmlWriter writer;
   private final QueryBuilder query;

   Pattern UI_NAME = Pattern.compile("\\{.*\\}");
   Pattern UI_IMPL = Pattern.compile("\\(Impl Details\\)");

   private final SoftwareReqVolatilityId[] reportColumns = {
      SoftwareReqVolatilityId.ACT,
      SoftwareReqVolatilityId.ActionName,
      SoftwareReqVolatilityId.Program,
      SoftwareReqVolatilityId.Build,
      SoftwareReqVolatilityId.Date,
      SoftwareReqVolatilityId.Completed,
      SoftwareReqVolatilityId.Added,
      SoftwareReqVolatilityId.Modified,
      SoftwareReqVolatilityId.Deleted};

   private final SoftwareReqVolatilityId[] weeklyReportColumns = {
      SoftwareReqVolatilityId.Week,
      SoftwareReqVolatilityId.Added,
      SoftwareReqVolatilityId.Modified,
      SoftwareReqVolatilityId.Deleted};

   public SoftwareReqVolatilityMetrics(OrcsApi orcsApi, AtsApi atsApi, String targetVersion, boolean includeUnchangedCode) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.includeUnchangedCode = includeUnchangedCode;
      this.query = orcsApi.getQueryFactory().fromBranch(atsApi.getAtsBranch());
   }

   @Override
   public void write(OutputStream output) {
      try {
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));
         writeReport();
         writer.endWorkbook();
      } catch (Exception ex) {
         try {
            writer.endWorkbook();
         } catch (IOException ex1) {
            throw new WebApplicationException(ex1);
         }
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport() throws IOException {
      Collection<IAtsTeamWorkflow> workflows = getDatedWorkflows();
      if (!workflows.isEmpty()) {

         Set<IAtsAction> actionableItems = getDatedActions(workflows);

         writer.startSheet("SRV", reportColumns.length);
         fillActionableData(actionableItems, reportColumns.length);
      }
   }

   private Collection<IAtsTeamWorkflow> getDatedWorkflows() {

      ArtifactToken versionId = atsApi.getQueryService().getArtifactFromTypeAndAttribute(AtsArtifactTypes.Version,
         CoreAttributeTypes.Name, targetVersion, atsApi.getAtsBranch());
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionId);
      Collection<IAtsTeamWorkflow> workflowArts = atsApi.getVersionService().getTargetedForTeamWorkflows(version);

      Collection<IAtsTeamWorkflow> codeWorkflows = new ArrayList<IAtsTeamWorkflow>();

      for (IAtsTeamWorkflow workflow : workflowArts) {
         if ((workflow.isWorkType(WorkType.Requirements) && workflow.isCompleted())) {
            IAtsAction action = atsApi.getActionService().getAction(workflow);
            Collection<IAtsTeamWorkflow> workflows = action.getTeamWorkflows();
            for (IAtsTeamWorkflow wf : workflows) {
               if (wf.isWorkType(WorkType.Code)) {
                  codeWorkflows.add(wf);
               }
            }
         }
      }

      Collection<IAtsTeamWorkflow> retCodeWorkflows = new ArrayList<IAtsTeamWorkflow>();

      for (IAtsTeamWorkflow codeWf : codeWorkflows) {
         Collection<IAtsTask> tasks = atsApi.getTaskService().getTask(codeWf);

         if (!tasks.isEmpty()) {
            retCodeWorkflows.add(codeWf);
         }

         for (IAtsTask task : tasks) {
            Matcher mName = UI_NAME.matcher(task.getName());
            Matcher mimpl = UI_IMPL.matcher(task.getName());

            if (mName.find() && mimpl.find() && atsApi.getAttributeResolver().getAttributeCount(task,
               CoreAttributeTypes.TaskChangeType) == 0) {
               tasksMissingChangeType.add(task);
            }
         }
      }

      programVersion = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getName();
      return retCodeWorkflows;
   }

   private Set<IAtsAction> getDatedActions(Collection<IAtsTeamWorkflow> workflows) {
      Set<IAtsAction> actionableItems = new HashSet<>();
      for (IAtsTeamWorkflow workflow : workflows) {
         actionableItems.add(workflow.getParentAction());
      }
      return actionableItems;
   }

   private void fillActionableData(Set<IAtsAction> actionableItems, int numColumns) throws IOException {
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = reportColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsAction actionItem : actionableItems) {
         IAtsTeamWorkflow reqWorkflow = IAtsTeamWorkflow.SENTINEL;
         IAtsTeamWorkflow codeWorkflow = IAtsTeamWorkflow.SENTINEL;
         for (IAtsTeamWorkflow wf : actionItem.getTeamWorkflows()) {
            if (wf.isWorkType(WorkType.Requirements)) {
               reqWorkflow = wf;
            }
            if (wf.isWorkType(WorkType.Code)) {
               codeWorkflow = wf;
            }
         }

         buffer[0] = actionItem.getAtsId();
         buffer[1] = actionItem.getName();
         buffer[2] = programVersion;
         buffer[3] = targetVersion;
         Date createdDate = new Date();
         for (IAtsTeamWorkflow teamWorkflow : actionItem.getTeamWorkflows()) {
            if (teamWorkflow.getCreatedDate().before(createdDate)) {
               createdDate = teamWorkflow.getCreatedDate();
            }
         }
         buffer[4] = reqWorkflow.getCreatedDate();
         if (reqWorkflow.isCompleted()) {
            buffer[5] = reqWorkflow.getCompletedDate();
         } else {
            buffer[5] = null;
         }
         buffer[6] = getAddedCount(codeWorkflow);
         buffer[7] = getModifiedCount(codeWorkflow);
         buffer[8] = getDeletedCount(codeWorkflow);

         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }

      writer.endSheet();
   }

   private int getAddedCount(IAtsTeamWorkflow teamWorkflow) {
      int added = 0;
      Collection<String> taskUINames = new ArrayList<String>();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWorkflow)) {
         Matcher mName = UI_NAME.matcher(task.getName());
         Matcher mimpl = UI_IMPL.matcher(task.getName());

         if (mName.find() && mimpl.find()) {
            String taskUIName = mName.group();
            String changeType =
               atsApi.getAttributeResolver().getSoleAttributeValue(task, CoreAttributeTypes.TaskChangeType, "");
            if (!taskUINames.contains(taskUIName) && changeType.equals(
               CoreAttributeTypes.TaskChangeType.Add.getName()) && (!task.getStateMgr().getCurrentState().getName().equals(
                  "No_Change") || includeUnchangedCode)) {
               taskUINames.add(taskUIName);
               added++;
            }
         }
      }
      return added;
   }

   private int getModifiedCount(IAtsTeamWorkflow teamWorkflow) {
      int modified = 0;
      Collection<String> taskUINames = new ArrayList<String>();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWorkflow)) {
         Matcher mName = UI_NAME.matcher(task.getName());
         Matcher mimpl = UI_IMPL.matcher(task.getName());

         if (mName.find() && mimpl.find()) {
            String taskUIName = mName.group();
            String changeType =
               atsApi.getAttributeResolver().getSoleAttributeValue(task, CoreAttributeTypes.TaskChangeType, "");
            if (!taskUINames.contains(taskUIName) && changeType.equals(
               CoreAttributeTypes.TaskChangeType.Modify.getName()) && (!task.getStateMgr().getCurrentState().getName().equals(
                  "No_Change") || includeUnchangedCode)) {
               taskUINames.add(taskUIName);
               modified++;
            }
         }
      }
      return modified;
   }

   private int getDeletedCount(IAtsTeamWorkflow teamWorkflow) {
      int deleted = 0;
      Collection<String> taskUINames = new ArrayList<String>();
      for (IAtsTask task : atsApi.getTaskService().getTasks(teamWorkflow)) {
         Matcher mName = UI_NAME.matcher(task.getName());
         Matcher mimpl = UI_IMPL.matcher(task.getName());

         if (mName.find() && mimpl.find()) {
            String taskUIName = mName.group();
            String changeType =
               atsApi.getAttributeResolver().getSoleAttributeValue(task, CoreAttributeTypes.TaskChangeType, "");
            if (!taskUINames.contains(taskUIName) && changeType.equals(
               CoreAttributeTypes.TaskChangeType.Delete.getName()) && (!task.getStateMgr().getCurrentState().getName().equals(
                  "No_Change") || includeUnchangedCode)) {
               taskUINames.add(taskUIName);
               deleted++;
            }
         }
      }
      return deleted;
   }
}