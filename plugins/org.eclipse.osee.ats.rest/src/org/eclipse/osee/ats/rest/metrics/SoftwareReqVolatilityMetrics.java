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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.QueryOption;
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
   private final boolean countImpacts;
   Collection<IAtsTask> tasksMissingChangeType = new ArrayList<>();
   private BranchId baselineBranchFromVersion = BranchId.SENTINEL;

   private ExcelXmlWriter writer;

   private final String VERIFY = "Verify";
   private final String DEMONSTRATE = "Demonstrate";
   private final String CLOSED = "Closed";

   private final SoftwareReqVolatilityId[] reportColumns = {
      SoftwareReqVolatilityId.ACT,
      SoftwareReqVolatilityId.TW,
      SoftwareReqVolatilityId.ActionName,
      SoftwareReqVolatilityId.Program,
      SoftwareReqVolatilityId.Build,
      SoftwareReqVolatilityId.Date,
      SoftwareReqVolatilityId.VerifyOrComplete,
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
      SoftwareReqVolatilityId.DeletedImpl,
      SoftwareReqVolatilityId.NumSafetyReq,
      SoftwareReqVolatilityId.NumSecurityReq};

   public SoftwareReqVolatilityMetrics(OrcsApi orcsApi, AtsApi atsApi, String targetVersion, Date startDate, Date endDate, boolean allTime, boolean countImpacts) {
      this.orcsApi = orcsApi;
      this.atsApi = atsApi;
      this.programVersion = null;
      this.targetVersion = targetVersion;
      this.startDate = startDate;
      this.endDate = endDate;
      this.allTime = allTime;
      this.countImpacts = countImpacts;
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
         int columnCount = countImpacts ? reportColumns.length : reportColumns.length - 2;
         writer.startSheet("SRV", columnCount);
         fillActionableData(reqWorkflows);
      }
   }

   private Collection<IAtsTeamWorkflow> getDatedWorkflows() {
      ArtifactToken versionId = atsApi.getQueryService().getArtifactFromTypeAndAttribute(AtsArtifactTypes.Version,
         CoreAttributeTypes.Name, targetVersion, atsApi.getAtsBranch());
      IAtsVersion version = atsApi.getVersionService().getVersionById(versionId);
      baselineBranchFromVersion = version.getBaselineBranch();
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

            Date stateStartedDate = new Date();

            if (isReqWf) {

               //Record wf if any of these states. Prioritized as Verify > Demonstrate > Completed > Closed
               if (atsApi.getWorkItemService().stateExists(workflow, VERIFY)) {
                  stateStartedDate = getStateStartedDate(workflow, VERIFY);
               } else if (atsApi.getWorkItemService().stateExists(workflow, DEMONSTRATE)) {
                  stateStartedDate = getStateStartedDate(workflow, DEMONSTRATE);
               } else if (workflow.isCompleted()) {
                  stateStartedDate = workflow.getCompletedDate();
               } else if (atsApi.getWorkItemService().stateExists(workflow, CLOSED)) {
                  stateStartedDate = getStateStartedDate(workflow, CLOSED);
               } else {
                  continue;
               }
               if (allTime || ((stateStartedDate.after(startDate) && stateStartedDate.before(endDate)))) {
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
         boolean attributeExists =
            atsApi.getAttributeResolver().hasAttribute(reqWorkflow, CoreAttributeTypes.BranchDiffData);
         if (attributeExists) {
            changeReportData =
               atsApi.getAttributeResolver().getSoleAttributeValue(reqWorkflow, CoreAttributeTypes.BranchDiffData, "");
         }
         if (changeReportData.isEmpty()) {
            changeItems = orcsApi.getBranchOps().compareBranch(branch);
            if (!changeItems.isEmpty() && !attributeExists) {
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
      int numColumns = countImpacts ? reportColumns.length : reportColumns.length - 2;
      int numRows = 0;
      Object[] buffer = new Object[numColumns];
      for (int i = 0; i < numColumns; ++i) {
         buffer[i] = reportColumns[i].getDisplayName();
      }
      writer.writeRow(buffer);

      for (IAtsTeamWorkflow reqWorkflow : reqWorkflows) {
         List<ChangeItem> changeItems = getChangeItems(reqWorkflow);
         try {
            //wf is 'Complete' if any of these states. Prioritized as Verify > Demonstrate > Completed > Closed
            if (atsApi.getWorkItemService().stateExists(reqWorkflow, VERIFY)) {
               buffer[6] = getStateStartedDate(reqWorkflow, VERIFY);
            } else if (atsApi.getWorkItemService().stateExists(reqWorkflow, DEMONSTRATE)) {
               buffer[6] = getStateStartedDate(reqWorkflow, DEMONSTRATE);
            } else if (reqWorkflow.isCompleted()) {
               buffer[6] = reqWorkflow.getCompletedDate();
            } else if (atsApi.getWorkItemService().stateExists(reqWorkflow, CLOSED)) {
               buffer[6] = getStateStartedDate(reqWorkflow, CLOSED);
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
               ChangeIgnoreType.NONE) && (changeItem.getItemTypeId().equals(
                  CoreAttributeTypes.WordTemplateContent) || changeItem.getItemTypeId().equals(
                     CoreAttributeTypes.PlainTextContent))) {
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
         int numSafety = 0;
         int numSecurity = 0;

         for (ChangeItem changeItem : attrChangeItems) {
            BranchToken workingBranch = atsApi.getBranchService().getBranch(reqWorkflow);
            BranchToken parentBranch = atsApi.getBranchService().getParentBranch(workingBranch);
            ArtifactToken artToken = orcsApi.getQueryFactory().fromBranch(parentBranch).includeDeletedAttributes(
               true).includeDeletedArtifacts(true).andId(changeItem.getArtId()).asArtifactTokenOrSentinel();
            ArtifactTypeToken artType = artToken.getArtifactType();
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
               if (countImpacts) {
                  if (isSafetyRelated(artToken)) {
                     ++numSafety;
                  }
                  if (isSecurityRelated(artToken)) {
                     ++numSecurity;
                  }
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
         if (countImpacts) {
            buffer[19] = numSafety;
            buffer[20] = numSecurity;
         }

         try {
            writer.writeRow(buffer);
            ++numRows;
         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      if (countImpacts) {
         Integer numSoftwareReqs = countAbstractRequirements();
         try {
            buffer[0] = " ";
            buffer[1] = " ";
            buffer[2] = " ";
            buffer[3] = " ";
            buffer[4] = " ";
            buffer[5] = " ";
            buffer[6] = " ";
            buffer[7] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[8] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[9] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[10] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[11] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[12] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[13] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[14] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[15] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[16] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[17] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[18] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[19] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            buffer[20] = String.format("=SUM(R[-%d]C:R[-1]C)", numRows);
            writer.writeRow(buffer);

            buffer[0] = " ";
            buffer[1] = " ";
            buffer[2] = " ";
            buffer[3] = " ";
            buffer[4] = " ";
            buffer[5] = " ";
            buffer[6] = "Total SW Reqs";
            buffer[7] = numSoftwareReqs.toString();
            buffer[8] = " ";
            buffer[9] = " ";
            buffer[10] = " ";
            buffer[11] = " ";
            buffer[12] = " ";
            buffer[13] = " ";
            buffer[14] = " ";
            buffer[15] = " ";
            buffer[16] = " ";
            buffer[17] = " ";
            buffer[18] = " ";
            buffer[19] = " ";
            buffer[20] = " ";
            writer.writeRow(buffer);

         } catch (IOException ex) {
            OseeCoreException.wrapAndThrow(ex);
         }
      }
      writer.endSheet();
   }

   private boolean isSecurityRelated(ArtifactToken artToken) {
      return orcsApi.getQueryFactory().fromBranch(baselineBranchFromVersion).andId(artToken).andExists(
         CoreAttributeTypes.PotentialSecurityImpact).exists();
   }

   private boolean isSafetyRelated(ArtifactToken artToken) {
      return orcsApi.getQueryFactory().fromBranch(baselineBranchFromVersion).andId(artToken).and(
         CoreAttributeTypes.IDAL, List.of("A", "B", "C"), QueryOption.CONTAINS_MATCH_ANY).exists();
   }

   private int countAbstractRequirements() {
      return orcsApi.getQueryFactory().fromBranch(baselineBranchFromVersion).andIsOfType(
         CoreArtifactTypes.SoftwareRequirementMsWord).getCount();
   }

   private Date getStateStartedDate(IAtsWorkItem teamWf, String stateName) {
      try {
         IAtsLogItem stateStartedData = atsApi.getWorkItemService().getStateStartedData(teamWf, stateName);
         return stateStartedData.getDate();
      } catch (Exception ex) {
         //Do Nothing
      }
      return null;
   }
}