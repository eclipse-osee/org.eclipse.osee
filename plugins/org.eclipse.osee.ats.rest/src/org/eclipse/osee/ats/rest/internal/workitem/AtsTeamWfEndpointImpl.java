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

import static org.eclipse.osee.framework.core.data.OseeClient.OSEE_ACCOUNT_ID;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.AtsTeamWfEndpointApi;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
@Path("teamwf")
public class AtsTeamWfEndpointImpl implements AtsTeamWfEndpointApi {

   private final AtsApi atsApi;

   public AtsTeamWfEndpointImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   @GET
   @Path("{id}/changedata")
   @Produces({MediaType.APPLICATION_JSON})
   public List<ChangeItem> getChangeData(@PathParam("id") String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
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
   @GET
   @Path("{id}")
   @Produces({MediaType.APPLICATION_JSON})
   public IAtsTeamWorkflow getTeamWorkflow(@PathParam("id") String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
      if (!workItem.isTeamWorkflow()) {
         throw new UnsupportedOperationException();
      }
      return (IAtsTeamWorkflow) workItem;
   }

   @Override
   @GET
   @Path("{aiId}/version")
   @Produces({MediaType.APPLICATION_JSON})
   public Collection<IAtsVersion> getVersionsbyTeamDefinition(@PathParam("aiId") String aiId) {
      IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItem(aiId);
      IAtsTeamDefinition impactedTeamDef = atsApi.getTeamDefinitionService().getImpactedTeamDef(ai);
      IAtsTeamDefinition teamDefHoldingVersions =
         atsApi.getTeamDefinitionService().getTeamDefinitionHoldingVersions(impactedTeamDef);

      return atsApi.getVersionService().getVersions(teamDefHoldingVersions);
   }

   @Override
   @PUT
   @Path("{id}/addchangeids/{teamId}")
   @Produces({MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_JSON})
   public XResultData addChangeIds(@PathParam("id") String workItemId, @PathParam("teamId") String teamId, @HeaderParam(OSEE_ACCOUNT_ID) UserId userId, List<String> changeIds) {
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
      if (teamWf.isCompletedOrCancelled()) {
         rd.errorf("%s is completed/cancelled and cannot be updated.", teamWf.toStringWithId());
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
      if (userId.isInvalid()) {
         rd.errorf("%s is an invalid ATS userId", userId);
         return rd;
      }
      AtsUser asUser = atsApi.getUserService().getUserByAccountId(userId);
      if (asUser.isInvalid()) {
         rd.errorf("%s is an invalid ATS userId", userId);
         return rd;
      }
      IAtsChangeSet changes = atsApi.createChangeSet("Add Change Id(s)", asUser);
      Set<String> distinctChangeIds = new HashSet<String>(
         atsApi.getAttributeResolver().getAttributesToStringList(workItem, CoreAttributeTypes.GitChangeId));
      distinctChangeIds.addAll(changeIds);
      List<String> updatedChangeIdsList = new ArrayList<>(distinctChangeIds);
      changes.setAttributeValuesAsStrings(workItem, CoreAttributeTypes.GitChangeId, updatedChangeIdsList);
      changes.executeIfNeeded();
      return rd;
   }

   @Override
   @GET
   @Path("{id}/goal")
   @Produces({MediaType.APPLICATION_JSON})
   public List<IAtsGoal> getGoals(@PathParam("id") String id) {
      IAtsWorkItem workItem = atsApi.getWorkItemService().getWorkItemByAnyId(id);
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
}
