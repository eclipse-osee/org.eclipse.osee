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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeIgnoreType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Stephen J. Molaro
 */
public final class SoftwareReqVolatilityMetrics implements StreamingOutput {
   private final OrcsApi orcsApi;
   private final AtsApi atsApi;
   private String programVersion;
   private final String targetVersion;
   private final Date startDate;
   private final Date endDate;
   private final boolean allTime;
   Collection<IAtsTask> tasksMissingChangeType = new ArrayList<>();

   private ExcelXmlWriter writer;

   Pattern UI_NAME = Pattern.compile("\\{.*\\}");

   private final SoftwareReqVolatilityId[] reportColumns = {
      SoftwareReqVolatilityId.ACT,
      SoftwareReqVolatilityId.TW,
      SoftwareReqVolatilityId.ActionName,
      SoftwareReqVolatilityId.Program,
      SoftwareReqVolatilityId.Build,
      SoftwareReqVolatilityId.Date,
      SoftwareReqVolatilityId.Completed,
      SoftwareReqVolatilityId.AddedReq,
      SoftwareReqVolatilityId.ModifiedReq,
      SoftwareReqVolatilityId.DeletedReq,
      SoftwareReqVolatilityId.AddedSub,
      SoftwareReqVolatilityId.ModifiedSub,
      SoftwareReqVolatilityId.DeletedSub,
      SoftwareReqVolatilityId.AddedHeading,
      SoftwareReqVolatilityId.ModifiedHeading,
      SoftwareReqVolatilityId.DeletedHeading,
      SoftwareReqVolatilityId.AddedImpl,
      SoftwareReqVolatilityId.ModifiedImpl,
      SoftwareReqVolatilityId.DeletedImpl};

