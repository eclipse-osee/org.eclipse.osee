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

package org.eclipse.osee.ats.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions.RuleWorkItemId;
import org.eclipse.osee.framework.db.connection.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemAttributes;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifact extends Artifact implements ICommitConfigArtifact {

   public static String ARTIFACT_NAME = "Team Definition";
   public static String TOP_TEAM_STATIC_ID = "osee.ats.TopTeamDefinition";
   public static Set<TeamDefinitionArtifact> EMPTY_SET = new HashSet<TeamDefinitionArtifact>();
   public static enum TeamDefinitionOptions {
      TeamUsesVersions, RequireTargetedVersion
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    */
   public TeamDefinitionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public Result isCreateBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(ATSAttributes.ALLOW_CREATE_BRANCH.getStoreName(), false) == false) {
         return new Result(false, "Branch creation disabled for Team Definition [" + this + "]");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   public Result isCommitBranchAllowed() throws OseeCoreException {
      if (getSoleAttributeValue(ATSAttributes.ALLOW_COMMIT_BRANCH.getStoreName(), false) == false) {
         return new Result(false, "Team Definition [" + this + "] not configured to allow branch commit.");
      }
      if (getParentBranch() == null) {
         return new Result(false, "Parent Branch not configured for Team Definition [" + this + "]");
      }
      return Result.TrueResult;
   }

   public void initialize(String fullname, String description, Collection<User> leads, Collection<User> members, Collection<ActionableItemArtifact> actionableItems, TeamDefinitionOptions... teamDefinitionOptions) throws OseeCoreException {
      List<Object> teamDefOptions = Collections.getAggregate((Object[]) teamDefinitionOptions);

      setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), description);
      setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), fullname);
      for (User user : leads) {
         addRelation(AtsRelation.TeamLead_Lead, user);
         // All leads are members
         addRelation(AtsRelation.TeamMember_Member, user);
      }
      for (User user : members) {
         addRelation(AtsRelation.TeamMember_Member, user);
      }

      if (teamDefOptions.contains(TeamDefinitionOptions.TeamUsesVersions)) {
         setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), true);
      }
      if (teamDefOptions.contains(TeamDefinitionOptions.RequireTargetedVersion)) {
         addWorkRule(RuleWorkItemId.atsRequireTargetedVersion.name());
      }

      // Relate to actionable items
      for (ActionableItemArtifact aia : actionableItems) {
         addRelation(AtsRelation.TeamActionableItem_ActionableItem, aia);
      }
      AtsCache.cache(this);
   }

   public static Set<TeamDefinitionArtifact> getTopLevelTeamDefinitions(Active active) throws OseeCoreException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false),
            active, TeamDefinitionArtifact.class);
   }

   public Branch getParentBranch() throws OseeCoreException {
      try {
         Integer branchId = getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), 0);
         if (branchId != null && branchId > 0) {
            return BranchManager.getBranch(branchId);
         }
      } catch (BranchDoesNotExist ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
      if (!isTeamUsesVersions()) return null;
      if (getVersionsArtifacts().size() > 0) return this;
      if (getParent() instanceof TeamDefinitionArtifact) {
         TeamDefinitionArtifact parentTda = (TeamDefinitionArtifact) getParent();
         if (parentTda != null) return parentTda.getTeamDefinitionHoldingVersions();
      }
      return null;
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with work flow.
    * 
    * @return parent TeamDefinition that holds the work flow id attribute
    */
   public TeamDefinitionArtifact getTeamDefinitionHoldingWorkFlow() throws OseeCoreException {
      for (Artifact artifact : getRelatedArtifacts(AtsRelation.WorkItem__Child, Artifact.class)) {
         if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            return this;
         }
      }
      if (getParent() instanceof TeamDefinitionArtifact) {
         TeamDefinitionArtifact parentTda = (TeamDefinitionArtifact) getParent();
         if (parentTda != null) return parentTda.getTeamDefinitionHoldingWorkFlow();
      }
      return null;
   }

   public VersionArtifact getNextReleaseVersion() throws OseeCoreException {
      for (VersionArtifact verArt : getRelatedArtifacts(AtsRelation.TeamDefinitionToVersion_Version,
            VersionArtifact.class)) {
         if (verArt.getSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), false)) {
            return verArt;
         }
      }
      return null;
   }

   public Collection<VersionArtifact> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType) throws OseeCoreException {
      TeamDefinitionArtifact teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) return new ArrayList<VersionArtifact>();
      return teamDef.getVersionsArtifacts(releaseType);
   }

   public static List<TeamDefinitionArtifact> getTeamDefinitions(Active active) throws OseeCoreException {
      return AtsCache.getArtifactsByActive(active, TeamDefinitionArtifact.class);
   }

   public static Set<TeamDefinitionArtifact> getTeamTopLevelDefinitions(Active active) throws OseeCoreException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false),
            active, TeamDefinitionArtifact.class);
   }

   public static TeamDefinitionArtifact getTopTeamDefinition() throws OseeCoreException {
      return (TeamDefinitionArtifact) StaticIdManager.getSingletonArtifactOrException(
            TeamDefinitionArtifact.ARTIFACT_NAME, TOP_TEAM_STATIC_ID, AtsPlugin.getAtsBranch());
   }

   public static Set<TeamDefinitionArtifact> getTeamReleaseableDefinitions(Active active) throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamDefinitionArtifact teamDef : getTeamDefinitions(active)) {
         if (teamDef.getVersionsArtifacts().size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException {
      Set<TeamDefinitionArtifact> resultTeams = new HashSet<TeamDefinitionArtifact>();
      for (ActionableItemArtifact aia : aias) {
         resultTeams.addAll(getImpactedTeamDefInherited(aia));
      }
      return resultTeams;
   }

   private static List<TeamDefinitionArtifact> getImpactedTeamDefInherited(ActionableItemArtifact aia) throws OseeCoreException {
      if (aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team).size() > 0) {
         return aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team, TeamDefinitionArtifact.class);
      }
      Artifact parentArt = aia.getParent();
      if (parentArt instanceof ActionableItemArtifact) return getImpactedTeamDefInherited((ActionableItemArtifact) parentArt);
      return java.util.Collections.emptyList();
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws OseeCoreException {
      Set<TeamDefinitionArtifact> aiaTeams = new HashSet<TeamDefinitionArtifact>();
      getTeamFromItemAndChildren(aia, aiaTeams);
      return aiaTeams;
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(TeamDefinitionArtifact teamDef) throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      teamDefs.add(teamDef);
      for (Artifact art : teamDef.getChildren()) {
         if (art instanceof TeamDefinitionArtifact) {
            teamDefs.addAll(getTeamsFromItemAndChildren((TeamDefinitionArtifact) art));
         }
      }
      return teamDefs;
   }

   private static void getTeamFromItemAndChildren(ActionableItemArtifact aia, Set<TeamDefinitionArtifact> aiaTeams) throws OseeCoreException {
      if (aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team).size() > 0) {
         aiaTeams.addAll(aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team, TeamDefinitionArtifact.class));
      }
      for (Artifact childArt : aia.getChildren()) {
         if (childArt instanceof ActionableItemArtifact) {
            getTeamFromItemAndChildren((ActionableItemArtifact) childArt, aiaTeams);
         }
      }
   }

   public double getManDayHrsFromItemAndChildren() {
      return getManDayHrsFromItemAndChildren(this);
   }

   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException {
      Artifact teamDef = getTeamDefinitionHoldingWorkFlow();
      if (teamDef == null) return null;
      Artifact workFlowArt = null;
      for (Artifact artifact : teamDef.getRelatedArtifacts(AtsRelation.WorkItem__Child, Artifact.class)) {
         if (artifact.getArtifactTypeName().equals(WorkFlowDefinition.ARTIFACT_NAME)) {
            if (workFlowArt != null) {
               OseeLog.log(
                     AtsPlugin.class,
                     Level.SEVERE,
                     "Multiple workflows found where only one expected for Team Definition " + getHumanReadableId() + " - " + getDescriptiveName());
            }
            workFlowArt = artifact;
         }
      }
      if (workFlowArt == null) {
         return null;
      }
      return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowArt.getDescriptiveName());
   }

   /**
    * Return rules associated with team definition . Use SMAManager.getWorkRulesStartsWith to acquire these and work
    * page rules and workflow rules.
    * 
    * @param ruleId
    * @return rule definitions
    * @throws OseeCoreException
    */
   public Collection<WorkRuleDefinition> getWorkRulesStartsWith(String ruleId) throws OseeCoreException {
      Set<WorkRuleDefinition> workRules = new HashSet<WorkRuleDefinition>();
      if (ruleId == null || ruleId.equals("")) return workRules;
      // Get work rules from team definition
      for (WorkRuleDefinition workRuleDefinition : getWorkRules()) {
         if (!workRuleDefinition.getId().equals("") && workRuleDefinition.getId().startsWith(ruleId)) {
            workRules.add(workRuleDefinition);
         }
      }

      return workRules;
   }

   public Collection<WorkRuleDefinition> getWorkRules() throws OseeCoreException {
      Set<WorkRuleDefinition> workRules = new HashSet<WorkRuleDefinition>();
      // Get work rules from team definition
      for (Artifact art : getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child)) {
         if (art.getArtifactTypeName().equals(WorkRuleDefinition.ARTIFACT_NAME)) {
            String id = art.getSoleAttributeValue(WorkItemAttributes.WORK_ID.getAttributeTypeName(), "");
            if (id != null && !id.equals("")) {
               workRules.add((WorkRuleDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(id));
            }
         }
      }

      return workRules;
   }

   /**
    * If man days hours attribute is set, use it, otherwise, walk up the Team Definition tree. Value used in
    * calculations.
    * 
    * @param teamDef
    * @return number of hours per single person per single day
    */
   public double getManDayHrsFromItemAndChildren(TeamDefinitionArtifact teamDef) {
      try {
         Double manDaysHrs = teamDef.getSoleAttributeValue(ATSAttributes.MAN_DAYS_NEEDED_ATTRIBUTE.getStoreName(), 0.0);
         if (manDaysHrs != null && manDaysHrs != 0) return manDaysHrs;
         if (teamDef.getParent() != null && (teamDef.getParent() instanceof TeamDefinitionArtifact)) return teamDef.getManDayHrsFromItemAndChildren((TeamDefinitionArtifact) teamDef.getParent());
         return StateMachineArtifact.DEFAULT_MAN_HOURS_PER_DAY;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
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
    * @throws OseeCoreException
    */
   public Collection<User> getLeads() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.TeamLead_Lead, User.class);
   }

   public Collection<User> getPrivilegedMembers() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.PrivilegedMember_Member, User.class);
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this TeamDefinitionArtifact.
    * 
    * @param actionableItems
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    */
   public Collection<User> getLeads(Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException {
      Set<User> leads = new HashSet<User>();
      for (ActionableItemArtifact aia : actionableItems) {
         if (aia.getImpactedTeamDefs().contains(this)) {
            // If leads are specified for this aia, add them
            if (aia.getLeads().size() > 0)
               leads.addAll(aia.getLeads());
            // Otherwise, add team definition's leads
            else {
               for (TeamDefinitionArtifact teamDef : aia.getImpactedTeamDefs()) {
                  leads.addAll(teamDef.getLeads());
               }
            }
         }
      }
      if (leads.size() == 0) leads.addAll(getLeads());
      return leads;
   }

   public Collection<User> getMembers() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.TeamMember_Member, User.class);
   }

   public VersionArtifact getVersionArtifact(String name, boolean create) throws OseeCoreException {
      for (VersionArtifact verArt : getVersionsArtifacts()) {
         if (verArt.getDescriptiveName().equals(name)) return verArt;
      }
      if (create) {
         return createVersion(name);
      }
      return null;
   }

   public VersionArtifact createVersion(String name) throws OseeCoreException {
      VersionArtifact versionArt =
            (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME, AtsPlugin.getAtsBranch(),
                  name);
      addRelation(AtsRelation.TeamDefinitionToVersion_Version, versionArt);
      AtsCache.cache(versionArt);
      return versionArt;
   }

   public Collection<VersionArtifact> getVersionsArtifacts() throws OseeCoreException {
      return getRelatedArtifacts(AtsRelation.TeamDefinitionToVersion_Version, VersionArtifact.class);
   }

   public Collection<VersionArtifact> getVersionsArtifacts(VersionReleaseType releaseType) throws OseeCoreException {
      ArrayList<VersionArtifact> versions = new ArrayList<VersionArtifact>();
      for (VersionArtifact version : getVersionsArtifacts()) {
         if (version.isReleased()) {
            if (releaseType == VersionReleaseType.Released || releaseType == VersionReleaseType.Both) versions.add(version);
         } else {
            if (releaseType == VersionReleaseType.UnReleased || releaseType == VersionReleaseType.Both) versions.add(version);
         }
      }
      return versions;
   }

   public boolean isTeamUsesVersions() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), false);
   }

   public boolean isActionable() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
   }

   public void addWorkRule(String ruleId) throws OseeCoreException {
      if (!hasWorkRule(ruleId)) {
         Artifact artifact = WorkItemDefinitionFactory.getWorkItemDefinitionArtifact(ruleId);
         if (artifact == null)
            throw new IllegalArgumentException("Rule \"" + ruleId + "\" does not exist.");
         else {
            addRelation(CoreRelationEnumeration.WorkItem__Child, artifact);
         }
      }
   }

   public boolean hasWorkRule(String ruleId) throws OseeCoreException {
      for (Artifact art : getRelatedArtifacts(CoreRelationEnumeration.WorkItem__Child)) {
         if (art.getDescriptiveName().equals(ruleId)) {
            return true;
         }
      }
      return false;
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/>
    * <br/>
    * If no branch is associated then null will be returned.
    * 
    * @throws BranchDoesNotExist
    */
   public Branch getTeamBranch() throws OseeCoreException {
      Integer branchId = getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), null);
      if (branchId != null && branchId > 0) {
         return BranchManager.getBranch(branchId);
      } else {
         Artifact parent = getParent();
         if (parent instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) parent).getTeamBranch();
         }
      }
      return null;
   }

   public static Set<TeamDefinitionArtifact> getTeamDefinitions(Collection<String> teamDefNames) throws OseeCoreException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (String teamDefName : teamDefNames) {
         teamDefs.addAll(AtsCache.getArtifactsByName(teamDefName, TeamDefinitionArtifact.class));
      }
      return teamDefs;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.util.widgets.commit.ICommitConfigArtifact#getFullDisplayName()
    */
   @Override
   public String getFullDisplayName() throws OseeCoreException {
      return getDescriptiveName();
   }

}
