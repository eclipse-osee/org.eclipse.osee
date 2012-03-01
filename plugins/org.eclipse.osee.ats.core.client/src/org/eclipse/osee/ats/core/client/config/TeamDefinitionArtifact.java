/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.ats.core.client.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.util.AtsUsers;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.version.VersionArtifact;
import org.eclipse.osee.ats.core.client.version.VersionLockedType;
import org.eclipse.osee.ats.core.client.version.VersionReleaseType;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.workdef.RuleDefinition;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.RuleManager;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifact extends Artifact implements ICommitConfigArtifact {

   public TeamDefinitionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   @Override
   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (!getSoleAttributeValue(AtsAttributeTypes.AllowCreateBranch, false)) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Result isCommitBranchAllowed() throws OseeCoreException {
      if (!getSoleAttributeValue(AtsAttributeTypes.AllowCommitBranch, false)) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   public void initialize(String fullname, String description, Collection<User> leads, Collection<User> members, Collection<ActionableItemArtifact> actionableItems, TeamDefinitionOptions... teamDefinitionOptions) throws OseeCoreException {
      List<Object> teamDefOptions = Collections.getAggregate((Object[]) teamDefinitionOptions);

      setSoleAttributeValue(AtsAttributeTypes.Description, description);
      setSoleAttributeValue(AtsAttributeTypes.FullName, fullname);
      for (User user : leads) {
         addRelation(AtsRelationTypes.TeamLead_Lead, user);
         // All leads are members
         addRelation(AtsRelationTypes.TeamMember_Member, user);
      }
      for (User user : members) {
         addRelation(AtsRelationTypes.TeamMember_Member, user);
      }

      if (teamDefOptions.contains(TeamDefinitionOptions.TeamUsesVersions)) {
         setSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, true);
      }
      if (teamDefOptions.contains(TeamDefinitionOptions.RequireTargetedVersion)) {
         addRule(RuleDefinitionOption.RequireTargetedVersion);
      }

      // Relate to actionable items
      for (ActionableItemArtifact aia : actionableItems) {
         addRelation(AtsRelationTypes.TeamActionableItem_ActionableItem, aia);
      }
   }

   @Override
   public Branch getParentBranch() throws OseeCoreException {
      try {
         String guid = getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, "");
         if (GUID.isValid(guid)) {
            return BranchManager.getBranchByGuid(guid);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with versions. This allows
    * multiple TeamDefinitions to be versioned/released together by having the parent hold the versions. It is not
    * required that a product configured in ATS uses the versions option. If no parent with versions is found, null is
    * returned. If boolean "Team Uses Versions" is false, just return cause this team doesn't use versions
    *
    * @return parent TeamDefinition that holds the version definitions
    */
   public TeamDefinitionArtifact getTeamDefinitionHoldingVersions() throws OseeCoreException {
      if (!isTeamUsesVersions()) {
         return null;
      }
      if (getVersionsArtifacts().size() > 0) {
         return this;
      }
      if (getParent() instanceof TeamDefinitionArtifact) {
         TeamDefinitionArtifact parentTda = (TeamDefinitionArtifact) getParent();
         if (parentTda != null) {
            return parentTda.getTeamDefinitionHoldingVersions();
         }
      }
      return null;
   }

   public Artifact getNextReleaseVersion() throws OseeCoreException {
      for (Artifact verArt : getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         if (verArt.getSoleAttributeValue(AtsAttributeTypes.NextVersion, false)) {
            return verArt;
         }
      }
      return null;
   }

   public Collection<VersionArtifact> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType, VersionLockedType lockedType) throws OseeCoreException {
      TeamDefinitionArtifact teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) {
         return new ArrayList<VersionArtifact>();
      }
      return teamDef.getVersionsArtifacts(releaseType, lockedType);
   }

   public double getManDayHrsFromItemAndChildren() {
      return getHoursPerWorkDayFromItemAndChildren(this);
   }

   /**
    * If hours per work day attribute is set, use it, otherwise, walk up the Team Definition tree. Value used in
    * calculations.
    */
   public double getHoursPerWorkDayFromItemAndChildren(TeamDefinitionArtifact teamDef) {
      try {
         Double manDaysHrs = teamDef.getSoleAttributeValue(AtsAttributeTypes.HoursPerWorkDay, 0.0);
         if (manDaysHrs != null && manDaysHrs != 0) {
            return manDaysHrs;
         }
         if (teamDef.getParent() instanceof TeamDefinitionArtifact) {
            return teamDef.getHoursPerWorkDayFromItemAndChildren((TeamDefinitionArtifact) teamDef.getParent());
         }
         return AtsUtilCore.DEFAULT_HOURS_PER_WORK_DAY;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return 0.0;
   }

   /**
    * Return ONLY leads configured for this TeamDefinitionArtifact. Depending on the use, like creating new actions, the
    * assignees (or Leads) are determined first from users configured as leads of individual actionable items and only
    * if that returns no leads, THEN default to using the leads configured for the TeamDefinition. In these cases, use
    * getLeads(Collection<ActionableItemArtifact>) instead.
    *
    * @return users configured as leads for this TeamDefinitionArtifact
    */
   public Collection<IAtsUser> getLeads() throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (User user : getRelatedArtifacts(AtsRelationTypes.TeamLead_Lead, User.class)) {
         users.add(AtsUsers.getUser(user.getUserId()));
      }
      return users;
   }

   public Collection<IAtsUser> getPrivilegedMembers() throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (User user : getRelatedArtifacts(AtsRelationTypes.PrivilegedMember_Member, User.class)) {
         users.add(AtsUsers.getUser(user.getUserId()));
      }
      return users;
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this TeamDefinitionArtifact.
    *
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    */
   public Collection<IAtsUser> getLeads(Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      Set<IAtsUser> leads = new HashSet<IAtsUser>();
      for (ActionableItemArtifact aia : actionableItems) {
         if (aia.getImpactedTeamDefs().contains(this)) {
            // If leads are specified for this aia, add them
            if (aia.getLeads().size() > 0) {
               leads.addAll(aia.getLeads());
            } else {
               for (TeamDefinitionArtifact teamDef : aia.getImpactedTeamDefs()) {
                  leads.addAll(teamDef.getLeads());
               }
            }
         }
      }
      if (leads.isEmpty()) {
         leads.addAll(getLeads());
      }
      return leads;
   }

   @SuppressWarnings("unchecked")
   public Collection<IAtsUser> getMembersAndLeads() throws OseeCoreException {
      return Collections.setUnion(getMembers(), getLeads());
   }

   public Collection<IAtsUser> getMembers() throws OseeCoreException {
      List<IAtsUser> users = new LinkedList<IAtsUser>();
      for (User user : getRelatedArtifacts(AtsRelationTypes.TeamMember_Member, User.class)) {
         users.add(AtsUsers.getUser(user.getUserId()));
      }
      return users;
   }

   public Artifact getVersionArtifact(String name, boolean create) throws OseeCoreException {
      for (Artifact verArt : getVersionsArtifacts()) {
         if (verArt.getName().equals(name)) {
            return verArt;
         }
      }
      if (create) {
         return createVersion(name);
      }
      return null;
   }

   public VersionArtifact createVersion(String name) throws OseeCoreException {
      VersionArtifact versionArt =
         (VersionArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.Version, AtsUtilCore.getAtsBranch(), name);
      addRelation(AtsRelationTypes.TeamDefinitionToVersion_Version, versionArt);
      return versionArt;
   }

   public Collection<VersionArtifact> getVersionsArtifacts() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version, VersionArtifact.class);
   }

   public Collection<VersionArtifact> getVersionsArtifacts(VersionReleaseType releaseType, VersionLockedType lockType) throws OseeCoreException {
      return Collections.setIntersection(getVersionsArtifacts(releaseType), getVersionsArtifacts(lockType));
   }

   private Collection<VersionArtifact> getVersionsArtifacts(VersionReleaseType releaseType) throws OseeCoreException {
      ArrayList<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      for (VersionArtifact version : getVersionsArtifacts()) {
         if (version.isReleased() && (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both)) {
            versions.add(version);
         } else if ((!version.isReleased() && releaseType == VersionReleaseType.UnReleased) || releaseType == VersionReleaseType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   private Collection<VersionArtifact> getVersionsArtifacts(VersionLockedType lockType) throws OseeCoreException {
      ArrayList<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      for (VersionArtifact version : getVersionsArtifacts()) {
         if (version.isVersionLocked() && (lockType == VersionLockedType.Locked || lockType == VersionLockedType.Both)) {
            versions.add(version);
         } else if ((!version.isVersionLocked() && lockType == VersionLockedType.UnLocked) || lockType == VersionLockedType.Both) {
            versions.add(version);
         }
      }
      return versions;
   }

   public boolean isTeamUsesVersions() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.TeamUsesVersions, false);
   }

   public boolean isActionable() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.Actionable, false);
   }

   public List<RuleDefinition> getWorkRules() throws OseeCoreException {
      List<RuleDefinition> rules = new ArrayList<RuleDefinition>();
      for (String ruleId : getAttributesToStringList(AtsAttributeTypes.RuleDefinition)) {
         try {
            RuleDefinition ruleDef = RuleManager.getOrCreateRule(ruleId);
            rules.add(ruleDef);
         } catch (Exception ex) {
            OseeLog.logf(Activator.class, Level.SEVERE, ex, "Unrecognized rule definition [%s] for Team Def [%s]",
               ruleId, this.toStringWithId());
         }
      }
      return rules;
   }

   public void addRule(RuleDefinitionOption option) throws OseeCoreException {
      addRule(option.name());
   }

   public void addRule(String ruleId) throws OseeCoreException {
      if (!hasRule(ruleId)) {
         addAttribute(AtsAttributeTypes.RuleDefinition, ruleId);
      }
   }

   public boolean hasRule(RuleDefinitionOption option) throws OseeCoreException {
      return hasRule(option.name());
   }

   public boolean hasRule(String ruleId) throws OseeCoreException {
      return getAttributesToStringList(AtsAttributeTypes.RuleDefinition).contains(ruleId);
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/>
    * <br/>
    * If no branch is associated then null will be returned.
    */
   public Branch getTeamBranch() throws OseeCoreException {
      String guid = getSoleAttributeValue(AtsAttributeTypes.BaselineBranchGuid, null);
      if (GUID.isValid(guid)) {
         return BranchManager.getBranchByGuid(guid);
      } else {
         Artifact parent = getParent();
         if (parent instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) parent).getTeamBranch();
         }
      }
      return null;
   }

   @Override
   public String getFullDisplayName() {
      return getName();
   }

}