   public SoftwareReqVolatilityMetrics(OrcsApi orcsApi, AtsApi atsApi, String targetVersion, Date startDate, Date endDate, boolean allTime) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.startDate = startDate;
      this.endDate = endDate;
      this.allTime = allTime;
   }

   @Override
   public void write(OutputStream output) {
      try {
         writer = new ExcelXmlWriter(new OutputStreamWriter(output, "UTF-8"));
         writeReport();
         writer.endWorkbook();
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   private void writeReport() throws IOException {
      Collection<IAtsTeamWorkflow> reqWorkflows = getDatedWorkflows();
      if (!reqWorkflows.isEmpty()) {

         writer.startSheet("SRV", reportColumns.length);
         fillActionableData(reqWorkflows);
      }
   }

   private Collection<IAtsTeamWorkflow> getDatedWorkflows() {
      ArtifactToken versionId = atsApi.getQueryService().getArtifactFromTypeAndAttribute(AtsArtifactTypes.Version,
         CoreAttributeTypes.Name, targetVersion, atsApi.getAtsBranch());
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionId);
      Collection<IAtsTeamWorkflow> workflowArts = atsApi.getVersionService().getTargetedForTeamWorkflows(version);

      Collection<IAtsTeamWorkflow> reqWorkflows = new ArrayList<IAtsTeamWorkflow>();
      for (IAtsTeamWorkflow workflow : workflowArts) {
         try {
            boolean isReqWf = false;
            isReqWf = workflow.isWorkType(WorkType.Requirements);
            WorkDefinition workDef = workflow.getWorkDefinition();
            if (!isReqWf && workDef != null) {
               isReqWf = workDef.getName().contains("Requirements");
            }

            if (isReqWf && workflow.isCompleted()) {
               if (allTime || (workflow.getCompletedDate().after(startDate) && workflow.getCompletedDate().before(
                  endDate))) {
                  reqWorkflows.add(workflow);
               }
            }
         } catch (Exception ex) {
            continue;
         }
      }
      programVersion = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getName();

      return reqWorkflows;
   }

   private List<ChangeItem> getChangeItems(IAtsTeamWorkflow reqWorkflow) {
      BranchToken branch = atsApi.getBranchService().getBranch(reqWorkflow);
      if (branch == null || branch.isInvalid()) {
         return List.of();
      }
      String changeReportData = "";
      List<ChangeItem> changeItems = new ArrayList<>();

      try {
         if (atsApi.getAttributeResolver().getAttributeCount(reqWorkflow, CoreAttributeTypes.BranchDiffData) == 1) {
            changeReportData =
               atsApi.getAttributeResolver().getSoleAttributeValue(reqWorkflow, CoreAttributeTypes.BranchDiffData, "");
         } else {
            changeItems = orcsApi.getBranchOps().compareBranch(branch);
            if (!changeItems.isEmpty()) {
               changeReportData = JsonUtil.toJson(changeItems);
               TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(atsApi.getAtsBranch(),
                  "Generate Diff for Requirement Metrics");
               tx.createAttribute(atsApi.getArtifactResolver().get(reqWorkflow), CoreAttributeTypes.BranchDiffData,
                  changeReportData);
               tx.commit();
            }
         }
      } catch (Exception ex) {
         return List.of();
      }

      if (changeItems.isEmpty() && !changeReportData.isEmpty()) {
         changeItems = JsonUtil.readValues(changeReportData, ChangeItem.class);
      }
      return changeItems;
   }

   private void fillActionableData(Collection<IAtsTeamWorkflow> reqWorkflows) throws IOException {
      int numColumns = reportColumns.length;
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = reportColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsTeamWorkflow reqWorkflow : reqWorkflows) {
         List<ChangeItem> changeItems = getChangeItems(reqWorkflow);
         Date completedDate = new Date();
         try {
            completedDate = reqWorkflow.getCompletedDate();
            if (!completedDate.equals(new Date(0L))) {
               buffer[6] = completedDate;
            } else {
               buffer[6] = "";
            }
         } catch (Exception ex) {
            buffer[6] = "Missing or Multiple Values";
         }

         try {
            buffer[0] = reqWorkflow.getParentAction().getAtsId();
         } catch (Exception ex) {
            buffer[0] = "";
         }
         try {
            buffer[1] = reqWorkflow.getAtsId();
         } catch (Exception ex) {
            buffer[1] = "";
         }
         try {
            buffer[2] = reqWorkflow.getName();
         } catch (Exception ex) {
            buffer[2] = "";
         }
         buffer[3] = programVersion;
         buffer[4] = targetVersion;

         Date createdDate = new Date();
         try {
            createdDate = reqWorkflow.getCreatedDate();
            if (!createdDate.equals(new Date(0L))) {
               buffer[5] = createdDate;
            } else {
               buffer[5] = "";
            }
         } catch (Exception ex) {
            buffer[5] = "Missing or Multiple Values";
         }

         List<ChangeItem> attrChangeItems = new ArrayList<>();
         Map<ArtifactId, ChangeItem> artChangeItems = new HashMap<>();

         for (ChangeItem changeItem : changeItems) {
            if (changeItem.getChangeType().equals(ChangeType.Attribute) && changeItem.getIgnoreType().equals(
               ChangeIgnoreType.NONE) && changeItem.getItemTypeId().equals(CoreAttributeTypes.WordTemplateContent)) {
               attrChangeItems.add(changeItem);
            } else if (changeItem.getChangeType().equals(ChangeType.Artifact)) {
               artChangeItems.put(changeItem.getArtId(), changeItem);
            }
         }

         int swAdded = 0;
         int swModified = 0;
         int swDeleted = 0;
         int subAdded = 0;
         int subModified = 0;
         int subDeleted = 0;
         int headAdded = 0;
         int headModified = 0;
         int headDeleted = 0;
         int implAdded = 0;
         int implModified = 0;
         int implDeleted = 0;

         for (ChangeItem changeItem : attrChangeItems) {
            BranchId workingBranch = atsApi.getBranchService().getBranch(reqWorkflow);
            BranchId parentBranch = atsApi.getBranchService().getParentBranch(workingBranch);
            ArtifactTypeToken artType = ArtifactTypeToken.SENTINEL;
            artType = orcsApi.getQueryFactory().fromBranch(parentBranch).includeDeletedAttributes(
               true).includeDeletedArtifacts(true).andId(
                  changeItem.getArtId()).asArtifactTokenOrSentinel().getArtifactType();
            if (artType.isInvalid()) {
               artType = orcsApi.tokenService().getArtifactTypeOrSentinel(
                  artChangeItems.get(changeItem.getArtId()).getItemTypeId().getId());
               if (artType.isInvalid()) {
                  artType = orcsApi.getQueryFactory().fromBranch(workingBranch).includeDeletedAttributes(
                     true).includeDeletedArtifacts(true).andId(
                        changeItem.getArtId()).asArtifactTokenOrSentinel().getArtifactType();
                  if (artType.isInvalid()) {
                     continue;
                  }
               }
            }
            if (artType.inheritsFrom(CoreArtifactTypes.AbstractSoftwareRequirement)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  swAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  swModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  swDeleted++;
               }
            } else if (artType.inheritsFrom(CoreArtifactTypes.AbstractSubsystemRequirement)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  subAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  subModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  subDeleted++;
               }
            } else if (artType.inheritsFrom(CoreArtifactTypes.AbstractHeading)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  headAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  headModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  headDeleted++;
               }
            } else if (artType.inheritsFrom(CoreArtifactTypes.AbstractImplementationDetails)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  implAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  implModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  implDeleted++;
               }
            } else {
               continue;
            }
         }

         buffer[7] = swAdded;
         buffer[8] = swModified;
         buffer[9] = swDeleted;
         buffer[10] = subAdded;
         buffer[11] = subModified;
         buffer[12] = subDeleted;
         buffer[13] = headAdded;
         buffer[14] = headModified;
         buffer[15] = headDeleted;
         buffer[16] = implAdded;
         buffer[17] = implModified;
         buffer[18] = implDeleted;

         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      writer.endSheet();
   }
}