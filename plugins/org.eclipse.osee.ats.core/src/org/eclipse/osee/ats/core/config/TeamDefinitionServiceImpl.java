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

import static org.eclipse.osee.ats.api.data.AtsRelationTypes.TeamActionableItem_ActionableItem;
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
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
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
   public TeamDefinition getTeamDefinitionById(ArtifactId teamDefId) {
      TeamDefinition teamDef = null;
      if (teamDefId instanceof TeamDefinition) {
         teamDef = (TeamDefinition) teamDefId;
      }
      if (teamDef == null) {
         teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());
      }
      if (teamDef == null) {
         // Don't want to load artifacts on client.  Request from server.
         if (atsApi.isIde()) {
            teamDef =
               atsApi.getServerEndpoints().getConfigEndpoint().getTeamDefinition(ArtifactId.valueOf(teamDefId.getId()));
            teamDef.setAtsApi(atsApi);
         } else {
            ArtifactToken teamDefArt = atsApi.getQueryService().getArtifact(teamDefId);
            if (teamDefArt.isValid()) {
               TeamDefinition teamDef2 = createTeamDefinition(teamDefArt);
               atsApi.getConfigService().getConfigurations().addTeamDef(teamDef2);
               teamDef = teamDef2;
            }
         }
      }
      return teamDef;
   }

   @Override
   public TeamDefinition createTeamDefinition(ArtifactToken teamDefArt) {
      TeamDefinition teamDef = new TeamDefinition(teamDefArt, atsApi);
      teamDef.setName(teamDefArt.getName());
      teamDef.setId(teamDefArt.getId());
      teamDef.setGuid(teamDefArt.getGuid());
      teamDef.setActive(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt, AtsAttributeTypes.Active, true));
      teamDef.setWorkType(
         atsApi.getAttributeResolver().getSoleAttributeValue(teamDefArt, AtsAttributeTypes.WorkType, ""));
      Collection<ArtifactToken> ais =
         atsApi.getRelationResolver().getRelated(teamDefArt, TeamActionableItem_ActionableItem);
      for (ArtifactToken ai : ais) {
         teamDef.addAi(ai);
      }
      ArtifactToken parent = atsApi.getRelationResolver().getParent(teamDefArt);
      if (parent != null) {
         teamDef.setParentId(parent.getId());
      }
      for (ArtifactToken child : atsApi.getRelationResolver().getChildren(teamDefArt)) {
         if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
            teamDef.getChildren().add(child.getId());
         }
      }
      return teamDef;
   }

   @Override
   public TeamDefinition getTeamDefinition(IAtsWorkItem workItem) {
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(workItem,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      TeamDefinition teamDef = null;
      if (teamDefId.isValid()) {
         teamDef = getTeamDefinitionById(teamDefId);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<>();
      TeamDefinition teamD = atsApi.getConfigService().getConfigurations().getTeamDef(teamDef);
      for (Long verId : teamD.getVersions()) {
         Version version = atsApi.getConfigService().getConfigurations().getIdToVersion().get(verId);
         versions.add(version);
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
      for (TeamDefinition teamD : atsApi.getConfigService().getConfigurations().getIdToTeamDef().values()) {
         if (teamD.getName().equals(name)) {
            teamDef = teamD;
            break;
         }
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (ArtifactId teamDef : atsApi.getRelationResolver().getRelated(agileTeam,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         teamDefs.add(getTeamDefinitionById(teamDef));
      }
      return teamDefs;
   }

   @Override
   public TeamDefinition createTeamDefinition(String name, long id, IAtsChangeSet changes) {
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.TeamDefinition, name, id);
      return createTeamDefinition(artifact);
   }

   @Override
   public TeamDefinition createTeamDefinition(String name, IAtsChangeSet changes) {
      return createTeamDefinition(name, Lib.generateArtifactIdAsInt(), changes);
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
         if (teamDef.equals(aia.getAtsApi().getActionableItemService().getTeamDefinitionInherited(aia))) {
            // If leads are specified for this aia, add them
            Collection<IAtsUser> leads2 = atsApi.getActionableItemService().getLeads(aia);
            if (leads2.size() > 0) {
               leads.addAll(leads2);
            } else {
               if (aia.getAtsApi().getActionableItemService().getTeamDefinitionInherited(aia) != null) {
                  Collection<IAtsUser> leads3 =
                     getLeads(aia.getAtsApi().getActionableItemService().getTeamDefinitionInherited(aia));
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
   public TeamDefinition getParentTeamDef(IAtsTeamDefinition teamDef) {
      TeamDefinition teamD = atsApi.getConfigService().getConfigurations().getTeamDef(teamDef);
      TeamDefinition parent = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamD.getParentId());
      return parent;
   }

   @Override
   public Collection<TeamDefinition> getChildrenTeamDefinitions(IAtsTeamDefinition teamDef) {
      TeamDefinition teamD = atsApi.getConfigService().getConfigurations().getTeamDef(teamDef);
      return teamD.getChildrenTeamDefs();
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

   @Override
   public Collection<TeamDefinition> getTopLevelTeamDefinitions(Active active) {
      List<TeamDefinition> teamDefs = new ArrayList<>();
      IAtsTeamDefinition topTeamDef = getTopTeamDefinitionOrSentinel();
      if (topTeamDef.isValid()) {
         TeamDefinition tTeamDef = getTeamDefinitionById(topTeamDef.getStoreObject());
         return tTeamDef.getChildrenTeamDefs();
      }
      return teamDefs;
   }

   @Override
   public List<IAtsTeamDefinition> getActive(Collection<IAtsTeamDefinition> teamDefs, Active active) {
      List<IAtsTeamDefinition> results = new ArrayList<>();
      for (IAtsTeamDefinition teamDef : teamDefs) {
         if (active == Active.Both) {
            results.add(teamDef);
         } else {
            // assume active unless otherwise specified
            boolean attributeActive = teamDef.isActive();
            if (active == Active.Active && attributeActive) {
               results.add(teamDef);
            } else if (active == Active.InActive && !attributeActive) {
               results.add(teamDef);
            }
         }
      }
      return results;
   }

   @Override
   public Set<IAtsTeamDefinition> getChildren(IAtsTeamDefinition teamDef, boolean recurse) {
      Set<IAtsTeamDefinition> children = new HashSet<>();
      Collection<TeamDefinition> cTeamDefs = getChildrenTeamDefinitions(teamDef);
      for (IAtsTeamDefinition child : cTeamDefs) {
         children.add(child);
         if (recurse) {
            Set<IAtsTeamDefinition> children2 = getChildren(child, recurse);
            children.addAll(children2);
         }
      }
      return children;
   }

   @Override
   public List<IAtsTeamDefinition> getTeamDefinitions(Active active) {
      List<IAtsTeamDefinition> teamDefs = new ArrayList<>();
      for (IAtsTeamDefinition teamDef : atsApi.getConfigService().getConfigurations().getIdToTeamDef().values()) {
         if (teamDef.isActive()) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   @Override
   public Collection<TeamDefinition> getTeamTopLevelDefinitions(Active active) {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinitionOrSentinel();
      if (topTeamDef.isInvalid()) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   @Override
   public IAtsTeamDefinition getTopTeamDefinition() {
      return atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(
         atsApi.getConfigService().getConfigurations().getTopTeamDefinition().getId());
   }

   @Override
   public IAtsTeamDefinition getTopTeamDefinitionOrSentinel() {
      IAtsTeamDefinition teamDef = getTopTeamDefinition();
      if (teamDef == null) {
         return IAtsTeamDefinition.SENTINEL;
      }
      return teamDef;
   }

   @Override
   public Set<IAtsTeamDefinition> getTeamReleaseableDefinitions(Active active) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(active)) {
         if (AtsApiService.get().getVersionService().getVersions(teamDef).size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   @Override
   public Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem ai) {
      Set<IAtsTeamDefinition> aiTeams = new HashSet<>();
      getTeamFromItemAndChildren(ai, aiTeams);
      return aiTeams;
   }

   @Override
   public Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsTeamDefinition teamDef) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      teamDefs.add(teamDef);
      for (IAtsTeamDefinition child : AtsApiService.get().getTeamDefinitionService().getChildrenTeamDefinitions(
         teamDef)) {
         teamDefs.addAll(getTeamsFromItemAndChildren(child));
      }
      return teamDefs;
   }

   @Override
   public void getTeamFromItemAndChildren(IAtsActionableItem ai, Set<IAtsTeamDefinition> aiTeams) {
      aiTeams.add(ai.getTeamDefinition());

      for (IAtsActionableItem childArt : ai.getChildrenActionableItems()) {
         getTeamFromItemAndChildren(childArt, aiTeams);
      }
   }

   @Override
   public Set<IAtsTeamDefinition> getTeamDefinitions(Collection<String> teamDefNames) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(Active.Both)) {
         if (teamDefNames.contains(teamDef.getName())) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   @Override
   public Set<IAtsTeamDefinition> getTeamDefinitionsNameStartsWith(String prefix) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(Active.Both)) {
         if (teamDef.getName().startsWith(prefix)) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   @Override
   public Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais) {
      Set<IAtsTeamDefinition> resultTeams = new HashSet<>();
      for (IAtsActionableItem ai : ais) {
         resultTeams.addAll(getImpactedTeamDefInherited(ai));
      }
      return resultTeams;
   }

   @Override
   public IAtsTeamDefinition getImpactedTeamDef(IAtsActionableItem ai) {
      if (ai.getTeamDefinition() != null) {
         return ai.getTeamDefinition();
      }
      if (ai.getParentActionableItem() != null) {
         return getImpactedTeamDef(ai.getParentActionableItem());
      }
      return null;
   }

   @Override
   public Collection<IAtsTeamDefinition> getImpactedTeamDefInherited(IAtsActionableItem ai) {
      if (ai == null) {
         return java.util.Collections.emptyList();
      }
      if (ai.getTeamDefinition() != null) {
         return java.util.Collections.singleton(ai.getTeamDefinition());
      }
      IAtsActionableItem parentArt = ai.getParentActionableItem();
      return getImpactedTeamDefInherited(parentArt);
   }

   @Override
   public Collection<TeamDefinition> getTeamTopLevelJaxDefinitions(Active active) {
      List<TeamDefinition> teamDefs = new LinkedList<>();
      TeamDefinition topTeam = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(
         atsApi.getConfigService().getConfigurations().getTopTeamDefinition().getId());
      for (Long id : topTeam.getChildren()) {
         teamDefs.add(atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(id));
      }
      return teamDefs;
   }

   @Override
   public Collection<IAtsTeamDefinition> getTeamDefs(Collection<TeamDefinition> jTeamDefs) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (TeamDefinition jTeamDef : jTeamDefs) {
         teamDefs.add(atsApi.getQueryService().getConfigItem(jTeamDef.getId()));
      }
      return teamDefs;

   }

}
