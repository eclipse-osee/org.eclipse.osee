/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionServiceImpl implements IAtsTeamDefinitionService {

   private final AtsApi atsApi;

   public TeamDefinitionServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionById(ArtifactId teamDefId) {
      IAtsTeamDefinition teamDef = null;
      if (teamDefId instanceof IAtsTeamDefinition) {
         teamDef = (IAtsTeamDefinition) teamDefId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(teamDefId);
         if (art != null && art.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef = new TeamDefinition(atsApi.getLogger(), atsApi, art);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(workItem,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isValid()) {
         teamDef = getTeamDefinitionById(teamDefId);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<>();
      for (ArtifactId verArt : atsApi.getRelationResolver().getRelated(teamDef,
         AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         versions.add(atsApi.getVersionService().getVersion(verArt));
      }
      return versions;
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsTeamDefinition teamDef) {
      return atsApi.getVersionService().getTeamDefinitionHoldingVersions(teamDef);
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program) {
      return atsApi.getProgramService().getTeamDefHoldingVersions(program);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(String name) {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefArt =
         atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.TeamDefinition, name);
      if (teamDefArt.isValid()) {
         teamDef = getTeamDefinitionById(teamDefArt);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (ArtifactId atsTeamArt : atsApi.getRelationResolver().getRelated(agileTeam,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         teamDefs.add(getTeamDefinitionById(atsTeamArt));
      }
      return teamDefs;
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, long id, IAtsChangeSet changes, AtsApi atsApi) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.TeamDefinition, name, id);
      return new TeamDefinition(atsApi.getLogger(), atsApi, artifact);
   }

   @Override
   public IAtsTeamDefinition createTeamDefinition(String name, IAtsChangeSet changes, AtsApi atsApi) {
      return createTeamDefinition(name, Lib.generateArtifactIdAsInt(), changes, atsApi);
   }

   @Override
   public Collection<WorkType> getWorkTypes(IAtsTeamDefinition teamDef) {
      Collection<WorkType> workTypes = new HashSet<>();
      Collection<String> workTypeStrs =
         atsApi.getAttributeResolver().getAttributeValues(teamDef, AtsAttributeTypes.WorkType);
      for (String workTypeStr : workTypeStrs) {
         try {
            WorkType workType = WorkType.valueOf(workTypeStr);
            workTypes.add(workType);
         } catch (Exception ex) {
            // do nothing
         }
      }
      return workTypes;
   }

   @Override
   public boolean isWorkType(IAtsWorkItem workItem, WorkType workType) {
      return getWorkTypes(getTeamDefinition(workItem)).contains(workType);
   }

   @Override
   public Collection<IAtsUser> getLeads(IAtsTeamDefinition teamDef, Collection<IAtsActionableItem> actionableItems) {
      Set<IAtsUser> leads = new HashSet<>();
      for (IAtsActionableItem aia : actionableItems) {
         if (teamDef.equals(aia.getTeamDefinitionInherited())) {
            // If leads are specified for this aia, add them
            Collection<IAtsUser> leads2 = atsApi.getActionableItemService().getLeads(aia);
            if (leads2.size() > 0) {
               leads.addAll(leads2);
            } else {
               if (aia.getTeamDefinitionInherited() != null) {
                  Collection<IAtsUser> leads3 = getLeads(aia.getTeamDefinitionInherited());
                  leads.addAll(leads3);
               }
            }
         }
      }
      if (leads.isEmpty()) {
         Collection<IAtsUser> leads2 = getLeads(teamDef);
         leads.addAll(leads2);
      }
      return leads;
   }

   @Override
   public Collection<IAtsUser> getLeads(IAtsTeamDefinition teamDef) {
      return AtsApiService.get().getUserService().getRelatedUsers(atsApi, teamDef.getStoreObject(),
         AtsRelationTypes.TeamLead_Lead);
   }

   @Override
   public Collection<IAtsUser> getMembers(IAtsTeamDefinition teamDef) {
      return AtsApiService.get().getUserService().getRelatedUsers(atsApi, teamDef.getArtifactToken(),
         AtsRelationTypes.TeamMember_Member);
   }

   @Override
   public Collection<IAtsUser> getSubscribed(IAtsTeamDefinition teamDef) {
      return AtsApiService.get().getUserService().getRelatedUsers(atsApi, teamDef.getArtifactToken(),
         AtsRelationTypes.SubscribedUser_User);
   }

   @Override
   public Collection<IAtsUser> getMembersAndLeads(IAtsTeamDefinition teamDef) {
      Set<IAtsUser> results = new HashSet<>();
      results.addAll(getLeads(teamDef));
      results.addAll(getMembers(teamDef));
      return results;
   }

   @Override
   public boolean isAllowCommitBranch(IAtsTeamDefinition teamDef) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(teamDef, AtsAttributeTypes.AllowCommitBranch, false);
   }

   @Override
   public Result isAllowCommitBranchInherited(IAtsTeamDefinition teamDef) {
      if (!isAllowCommitBranch(teamDef)) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (getBaselineBranchId(teamDef).isInvalid()) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public boolean isAllowCreateBranch(IAtsTeamDefinition teamDef) {
      return atsApi.getAttributeResolver().getSoleAttributeValue(teamDef, AtsAttributeTypes.AllowCreateBranch, false);
   }

   @Override
   public Result isAllowCreateBranchInherited(IAtsTeamDefinition teamDef) {
      if (!isAllowCreateBranch(teamDef)) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (getBaselineBranchId(teamDef).isInvalid()) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public BranchId getBaselineBranchId(IAtsTeamDefinition teamDef) {
      return BranchId.valueOf(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamDef, AtsAttributeTypes.BaselineBranchId, "0"));
   }

   @Override
   public BranchId getTeamBranchId(IAtsTeamDefinition teamDef) {
      BranchId branch = getBaselineBranchId(teamDef);
      if (branch.isValid()) {
         return branch;
      } else {
         IAtsTeamDefinition parentTeamDef = getParentTeamDef(teamDef);
         return getTeamBranchId(parentTeamDef);
      }
   }

   @Override
   public IAtsTeamDefinition getParentTeamDef(IAtsTeamDefinition teamDef) {
      ArtifactToken parentArt = atsApi.getRelationResolver().getParent(teamDef.getArtifactToken());
      if (parentArt != null && parentArt.notEqual(AtsArtifactToken.HeadingFolder)) {
         return atsApi.getTeamDefinitionService().getTeamDefinitionById(parentArt);
      }
      return null;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions(IAtsTeamDefinition teamDef) {
      Set<IAtsTeamDefinition> children = new HashSet<>();
      for (ArtifactId childArt : atsApi.getRelationResolver().getRelated(teamDef.getStoreObject(),
         CoreRelationTypes.DefaultHierarchical_Child)) {
         IAtsTeamDefinition childTeamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(childArt);
         if (childTeamDef != null) {
            children.add(childTeamDef);
         }
      }
      return children;
   }

   @Override
   public boolean isTeamUsesVersions(IAtsTeamDefinition teamDef) {
      return atsApi.getVersionService().isTeamUsesVersions(teamDef);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions(IAtsTeamDefinition teamDef) {
      return atsApi.getVersionService().getTeamDefinitionHoldingVersions(teamDef);
   }

   @Override
   public Collection<String> getRules(IAtsTeamDefinition teamDef) {
      Collection<String> rules =
         atsApi.getAttributeResolver().getAttributesToStringList(teamDef, AtsAttributeTypes.RuleDefinition);
      return rules;
   }

   @Override
   public boolean hasRule(IAtsTeamDefinition teamDef, String rule) {
      boolean result = false;
      for (String rule2 : getRules(teamDef)) {
         if (rule.equals(rule2)) {
            result = true;
            break;
         }
      }
      return result;
   }

}
