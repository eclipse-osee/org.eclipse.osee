/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.rest.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.review.IAtsAbstractReview;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.dto.DiffReportEndpointDto;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
public class AtsTeamWfEndpointImpl implements AtsTeamWfEndpointApi {

   private final AtsApi atsApi;
   private final OrcsApi orcsApi;

   public AtsTeamWfEndpointImpl(AtsApi atsApi, OrcsApi orcsApi) {
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   @Override
   public List<ChangeItem> getChangeData(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (workItem == null) {
         return Collections.<ChangeItem> emptyList();
      }
      if (!workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      IAtsTeamWorkflow teamWf = workItem.getParentTeamWorkflow();
      TransactionToken trans = atsApi.getBranchService().getEarliestTransactionId(teamWf);
      if (trans.isValid()) {
         return atsApi.getBranchService().getChangeData(trans);
      }
      BranchId branch = atsApi.getBranchService().getWorkingBranch(teamWf);
      if (branch.isValid()) {
         return atsApi.getBranchService().getChangeData(branch);
      }
      return Collections.<ChangeItem> emptyList();
   }

   @Override
   public IAtsTeamWorkflow getTeamWorkflow(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (workItem == null || !workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      return (IAtsTeamWorkflow) workItem;
   }

   @Override
   public Collection<IAtsTeamWorkflow> getTeamWorkflows(String ids) {
      List<IAtsWorkItem> workItems = atsApi.getQueryService().getWorkItemsByIds(ids);
      List<IAtsTeamWorkflow> teamWfs = new ArrayList<>();
      for (IAtsWorkItem workItem : workItems) {
         if (workItem.isTeamWorkflow()) {
            teamWfs.add((IAtsTeamWorkflow) workItem);
         }
      }
      return teamWfs;
   }

   @Override
   public Collection<IAtsVersion> getVersionsbyTeamDefinition(String aiId, String sort) {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItem(aiId);
      IAtsTeamDefinition impactedTeamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
      IAtsTeamDefinition teamDefHoldingVersions =
         atsApi.getTeamDefinitionService().getTeamDefinitionHoldingVersions(impactedTeamDef);

      ArrayList<IAtsVersion> versionsList =
         new ArrayList<>(atsApi.getVersionService().getVersions(teamDefHoldingVersions));
      if ("true".equals(sort)) {
         Collections.sort(versionsList);
      }
      return versionsList;
   }

   @Override
   public XResultData addChangeIds(String workItemId, String teamId, List<String> changeIds) {
      XResultData rd = new XResultData();
      rd.setTitle("Add Change Ids: " + changeIds);
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(workItemId);

      if (workItem == null) {
         rd.errorf("%s is not a valid team workflow id.", workItemId);
         return rd;
      }
      if (!workItem.isTeamWorkflow()) {
         rd.errorf("%s is not a valid team workflow id.", workItem.toStringWithId());
         return rd;
      }
      IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) workItem;
      if (teamWf.isCancelled()) {
         rd.errorf("%s is cancelled and cannot be updated.", teamWf.toStringWithId());
         return rd;
      }
      TeamDefinition passedTeamDef =
         atsApi.getTeamDefinitionService().getTeamDefinitionById(ArtifactId.valueOf(teamId));
      if (passedTeamDef == null) {
         rd.errorf("%s is an invalid Team Definition Id", teamId);
         return rd;
      }
      if (!teamWf.getTeamDefinition().getIdString().equals(teamId)) {
         if (!passedTeamDef.getChildrenTeamDefs().contains(teamWf.getTeamDefinition())) {
            rd.errorf(
               "Workflow %s has a Team Definition %s which does not match/nor is the child of the passed in Team Definition %s",
               teamWf.toStringWithId(), teamWf.getTeamDefinition().toStringWithId(), passedTeamDef.toStringWithId());
            return rd;
         }
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Add Change Id(s)");
      Set<String> distinctChangeIds = new HashSet<String>(
         atsApi.getAttributeResolver().getAttributesToStringList(workItem, CoreAttributeTypes.GitChangeId));
      distinctChangeIds.addAll(changeIds);
      List<String> updatedChangeIdsList = new ArrayList<>(distinctChangeIds);
      changes.setAttributeValuesAsStrings(workItem, CoreAttributeTypes.GitChangeId, updatedChangeIdsList);
      changes.executeIfNeeded();
      return rd;
   }

   @Override
   public List<IAtsGoal> getGoals(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (workItem == null) {
         throw new NullPointerException();
      }
      if (!workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      Collection<ArtifactToken> artifacts =
         atsApi.getRelationResolver().getRelated(workItem.getArtifactId(), AtsRelationTypes.Goal_Goal);
      List<IAtsGoal> goalList = new ArrayList<IAtsGoal>();
      for (ArtifactToken art : artifacts) {
         goalList.add(atsApi.getWorkItemService().getGoal(art));
      }
      return goalList;
   }

   @Override
   public Collection<ArtifactToken> getWfByRelease(String releaseName) {
      Collection<ArtifactToken> releases =
         atsApi.getQueryService().createQuery(AtsArtifactTypes.ReleaseArtifact).andName(releaseName).getArtifacts();
      if (releases.size() > 1) {
         throw new OseeCoreException("Release Name [%s] matches multiple releases", releaseName);
      } else if (releases.isEmpty()) {
         throw new OseeCoreException("No Releases found with name [%s]", releaseName);
      }
      ArtifactToken release = releases.iterator().next();
      IRelationResolver relationResolver = atsApi.getRelationResolver();
      return relationResolver.getRelated(release, AtsRelationTypes.TeamWorkflowToRelease_TeamWorkflow);
   }

   @Override
   public XResultData relateReleaseToWorkflow(String build, List<String> changeIds) {
      XResultData rd = new XResultData();
      try {
         rd.setTitle("Add Workflow to Release Relations");

         Collection<ArtifactToken> allWorkflows =
            atsApi.getQueryService().createQuery(AtsArtifactTypes.TeamWorkflow).andAttr(CoreAttributeTypes.GitChangeId,
               changeIds).getArtifacts();

         IAtsChangeSet changes = atsApi.createChangeSet("Add Build Incorporation(s)");
         Collection<IAtsWorkItem> workItems = new ArrayList<>();
         for (IAtsWorkItem workItem : atsApi.getWorkItemService().getWorkItems(allWorkflows)) {
            if (!workItem.isTeamWorkflow()) {
               rd.errorf("%s is not a valid team workflow id.", workItem.toStringWithId());
               return rd;
            }

            workItems.add(workItem);
            Set<String> distinctChangeIds = new HashSet<String>(
               atsApi.getAttributeResolver().getAttributesToStringList(workItem, CoreAttributeTypes.GitChangeId));
            boolean isBuildValid = false;
            for (String changeId : changeIds) {
               if (distinctChangeIds.contains(changeId)) {
                  isBuildValid = true;
                  break;
               }
            }
            Collection<ArtifactToken> release =
               atsApi.getQueryService().createQuery(AtsArtifactTypes.ReleaseArtifact).andName(build).getArtifacts();
            if (isBuildValid) {
               if (release.size() > 1) {
                  rd.errorf("Release Name [%s] matches multiple releases", build);
                  return rd;
               }
               if (release.isEmpty()) {
                  rd.errorf("No Releases found with name [%s]", build);
                  return rd;
               }
               IRelationResolver relationResolver = atsApi.getRelationResolver();
               if (!relationResolver.areRelated(workItem.getArtifactId(),
                  AtsRelationTypes.TeamWorkflowToRelease_Release, release.stream().findFirst().get())) {
                  changes.relate(workItem, AtsRelationTypes.TeamWorkflowToRelease_Release,
                     release.stream().findFirst().get());
               }
            }
         }
         TransactionId transId = changes.executeIfNeeded();
         rd.setTxId(transId.getIdString());
      } catch (Exception Ex) {
         rd.errorf("Exception %s", Lib.exceptionToString(Ex));
      }

      return rd;
   }

   @Override
   public Collection<ChangeTypes> getChangeTypes(String id, String sort) {
      ArtifactToken artifact = atsApi.getQueryService().getArtifact(Long.valueOf(id));
      IAtsObject atsObj =
         artifact.isOfType(AtsArtifactTypes.ActionableItem) ? atsApi.getActionableItemService().getActionableItemById(
            artifact) : artifact.isOfType(
               AtsArtifactTypes.TeamDefinition) ? atsApi.getTeamDefinitionService().getTeamDefinitionById(
                  artifact) : artifact.isOfType(
                     AtsArtifactTypes.WorkDefinition) ? atsApi.getWorkItemService().getWorkItem(artifact) : null;

      List<ChangeTypes> changeTypes = atsApi.getWorkItemService().getChangeTypeOptions(atsObj);
      if ("true".equals(sort)) {
         Collections.sort(changeTypes);
      }

      return changeTypes;
   }

   @Override
   public DiffReportEndpointDto getDiffReportEndpoint() {
      ArtifactReadable art =
         orcsApi.getQueryFactory().fromBranch(BranchId.valueOf(CoreBranches.COMMON.getId())).andIsOfType(
            CoreArtifactTypes.DiffReportEndPoint).asArtifactOrSentinel();
      return new DiffReportEndpointDto(art);
   }

   @Override
   public Collection<IAtsAbstractReview> getReviews(String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (workItem == null || !workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }

      Collection<IAtsAbstractReview> reviews = atsApi.getWorkItemService().getReviews((IAtsTeamWorkflow) workItem);

      return reviews;
   }
}