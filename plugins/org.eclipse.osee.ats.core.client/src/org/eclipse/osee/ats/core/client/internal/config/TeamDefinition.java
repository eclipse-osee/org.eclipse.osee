/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client.internal.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.config.RuleManager;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinition extends AtsObject implements IAtsTeamDefinition {
   private boolean active = true;
   private boolean actionable = false;
   private boolean allowCreateBranch = false;
   private boolean allowCommitBranch = false;
   private long baselineBranchUuid = 0;
   private String description = null;
   private String fullName = null;
   private String workflowDefinitionName;
   private String relatedTaskWorkDefinition;
   private IAtsTeamDefinition parentTeamDef;

   private Set<String> staticIds = null;
   private final RuleManager ruleMgr = new RuleManager();

   private Set<IAtsUser> leads = null;
   private Set<IAtsUser> members = null;
   private Set<IAtsUser> priviledgedMembers = null;
   private Set<IAtsUser> subscribed = null;

   private Set<IAtsTeamDefinition> childrenTeamDefinitions = null;
   private Set<IAtsActionableItem> actionableItems = null;
   private Set<IAtsVersion> versions = null;
   private String relatedPeerWorkflowDefinition;

   @Override
   public String getRelatedTaskWorkDefinition() {
      return relatedTaskWorkDefinition;
   }

   public TeamDefinition(String name, String guid, long uuid) {
      super(name, uuid);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!allowCreateBranch) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (!AtsClientService.get().getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!allowCommitBranch) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (!AtsClientService.get().getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with versions. This allows
    * multiple TeamDefinitions to be versioned/released together by having the parent hold the versions. It is not
    * required that a product configured in ATS uses the versions option. If no parent with versions is found, null is
    * returned. If boolean "Team Uses Versions" is false, just return cause this team doesn't use versions
    *
    * @return parent TeamDefinition that holds the version definitions
    */

   @Override
   public IAtsTeamDefinition getTeamDefinitionHoldingVersions() throws OseeCoreException {
      if (getVersions().size() > 0) {
         return this;
      }
      IAtsTeamDefinition parentTda = getParentTeamDef();
      if (parentTda != null) {
         return parentTda.getTeamDefinitionHoldingVersions();
      }
      return null;
   }

   @Override
   public IAtsVersion getNextReleaseVersion() {
      for (IAtsVersion verArt : getVersions()) {
         if (verArt.isNextVersion()) {
            return verArt;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsVersion> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException {
      IAtsTeamDefinition teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) {
         return new ArrayList<IAtsVersion>();
      }
      return teamDef.getVersions(releaseType, lockedType);
   }

   /**
    * Return ONLY leads configured for this Depending on the use, like creating new actions, the assignees (or Leads)
    * are determined first from users configured as leads of individual actionable items and only if that returns no
    * leads, THEN default to using the leads configured for the TeamDefinition. In these cases, use getLeads(Collection
    * <IAtsActionableItem>) instead.
    *
    * @return users configured as leads for this IAtsTeamDefinition
    */

   @Override
   public Collection<IAtsUser> getLeads() {
      if (leads == null) {
         leads = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               leads.add(user);
            }
         }
      }
      return leads;
   }

   private Artifact getArtifact() {
      return (Artifact) getStoreObject();
   }

   @Override
   public Collection<IAtsUser> getPrivilegedMembers() {
      if (priviledgedMembers == null) {
         priviledgedMembers = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               priviledgedMembers.add(user);
            }
         }
      }
      return priviledgedMembers;
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this
    *
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    */

   @Override
   public Collection<IAtsUser> getLeads(Collection<IAtsActionableItem> actionableItems) throws OseeCoreException {
      Set<IAtsUser> leads = new HashSet<>();
      for (IAtsActionableItem aia : actionableItems) {
         if (this.equals(aia.getTeamDefinitionInherited())) {
            // If leads are specified for this aia, add them
            if (aia.getLeads().size() > 0) {
               leads.addAll(aia.getLeads());
            } else {
               if (aia.getTeamDefinitionInherited() != null) {
                  leads.addAll(aia.getTeamDefinitionInherited().getLeads());
               }
            }
         }
      }
      if (leads.isEmpty()) {
         leads.addAll(getLeads());
      }
      return leads;
   }

   @Override
   @SuppressWarnings("unchecked")
   public Collection<IAtsUser> getMembersAndLeads() {
      return Collections.setUnion(getMembers(), getLeads());
   }

   @Override
   public Collection<IAtsUser> getMembers() {
      if (members == null) {
         members = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.TeamMember_Member)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               members.add(user);
            }
         }
      }
      return members;
   }

   @Override
   public IAtsVersion getVersion(String name) {
      for (IAtsVersion verArt : getVersions()) {
         if (verArt.getName().equals(name)) {
            return verArt;
         }
      }
      return null;
   }

   @Override
   public Collection<IAtsVersion> getVersions() {
      if (versions == null) {
         versions = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact verArt : getArtifact().getRelatedArtifacts(
               AtsRelationTypes.TeamDefinitionToVersion_Version)) {
               IAtsVersion version = AtsClientService.get().getCache().getAtsObject(verArt.getUuid());
               versions.add(version);
            }
         }
      }
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockType) {
      return Collections.setIntersection(getVersionsReleased(releaseType), getVersionsLocked(lockType));
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
   public boolean isTeamUsesVersions() throws OseeCoreException {
      return getTeamDefinitionHoldingVersions() != null;
   }

   @Override
   public boolean isActionable() {
      return actionable;
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/>
    * <br/>
    * If no branch is associated then null will be returned.
    */
   @Override
   public long getTeamBranchUuid() {
      long uuid = getBaselineBranchUuid();
      if (uuid > 0) {
         return uuid;
      } else {
         IAtsTeamDefinition parentTeamDef = getParentTeamDef();
         if (parentTeamDef instanceof TeamDefinition) {
            return parentTeamDef.getTeamBranchUuid();
         }
      }
      return 0;
   }

   @Override
   public String getCommitFullDisplayName() {
      return getName();
   }

   @Override
   public boolean isAllowCreateBranch() {
      return allowCreateBranch;
   }

   @Override
   public void setAllowCreateBranch(boolean allowCreateBranch) {
      this.allowCreateBranch = allowCreateBranch;
   }

   @Override
   public boolean isAllowCommitBranch() {
      return allowCommitBranch;
   }

   @Override
   public void setAllowCommitBranch(boolean allowCommitBranch) {
      this.allowCommitBranch = allowCommitBranch;
   }

   @Override
   public String getDescription() {
      return description;
   }

   @Override
   public void setDescription(String description) {
      this.description = description;
   }

   @Override
   public String getFullName() {
      return fullName;
   }

   @Override
   public void setFullName(String fullName) {
      this.fullName = fullName;
   }

   @Override
   public IAtsTeamDefinition getParentTeamDef() {
      if (parentTeamDef == null && getArtifact() != null) {
         Artifact parentTeamDefArt = getArtifact().getParent();
         if (parentTeamDefArt != null && parentTeamDefArt.isOfType(AtsArtifactTypes.TeamDefinition)) {
            IAtsTeamDefinition parent = AtsClientService.get().getCache().getAtsObject(parentTeamDefArt.getUuid());
            this.parentTeamDef = parent;
         }
      }
      return parentTeamDef;
   }

   @Override
   public void setActionable(boolean actionable) {
      this.actionable = actionable;
   }

   @Override
   public boolean isActive() {
      return active;
   }

   @Override
   public void setActive(boolean active) {
      this.active = active;
   }

   @Override
   public Collection<IAtsTeamDefinition> getChildrenTeamDefinitions() {
      if (childrenTeamDefinitions == null) {
         childrenTeamDefinitions = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact child : getArtifact().getChildren()) {
               if (child.isOfType(AtsArtifactTypes.TeamDefinition)) {
                  IAtsTeamDefinition childTeamDef = AtsClientService.get().getCache().getAtsObject(child.getUuid());
                  childrenTeamDefinitions.add(childTeamDef);
               }
            }
         }
      }
      return childrenTeamDefinitions;
   }

   @Override
   public void setWorkflowDefinition(String workDefinitionName) {
      workflowDefinitionName = workDefinitionName;
   }

   @Override
   public String getWorkflowDefinition() {
      return workflowDefinitionName;
   }

   @Override
   public Collection<String> getStaticIds() {
      if (staticIds == null) {
         staticIds = new HashSet<>();
         if (getArtifact() != null) {
            for (String staticId : getArtifact().getAttributesToStringList(CoreAttributeTypes.StaticId)) {
               staticIds.add(staticId);
            }
         }
      }
      return staticIds;
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems() {
      if (actionableItems == null) {
         actionableItems = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact aiArt : getArtifact().getRelatedArtifacts(
               AtsRelationTypes.TeamActionableItem_ActionableItem)) {
               IAtsActionableItem ai = AtsClientService.get().getCache().getAtsObject(aiArt.getUuid());
               actionableItems.add(ai);
            }
         }
      }
      return actionableItems;
   }

   @Override
   public void setRelatedTaskWorkDefinition(String name) {
      relatedTaskWorkDefinition = name;
   }

   @Override
   public void setParentTeamDef(IAtsTeamDefinition parentTeamDef) {
      if (parentTeamDef.getUuid().equals(getUuid())) {
         throw new IllegalArgumentException("Can't set parent to self");
      }
      this.parentTeamDef = parentTeamDef;
   }

   @Override
   public Collection<IAtsUser> getSubscribed() {
      if (subscribed == null) {
         subscribed = new HashSet<>();
         if (getArtifact() != null) {
            for (Artifact userArt : getArtifact().getRelatedArtifacts(AtsRelationTypes.SubscribedUser_User)) {
               IAtsUser user = AtsClientService.get().getUserServiceClient().getUserFromOseeUser((User) userArt);
               subscribed.add(user);
            }
         }
      }
      return subscribed;
   }

   /**
    * Rules
    */
   @Override
   public void removeRule(String rule) {
      ruleMgr.removeRule(rule);
   }

   @Override
   public List<String> getRules() {
      return ruleMgr.getRules();
   }

   @Override
   public void addRule(String rule) {
      ruleMgr.addRule(rule);
   }

   @Override
   public boolean hasRule(String rule) {
      return ruleMgr.hasRule(rule);
   }

   @Override
   public String getTypeName() {
      return "Team Definition";
   }

   @Override
   public String getRelatedPeerWorkDefinition() {
      return relatedPeerWorkflowDefinition;
   }

   @Override
   public void setRelatedPeerWorkDefinition(String relatedPeerWorkflowDefinition) {
      this.relatedPeerWorkflowDefinition = relatedPeerWorkflowDefinition;
   }

   @Override
   public long getBaselineBranchUuid() {
      return baselineBranchUuid;
   }

   @Override
   public void setBaselineBranchUuid(long uuid) {
      this.baselineBranchUuid = uuid;
   }

   @Override
   public void setBaselineBranchUuid(String uuid) {
      if (Strings.isValid(uuid)) {
         this.baselineBranchUuid = Long.valueOf(uuid);
      }
   }
}
