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
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
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
   private final boolean implDetails;
   Collection<IAtsTask> tasksMissingChangeType = new ArrayList<>();

   private ExcelXmlWriter writer;

   Pattern UI_NAME = Pattern.compile("\\{.*\\}");
   Pattern UI_IMPL = Pattern.compile("\\(Impl Details\\)");

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
      SoftwareReqVolatilityId.AddedImpl,
      SoftwareReqVolatilityId.ModifiedImpl,
      SoftwareReqVolatilityId.DeletedImpl};

   public SoftwareReqVolatilityMetrics(OrcsApi orcsApi, AtsApi atsApi, String targetVersion, Date startDate, Date endDate, boolean allTime, boolean implDetails) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.startDate = startDate;
      this.endDate = endDate;
      this.allTime = allTime;
      this.implDetails = implDetails;
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
         if ((workflow.isWorkType(WorkType.Requirements) && workflow.isCompleted())) {
            if (allTime || (workflow.getCompletedDate().after(startDate) && workflow.getCompletedDate().before(
               endDate))) {
               reqWorkflows.add(workflow);
            }
         }
      }

      programVersion = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).getName();
      return reqWorkflows;
   }

   private void fillActionableData(Collection<IAtsTeamWorkflow> reqWorkflows) throws IOException {
      int numColumns = reportColumns.length;
      if (!implDetails) {
         numColumns -= 3;
      }
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = reportColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsTeamWorkflow reqWorkflow : reqWorkflows) {
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

         String changeReportData =
            atsApi.getAttributeResolver().getSoleAttributeValue(reqWorkflow, CoreAttributeTypes.BranchDiffData, "");
         if (changeReportData.isEmpty()) {
            continue;
         }

         List<ChangeItem> changeItems = JsonUtil.readValues(changeReportData, ChangeItem.class);

         buffer[0] = reqWorkflow.getParentAction().getAtsId();
         buffer[1] = reqWorkflow.getAtsId();
         buffer[2] = reqWorkflow.getName();
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
            if (artType.inheritsFrom(CoreArtifactTypes.AbstractImplementationDetails)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  implAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  implModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  implDeleted++;
               }
            } else if (artType.inheritsFrom(CoreArtifactTypes.AbstractSoftwareRequirement)) {
               ModificationType modType = changeItem.getNetChange().getModType();
               if (modType.equals(ModificationType.NEW)) {
                  swAdded++;
               } else if (modType.equals(ModificationType.MODIFIED) || modType.equals(ModificationType.MERGED)) {
                  swModified++;
               } else if (modType.equals(ModificationType.DELETED) || modType.equals(
                  ModificationType.ARTIFACT_DELETED)) {
                  swDeleted++;
               }
            } else {
               continue;
            }
         }
         buffer[7] = swAdded;
         buffer[8] = swModified;
         buffer[9] = swDeleted;
         if (implDetails) {
            buffer[10] = implAdded;
            buffer[11] = implModified;
            buffer[12] = implDeleted;
         }
         try {
            writer.writeRow(buffer);
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      writer.endSheet();
   }
}