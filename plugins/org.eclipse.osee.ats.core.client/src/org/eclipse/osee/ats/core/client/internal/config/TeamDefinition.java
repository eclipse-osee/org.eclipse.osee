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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.TeamDefinitionOptions;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.VersionLockedType;
import org.eclipse.osee.ats.api.version.VersionReleaseType;
import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.config.RuleManager;
import org.eclipse.osee.ats.core.model.impl.AtsObject;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinition extends AtsObject implements IAtsTeamDefinition {
   private boolean active = true;
   private boolean actionable = false;
   private boolean allowCreateBranch = false;
   private boolean allowCommitBranch = false;

   private String baselineBranchGuid = null;
   private String description = null;
   private String fullName = null;
   private String workflowDefinitionName;
   private String relatedTaskWorkDefinition;

   private IAtsTeamDefinition parentTeamDef;

   private final Set<String> staticIds = new HashSet<String>();
   private final RuleManager ruleMgr = new RuleManager();

   private final Set<IAtsUser> leads = new HashSet<IAtsUser>();
   private final Set<IAtsUser> members = new HashSet<IAtsUser>();
   private final Set<IAtsUser> priviledgedMembers = new HashSet<IAtsUser>();
   private final Set<IAtsUser> subscribed = new HashSet<IAtsUser>();

   private final Set<IAtsTeamDefinition> childrenTeamDefinitions = new HashSet<IAtsTeamDefinition>();
   private final Set<IAtsActionableItem> actionableItems = new HashSet<IAtsActionableItem>();
   private final Set<IAtsVersion> versions = new HashSet<IAtsVersion>();
   private String relatedPeerWorkflowDefinition;

   @Override
   public String getRelatedTaskWorkDefinition() {
      return relatedTaskWorkDefinition;
   }

   public TeamDefinition(String name, String guid) {
      super(name, guid);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!allowCreateBranch) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!allowCommitBranch) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (!Strings.isValid(getBaslineBranchGuid())) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public void initialize(String fullname, String description, Collection<IAtsUser> leads, Collection<IAtsUser> members, Collection<IAtsActionableItem> actionableItems, TeamDefinitionOptions... teamDefinitionOptions) {
      List<Object> teamDefOptions = Collections.getAggregate((Object[]) teamDefinitionOptions);

      setDescription(description);
      setFullName(fullname);
      leads.addAll(leads);
      members.addAll(members);

      if (teamDefOptions.contains(TeamDefinitionOptions.RequireTargetedVersion)) {
         addRule(RuleDefinitionOption.RequireTargetedVersion.name());
      }
      actionableItems.addAll(actionableItems);
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
    * leads, THEN default to using the leads configured for the TeamDefinition. In these cases, use
    * getLeads(Collection<IAtsActionableItem>) instead.
    * 
    * @return users configured as leads for this IAtsTeamDefinition
    */

   @Override
   public Collection<IAtsUser> getLeads() {
      return leads;
   }

   @Override
   public Collection<IAtsUser> getPrivilegedMembers() {
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
      Set<IAtsUser> leads = new HashSet<IAtsUser>();
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
      return versions;
   }

   @Override
   public Collection<IAtsVersion> getVersions(VersionReleaseType releaseType, VersionLockedType lockType) {
      return Collections.setIntersection(getVersionsReleased(releaseType), getVersionsLocked(lockType));
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
   public String getTeamBranchGuid() {
      String guid = getBaslineBranchGuid();
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
   public boolean isAllowCreateBranch() {
      return allowCreateBranch;
   }

   @Override
   public void setAllowCreateBranch(boolean allowCreateBranch) {
      this.allowCreateBranch = allowCreateBranch;
   }

   @Override
   public String getBaslineBranchGuid() {
      return baselineBranchGuid;
   }

   @Override
   public void setBaselineBranchGuid(String baselineBranchGuid) {
      this.baselineBranchGuid = baselineBranchGuid;
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
      return staticIds;
   }

   @Override
   public Collection<IAtsActionableItem> getActionableItems() {
      return actionableItems;
   }

   @Override
   public void setRelatedTaskWorkDefinition(String name) {
      relatedTaskWorkDefinition = name;
   }

   @Override
   public void setParentTeamDef(IAtsTeamDefinition parentTeamDef) {
      if (parentTeamDef.getGuid().equals(getGuid())) {
         throw new IllegalArgumentException("Can't set parent to self");
      }
      this.parentTeamDef = parentTeamDef;
   }

   @Override
   public Collection<IAtsUser> getSubscribed() {
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

}
