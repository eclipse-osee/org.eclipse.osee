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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkFlowDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifact extends BasicArtifact {

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
    * @throws SQLException
    */
   public TeamDefinitionArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
   }

   public static TeamDefinitionArtifact createNewTeamDefinition(String name, String fullname, String description, Collection<User> leads, Collection<User> members, Collection<ActionableItemArtifact> actionableItems, Artifact parentTeamDef, TeamDefinitionOptions... teamDefinitionOptions) throws OseeCoreException, SQLException {
      List<Object> teamDefOptions = Collections.getAggregate((Object[]) teamDefinitionOptions);
      TeamDefinitionArtifact tda = null;
      tda =
            (TeamDefinitionArtifact) ArtifactTypeManager.addArtifact(TeamDefinitionArtifact.ARTIFACT_NAME,
                  BranchPersistenceManager.getAtsBranch(), name);
      tda.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), description);
      tda.setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), fullname);
      for (User user : leads) {
         tda.addRelation(AtsRelation.TeamLead_Lead, user);
         // All leads are members
         tda.addRelation(AtsRelation.TeamMember_Member, user);
      }
      for (User user : members) {
         tda.addRelation(AtsRelation.TeamMember_Member, user);
      }

      if (teamDefOptions.contains(TeamDefinitionOptions.TeamUsesVersions)) {
         tda.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), true);
      }
      if (teamDefOptions.contains(TeamDefinitionOptions.RequireTargetedVersion)) {
         tda.setSoleAttributeValue(ATSAttributes.REQUIRE_TARGETED_VERSION_ATTRIBUTE.getStoreName(), true);
      }
      tda.persistAttributesAndRelations();

      Artifact parentTeamDefinition = parentTeamDef;
      if (parentTeamDefinition == null) {
         // Relate to team heading
         parentTeamDef = AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      }
      parentTeamDef.addChild(tda);
      parentTeamDef.persistAttributesAndRelations();

      // Relate to actionable items
      for (ActionableItemArtifact aia : actionableItems) {
         tda.addRelation(AtsRelation.TeamActionableItem_ActionableItem, aia);
      }

      tda.persistAttributesAndRelations();
      AtsCache.cache(tda);
      return tda;
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with versions. This allows
    * multiple TeamDefinitions to be versioned/released together by having the parent hold the versions. It is not
    * required that a product configured in ATS uses the versions option. If no parent with versions is found, null is
    * returned. If boolean "Team Uses Versions" is false, just return cause this team doesn't use versions
    * 
    * @return parent TeamDefinition that holds the version definitions
    */
   public TeamDefinitionArtifact getTeamDefinitionHoldingVersions() throws SQLException, MultipleAttributesExist {
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
   public TeamDefinitionArtifact getTeamDefinitionHoldingWorkFlow() throws SQLException {
      if (getArtifacts(AtsRelation.WorkItem__Child, Artifact.class).size() > 0) return this;
      if (getParent() instanceof TeamDefinitionArtifact) {
         TeamDefinitionArtifact parentTda = (TeamDefinitionArtifact) getParent();
         if (parentTda != null) return parentTda.getTeamDefinitionHoldingWorkFlow();
      }
      return null;
   }

   public VersionArtifact getNextReleaseVersion() throws SQLException, MultipleAttributesExist {
      for (VersionArtifact verArt : getArtifacts(AtsRelation.TeamDefinitionToVersion_Version, VersionArtifact.class)) {
         if (verArt.getSoleAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName(), false)) {
            return verArt;
         }
      }
      return null;
   }

   public Collection<VersionArtifact> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType) throws SQLException, MultipleAttributesExist {
      TeamDefinitionArtifact teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) return new ArrayList<VersionArtifact>();
      return teamDef.getVersionsArtifacts(releaseType);
   }

   public static List<TeamDefinitionArtifact> getTeamDefinitions(Active active) throws OseeCoreException {
      return AtsCache.getArtifactsByActive(active, TeamDefinitionArtifact.class);
   }

   public static Set<TeamDefinitionArtifact> getTeamTopLevelDefinitions(Active active) throws SQLException, OseeCoreException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false),
            active, TeamDefinitionArtifact.class);
   }

   public static TeamDefinitionArtifact getTopTeamDefinition() throws OseeCoreException {
      return (TeamDefinitionArtifact) StaticIdQuery.getSingletonArtifactOrException(
            TeamDefinitionArtifact.ARTIFACT_NAME, TOP_TEAM_STATIC_ID, BranchPersistenceManager.getAtsBranch());
   }

   public static Set<TeamDefinitionArtifact> getTeamReleaseableDefinitions(Active active) throws OseeCoreException, SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (TeamDefinitionArtifact teamDef : getTeamDefinitions(active)) {
         if (teamDef.getVersionsArtifacts().size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Collection<TeamDefinitionArtifact> getImpactedTeamDefs(Collection<ActionableItemArtifact> aias) throws OseeCoreException, SQLException {
      Set<TeamDefinitionArtifact> resultTeams = new HashSet<TeamDefinitionArtifact>();
      for (ActionableItemArtifact aia : aias) {
         resultTeams.addAll(getImpactedTeamDefInherited(aia));
      }
      return resultTeams;
   }

   private static List<TeamDefinitionArtifact> getImpactedTeamDefInherited(ActionableItemArtifact aia) throws OseeCoreException, SQLException {
      if (aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team).size() > 0) {
         return aia.getArtifacts(AtsRelation.TeamActionableItem_Team, TeamDefinitionArtifact.class);
      }
      Artifact parentArt = aia.getParent();
      if (parentArt instanceof ActionableItemArtifact) return getImpactedTeamDefInherited((ActionableItemArtifact) parentArt);
      return java.util.Collections.emptyList();
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws SQLException {
      Set<TeamDefinitionArtifact> aiaTeams = new HashSet<TeamDefinitionArtifact>();
      getTeamFromItemAndChildren(aia, aiaTeams);
      return aiaTeams;
   }

   private static void getTeamFromItemAndChildren(ActionableItemArtifact aia, Set<TeamDefinitionArtifact> aiaTeams) throws SQLException {
      if (aia.getRelatedArtifacts(AtsRelation.TeamActionableItem_Team).size() > 0) aiaTeams.addAll(aia.getArtifacts(
            AtsRelation.TeamActionableItem_Team, TeamDefinitionArtifact.class));
      for (Artifact childArt : aia.getChildren()) {
         if (childArt instanceof ActionableItemArtifact) getTeamFromItemAndChildren((ActionableItemArtifact) childArt,
               aiaTeams);
      }
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(TeamDefinitionArtifact teamDef) throws SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      getTeamFromItemAndChildren(Arrays.asList(teamDef), teamDefs);
      return teamDefs;
   }

   private static void getTeamFromItemAndChildren(Collection<TeamDefinitionArtifact> teamDefs, Set<TeamDefinitionArtifact> returnTeamDefs) throws SQLException {
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         returnTeamDefs.add(teamDef);
         for (Artifact childArt : teamDef.getChildren()) {
            if (childArt instanceof TeamDefinitionArtifact) getTeamFromItemAndChildren(
                  Arrays.asList((TeamDefinitionArtifact) childArt), returnTeamDefs);
         }
      }
   }

   public double getManDayHrsFromItemAndChildren() throws SQLException {
      return getManDayHrsFromItemAndChildren(this);
   }

   public WorkFlowDefinition getWorkFlowDefinition() throws OseeCoreException, SQLException {
      Artifact teamDef = getTeamDefinitionHoldingWorkFlow();
      if (teamDef == null) return null;
      Artifact workFlowArt =
            getTeamDefinitionHoldingWorkFlow().getArtifacts(AtsRelation.WorkItem__Child, Artifact.class).iterator().next();
      if (workFlowArt == null) return null;
      return (WorkFlowDefinition) WorkItemDefinitionFactory.getWorkItemDefinition(workFlowArt.getDescriptiveName());
   }

   /**
    * If man days hours attribute is set, use it, otherwise, walk up the Team Definition tree. Value used in
    * calculations.
    * 
    * @param teamDef
    * @return number of hours per single person per single day
    * @throws SQLException
    */
   public double getManDayHrsFromItemAndChildren(TeamDefinitionArtifact teamDef) {
      try {
         Double manDaysHrs = teamDef.getSoleAttributeValue(ATSAttributes.MAN_DAYS_NEEDED_ATTRIBUTE.getStoreName(), 0.0);
         if (manDaysHrs != null && manDaysHrs != 0) return manDaysHrs;
         if (teamDef.getParent() != null && (teamDef.getParent() instanceof TeamDefinitionArtifact)) return teamDef.getManDayHrsFromItemAndChildren((TeamDefinitionArtifact) teamDef.getParent());
         return StateMachineArtifact.MAN_DAY_HOURS;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
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
    * @throws SQLException
    */
   public Collection<User> getLeads() throws SQLException {
      return getArtifacts(AtsRelation.TeamLead_Lead, User.class);
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this TeamDefinitionArtifact.
    * 
    * @param actionableItems
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    * @throws SQLException
    */
   public Collection<User> getLeads(Collection<ActionableItemArtifact> actionableItems) throws OseeCoreException, SQLException {
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

   public Collection<User> getMembers() throws SQLException {
      return getArtifacts(AtsRelation.TeamMember_Member, User.class);
   }

   public VersionArtifact getVersionArtifact(String name, boolean create) throws OseeCoreException {
      try {
         for (VersionArtifact verArt : getVersionsArtifacts()) {
            if (verArt.getDescriptiveName().equals(name)) return verArt;
         }
         if (create) return createVersion(name);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
      return null;
   }

   public VersionArtifact createVersion(String name) throws OseeCoreException {
      try {
         VersionArtifact versionArt =
               (VersionArtifact) ArtifactTypeManager.addArtifact(VersionArtifact.ARTIFACT_NAME,
                     BranchPersistenceManager.getAtsBranch(), name);
         versionArt.addRelation(AtsRelation.TeamDefinitionToVersion_Version, versionArt);
         versionArt.persistAttributesAndRelations();
         AtsCache.cache(versionArt);
         return versionArt;
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public Collection<VersionArtifact> getVersionsArtifacts() throws SQLException {
      return getArtifacts(AtsRelation.TeamDefinitionToVersion_Version, VersionArtifact.class);
   }

   public Collection<VersionArtifact> getVersionsArtifacts(VersionReleaseType releaseType) throws SQLException, MultipleAttributesExist {
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

   public boolean isTeamUsesVersions() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), false);
   }

   public boolean isRequireTargetedVersion() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.REQUIRE_TARGETED_VERSION_ATTRIBUTE.getStoreName(), false);
   }

   public boolean isActionable() throws IllegalStateException, SQLException, MultipleAttributesExist {
      return getSoleAttributeValue(ATSAttributes.ACTIONABLE_ATTRIBUTE.getStoreName(), false);
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/><br/> If no branch is associated then null will be returned.
    * 
    * @throws SQLException
    * @throws BranchDoesNotExist
    */
   public Branch getTeamBranch() throws SQLException, MultipleAttributesExist, AttributeDoesNotExist, BranchDoesNotExist {
      Integer branchId = getSoleAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName(), null);
      if (branchId != null && branchId > 0) {
         return BranchPersistenceManager.getInstance().getBranch(branchId);
      } else {
         Artifact parent = getParent();
         if (parent instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) parent).getTeamBranch();
         }
      }
      return null;
   }

   public static Set<TeamDefinitionArtifact> getTeamDefinitions(Collection<String> teamDefNames) throws OseeCoreException, SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (String teamDefName : teamDefNames) {
         teamDefs.addAll(AtsCache.getArtifactsByName(teamDefName, TeamDefinitionArtifact.class));
      }
      return teamDefs;
   }

}
