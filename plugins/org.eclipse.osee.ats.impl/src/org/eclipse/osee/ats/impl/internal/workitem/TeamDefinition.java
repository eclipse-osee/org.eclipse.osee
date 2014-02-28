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
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.TeamDefinitionOptions;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G Dunne
 */
public class TeamDefinition extends AtsConfigObject implements IAtsTeamDefinition {

   public TeamDefinition(Log logger, IAtsServer atsServer, ArtifactReadable artifact) {
      super(logger, atsServer, artifact);
   }

   @Override
   public String getTypeName() {
      return "Team Definition";
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems() {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      try {
         for (Object aiGuidObj : artifact.getAttributeValues(AtsAttributeTypes.ActionableItem)) {
            String aiGuid = (String) aiGuidObj;
            ArtifactReadable aiArt = getAtsServer().getArtifactByGuid(aiGuid);
            IAtsActionableItem ai = getAtsServer().getConfigItemFactory().getActionableItem(aiArt);
            ais.add(ai);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getActionableItems");
      }
      return ais;
   }

   @Override
   public void setParentTeamDef(IAtsTeamDefinition parentTeamDef) {
      getLogger().error("TeamDefinition.setParentTeamDef not implemented");
   }

   @Override
   public IAtsTeamDefinition getParentTeamDef() {
      IAtsTeamDefinition parent = null;
      try {
         ResultSet<ArtifactReadable> related = artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Parent);
         if (!related.isEmpty()) {
            parent = getAtsServer().getConfigItemFactory().getTeamDef(related.iterator().next());
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getParentTeamDef");
      }
      return parent;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions() {
      Set<IAtsTeamDefinition> children = new HashSet<IAtsTeamDefinition>();
      try {
         for (ArtifactReadable childArt : artifact.getRelated(CoreRelationTypes.Default_Hierarchical__Child)) {
            IAtsTeamDefinition childTeamDef = getAtsServer().getConfigItemFactory().getTeamDef(childArt);
            children.add(childTeamDef);
         }
      } catch (OseeCoreException ex) {
         getLogger().error(ex, "Error getChildrenTeamDefinitions");
      }
      return children;
   }

   @Override
   public Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) {
      return null;
   }

   @Override
   public Collection<IAtsUser> getMembers() {
      return getRelatedUsers(AtsRelationTypes.TeamMember_Member);
   }

   @Override
   public Collection<IAtsUser> getMembersAndLeads() {
      Set<IAtsUser> results = new HashSet<IAtsUser>();
      results.addAll(getLeads());
      results.addAll(getMembers());
      return results;
   }

   @Override
   public Collection<IAtsUser> getPrivilegedMembers() {
      return getRelatedUsers(AtsRelationTypes.PrivilegedMember_Member);
   }

   @Override
   public void setAllowCommitBranch(boolean allowCommitBranch) {
      getLogger().error("TeamDefinition.setAllowCommitBranch not implemented");
   }

   @Override
   public boolean isAllowCommitBranch() {
      boolean set = false;
      try {
         set = artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false);
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
      if (!Strings.isValid(getBaselineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public void setAllowCreateBranch(boolean allowCreateBranch) {
      getLogger().error("TeamDefinition.setAllowCreateBranch not implemented");
   }

   @Override
   public boolean isAllowCreateBranch() {
      boolean set = false;
      try {
         set = artifact.getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false);
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
      if (!Strings.isValid(getBaselineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public void setBaselineBranchGuid(String parentBranchGuid) {
      getLogger().error("TeamDefinition.setBaselineBranchGuid not implemented");
   }

   @Override
   public String getBaselineBranchGuid() {
      return getAttributeValue(AtsAttributeTypes.BaselineBranchGuid, false);
   }

   @Override
   public String getTeamBranchGuid() {
      String guid = getBaselineBranchGuid();
      if (GUID.isValid(guid)) {
         return guid;
      } else {
         IAtsTeamDefinition parentTeamDef = getParentTeamDef();
         if (parentTeamDef instanceof TeamDefinition) {
            return parentTeamDef.getTeamBranchGuid();
         }
      }
      return null;
   }

   @Override
   public String getCommitFullDisplayName() {
      return getName();
   }

   @Override
   public boolean isTeamUsesVersions() throws OseeCoreException {
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
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions() throws OseeCoreException {
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
   public Collection<IAtsVersion> getVersions() {
      Set<IAtsVersion> results = new HashSet<IAtsVersion>();
      try {
         for (ArtifactReadable verArt : artifact.getRelated(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
            IAtsVersion version = (IAtsVersion) getAtsServer().getAtsConfig().getSoleByGuid(verArt.getGuid());
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
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException {
      IAtsTeamDefinition teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) {
         return new ArrayList<IAtsVersion>();
      }
      return teamDef.getVersions(releaseType, lockedType);
   }

   @Override
   public Collection<IAtsVersion> getVersionsLocked(VersionLockedType lockType) {
      ArrayList<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (IAtsVersion version : getVersions()) {
         if (version.isVersionLocked() && (lockType == VersionLockedType.Locked || lockType == VersionLockedType.Both)) {
            versions.add(version);
         } else if ((!version.isVersionLocked() && lockType == VersionLockedType.UnLocked) || lockType == VersionLockedType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersionsReleased(VersionReleaseType releaseType) {
      ArrayList<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      for (IAtsVersion version : getVersions()) {
         if (version.isReleased() && (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both)) {
            versions.add(version);
         } else if ((!version.isReleased() && releaseType == VersionReleaseType.UnReleased) || releaseType == VersionReleaseType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   @Override
   public void setWorkflowDefinition(String workflowDefinitionName) {
      getLogger().error("TeamDefinition.setWorkflowDefinition not implemented");
   }

   @Override
   public String getWorkflowDefinition() {
      return getAttributeValue(AtsAttributeTypes.WorkflowDefinition, "");
   }

   @Override
   public String getRelatedTaskWorkDefinition() {
      return getAttributeValue(AtsAttributeTypes.RelatedTaskWorkDefinition, "");
   }

   @Override
   public void setRelatedTaskWorkDefinition(String name) {
      getLogger().error("TeamDefinition.setRelatedTaskWorkDefinition not implemented");
   }

   @Override
   public void initialize(String fullname, String description, Collection<IAtsUser> leads, Collection<IAtsUser> members, Collection<IAtsActionableItem> actionableItems, TeamDefinitionOptions... teamDefinitionOptions) {
      getLogger().error("TeamDefinition.setActionable not implemented");
   }

   @Override
   public String getRelatedPeerWorkDefinition() {
      return getAttributeValue(AtsAttributeTypes.RelatedPeerWorkflowDefinition, "");
   }

   @Override
   public void setRelatedPeerWorkDefinition(String relatedPeerWorkDefinition) {
      getLogger().error("TeamDefinition.setRelatedPeerWorkDefinition not implemented");
   }

   @Override
   public void addRule(String rule) {
      getLogger().error("TeamDefinition.addRule not implemented");
   }

   @Override
   public Collection<String> getRules() {
      Collection<String> rules = new ArrayList<String>();
      try {
         rules = artifact.getAttributeValues(AtsAttributeTypes.RuleDefinition);
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

   @Override
   public void removeRule(String rule) {
      getLogger().error("TeamDefinition.setActionable not implemented");
   }

}
