/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamDefinition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class TeamDefinition extends AtsConfigObject implements IAtsTeamDefinition {

   private JaxTeamDefinition jaxTeamDef;
   private IAtsTeamDefinition parentTeamDef;

   public TeamDefinition(Log logger, AtsApi atsApi, JaxTeamDefinition jaxTeamDef) {
      super(logger, atsApi, ArtifactToken.valueOf(jaxTeamDef.getId(), jaxTeamDef.getGuid(), jaxTeamDef.getName(),
         atsApi.getAtsBranch(), TeamDefinition), TeamDefinition);
      this.jaxTeamDef = jaxTeamDef;
   }

   public TeamDefinition(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, TeamDefinition);
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems() {
      List<IAtsActionableItem> children = new LinkedList<>();
      if (jaxTeamDef != null) {
         for (Long aiId : jaxTeamDef.getAis()) {
            children.add(
               new ActionableItem(logger, atsApi, atsApi.getConfigService().getConfigurations().getIdToAi().get(aiId)));
         }
      } else {
         for (ArtifactToken artifact : atsApi.getRelationResolver().getRelated(artifact,
            AtsRelationTypes.TeamActionableItem_ActionableItem)) {
            if (atsApi.getStoreService().isOfType(artifact, AtsArtifactTypes.ActionableItem)) {
               children.add(new ActionableItem(logger, atsApi, artifact));
            }
         }
      }
      return children;
   }

   @Override
   public IAtsTeamDefinition getParentTeamDef() {
      if (parentTeamDef == null) {
         try {
            ArtifactToken parentArt = atsApi.getRelationResolver().getParent(artifact);
            if (parentArt != null && parentArt.notEqual(AtsArtifactToken.HeadingFolder)) {
               parentTeamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(parentArt);
            }
         } catch (OseeCoreException ex) {
            getLogger().error(ex, "Error getParentTeamDef");
         }
      }
      return parentTeamDef;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions() {
      Set<IAtsTeamDefinition> children = new HashSet<>();
      try {
         for (ArtifactId childArt : atsApi.getRelationResolver().getRelated(artifact,
            CoreRelationTypes.Default_Hierarchical__Child)) {
            IAtsTeamDefinition childTeamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(childArt);
            if (childTeamDef != null) {
               children.add(childTeamDef);
            }
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getChildrenTeamDefinitions");
      }
      return children;
   }

   @Override
   public Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) {
      Set<IAtsUser> leads = new HashSet<>();
      for (IAtsActionableItem aia : actionableItems) {
         if (this.equals(aia.getTeamDefinitionInherited())) {
            // If leads are specified for this aia, add them
            Collection<IAtsUser> leads2 = aia.getLeads();
            if (leads2.size() > 0) {
               leads.addAll(leads2);
            } else {
               if (aia.getTeamDefinitionInherited() != null) {
                  Collection<IAtsUser> leads3 = aia.getTeamDefinitionInherited().getLeads();
                  leads.addAll(leads3);
               }
            }
         }
      }
      if (leads.isEmpty()) {
         Collection<IAtsUser> leads2 = getLeads();
         leads.addAll(leads2);
      }
      return leads;
   }

   @Override
   public Collection<IAtsUser> getMembers() {
      return getRelatedUsers(AtsRelationTypes.TeamMember_Member);
   }

   @Override
   public Collection<IAtsUser> getMembersAndLeads() {
      Set<IAtsUser> results = new HashSet<>();
      results.addAll(getLeads());
      results.addAll(getMembers());
      return results;
   }

   @Override
   public Collection<IAtsUser> getPrivilegedMembers() {
      return getRelatedUsers(AtsRelationTypes.PrivilegedMember_Member);
   }

   @Override
   public boolean isAllowCommitBranch() {
      boolean set = false;
      try {
         set =
            atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowCommitBranch, false);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error is allow commit branch");
      }
      return set;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!isAllowCommitBranch()) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (getBaselineBranchId().isInvalid()) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public boolean isAllowCreateBranch() {
      boolean set = false;
      try {
         set =
            atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowCreateBranch, false);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error get allow create branch");
      }
      return set;
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!isAllowCreateBranch()) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (getBaselineBranchId().isInvalid()) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public BranchId getBaselineBranchId() {
      return BranchId.valueOf((String) getAttributeValue(AtsAttributeTypes.BaselineBranchId, "0"));
   }

   @Override
   public BranchId getTeamBranchId() {
      BranchId branch = getBaselineBranchId();
      if (branch.isValid()) {
         return branch;
      } else {
         IAtsTeamDefinition parentTeamDef = getParentTeamDef();
         if (parentTeamDef instanceof TeamDefinition) {
            return parentTeamDef.getTeamBranchId();
         }
      }
      return BranchId.SENTINEL;
   }

   @Override
   public String getCommitFullDisplayName() {
      return getName();
   }

   @Override
   public boolean isTeamUsesVersions() {
      return getTeamDefinitionHoldingVersions() != null;
   }

   @Override
   public IAtsVersion getNextReleaseVersion() {
      IAtsVersion result = null;
      for (IAtsVersion version : getVersions()) {
         if (version.isNextVersion()) {
            result = version;
            break;
         }
      }
      return result;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions() {
      IAtsTeamDefinition teamDef = null;
      if (getVersions().size() > 0) {
         teamDef = this;
      } else {
         IAtsTeamDefinition parentTda = getParentTeamDef();
         if (parentTda != null) {
            teamDef = parentTda.getTeamDefinitionHoldingVersions();
         }
      }
      return teamDef;
   }

   @Override
   public IAtsVersion getVersion(String name) {
      IAtsVersion result = null;
      for (IAtsVersion version : getVersions()) {
         if (version.getName().equals(name)) {
            result = version;
            break;
         }
      }
      return result;
   }

   @Override
   public Set<IAtsVersion> getVersions() {
      Set<IAtsVersion> results = new HashSet<>();
      try {
         for (ArtifactId verArt : atsApi.getRelationResolver().getRelated(artifact,
            AtsRelationTypes.TeamDefinitionToVersion_Version)) {
            IAtsVersion version = atsApi.getVersionService().getVersion(verArt);
            results.add(version);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting versions");
      }
      return results;
   }

   @Override
   public Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockedType) {
      return org.eclipse.osee.framework.jdk.core.util.Collections.setIntersection(getVersionsReleased(releaseType),
         getVersionsLocked(lockedType));
   }

   @Override
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) {
      IAtsTeamDefinition teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) {
         return new ArrayList<IAtsVersion>();
      }
      return teamDef.getVersions(releaseType, lockedType);
   }

   @Override
   public Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType) {
      ArrayList<IAtsVersion> versions = new ArrayList<>();
      for (IAtsVersion version : getVersions()) {
         if (version.isVersionLocked() && (lockType == VersionLockedType.Locked || lockType == VersionLockedType.Both)) {
            versions.add(version);
         } else if (!version.isVersionLocked() && lockType == VersionLockedType.UnLocked || lockType == VersionLockedType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType) {
      ArrayList<IAtsVersion> versions = new ArrayList<>();
      for (IAtsVersion version : getVersions()) {
         if (version.isReleased() && (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both)) {
            versions.add(version);
         } else if (!version.isReleased() && releaseType == VersionReleaseType.UnReleased || releaseType == VersionReleaseType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<String> getRules() {
      Collection<String> rules = new ArrayList<>();
      try {
         rules = atsApi.getAttributeResolver().getAttributesToStringList(this, AtsAttributeTypes.RuleDefinition);
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getting rules");
      }
      return rules;
   }

   @Override
   public boolean hasRule(String rule) {
      boolean result = false;
      for (String rule2 : getRules()) {
         if (rule.equals(rule2)) {
            result = true;
            break;
         }
      }
      return result;
   }

}
