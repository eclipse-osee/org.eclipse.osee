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
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.config.AtsConfig;
import org.eclipse.osee.ats.util.AtsLib;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BasicArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.search.Active;
import org.eclipse.osee.framework.skynet.core.artifact.search.ActiveArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactStaticIdSearch.SearchOperator;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.util.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionArtifact extends BasicArtifact {

   public static String ARTIFACT_NAME = "Team Definition";
   public static String TOP_TEAM_STATIC_ID = "osee.ats.TopTeamDefinition";
   public static Set<TeamDefinitionArtifact> EMPTY_SET = new HashSet<TeamDefinitionArtifact>();

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public TeamDefinitionArtifact(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);
   }

   public static TeamDefinitionArtifact createNewTeamDefinition(String name, String fullname, String description, Collection<User> leads, Collection<User> members, boolean usesVersions, Collection<ActionableItemArtifact> actionableItems, Artifact parentTeamDef) throws SQLException {
      TeamDefinitionArtifact tda = null;
      tda =
            (TeamDefinitionArtifact) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                  TeamDefinitionArtifact.ARTIFACT_NAME).makeNewArtifact(
                  BranchPersistenceManager.getInstance().getAtsBranch());
      tda.setDescriptiveName(name);
      tda.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), description);
      tda.setSoleAttributeValue(ATSAttributes.FULL_NAME_ATTRIBUTE.getStoreName(), fullname);
      for (User user : leads) {
         tda.relate(RelationSide.TeamLead_Lead, user);
         // All leads are members
         tda.relate(RelationSide.TeamMember_Member, user);
      }
      for (User user : members) {
         tda.relate(RelationSide.TeamMember_Member, user);
      }

      if (usesVersions) tda.setSoleAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName(), "yes");
      tda.persist(true);

      Artifact parentTeamDefinition = parentTeamDef;
      if (parentTeamDefinition == null) {
         // Relate to team heading
         parentTeamDef = AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
      }
      parentTeamDef.addChild(tda);
      parentTeamDef.persist(true);

      // Relate to actionable items
      for (ActionableItemArtifact aia : actionableItems) {
         tda.relate(RelationSide.TeamActionableItem_ActionableItem, aia);
      }

      tda.persist(true);
      return tda;
   }

   public static TeamDefinitionArtifact getOrCreateTeamsDefinitionArtifact() throws SQLException {
      return AtsConfig.getInstance().getOrCreateTeamsDefinitionArtifact();
   }

   /**
    * This method will walk up the TeamDefinition tree until a def is found that configured with versions. This allows
    * multiple TeamDefinitions to be versioned/released together by having the parent hold the versions. It is not
    * required that a product configured in ATS uses the versions option. If no parent with versions is found, null is
    * returned. If boolean "Team Uses Versions" is false, just return cause this team doesn't use versions
    * 
    * @return parent TeamDefinition that holds the version definintions
    */
   public TeamDefinitionArtifact getTeamDefinitionHoldingVersions() throws SQLException {
      if (!isTeamUsesVersions()) return null;
      if (getVersionsArtifacts().size() > 0) return this;
      if (getParent() instanceof TeamDefinitionArtifact) {
         TeamDefinitionArtifact parentTda = (TeamDefinitionArtifact) getParent();
         if (parentTda != null) return parentTda.getTeamDefinitionHoldingVersions();
      }
      return null;
   }

   public VersionArtifact getNextReleaseVersion() throws SQLException {
      for (VersionArtifact verArt : getArtifacts(RelationSide.TeamDefinitionToVersion_Version, VersionArtifact.class)) {
         if (verArt.getSoleBooleanAttributeValue(ATSAttributes.NEXT_VERSION_ATTRIBUTE.getStoreName())) {
            return verArt;
         }
      }
      return null;
   }

   public Collection<VersionArtifact> getVersionsFromTeamDefHoldingVersions(VersionReleaseType releaseType) throws SQLException {
      TeamDefinitionArtifact teamDef = getTeamDefinitionHoldingVersions();
      if (teamDef == null) return new ArrayList<VersionArtifact>();
      return teamDef.getVersionsArtifacts(releaseType);
   }

   public static Set<TeamDefinitionArtifact> getTeamDefinitions(Active active) throws SQLException {
      ActiveArtifactTypeSearch search =
            new ActiveArtifactTypeSearch(ARTIFACT_NAME, active, BranchPersistenceManager.getInstance().getAtsBranch());
      return search.getArtifacts(TeamDefinitionArtifact.class);
   }

   public static Set<TeamDefinitionArtifact> getTeamTopLevelDefinitions(Active active) throws SQLException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) return EMPTY_SET;
      return AtsLib.getActiveSet(Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false),
            active, TeamDefinitionArtifact.class);
   }

   public static TeamDefinitionArtifact getTopTeamDefinition() throws SQLException {
      return ArtifactStaticIdSearch.getSingletonArtifactOrException(TeamDefinitionArtifact.ARTIFACT_NAME,
            TOP_TEAM_STATIC_ID, BranchPersistenceManager.getInstance().getAtsBranch(), SearchOperator.EQUAL,
            TeamDefinitionArtifact.class);
   }

   public static Set<TeamDefinitionArtifact> getTeamReleaseableDefinitions(Active active) throws SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      ActiveArtifactTypeSearch search =
            new ActiveArtifactTypeSearch(ARTIFACT_NAME, active, BranchPersistenceManager.getInstance().getAtsBranch());
      for (TeamDefinitionArtifact teamDef : search.getArtifacts(TeamDefinitionArtifact.class)) {
         if (teamDef.getVersionsArtifacts().size() > 0 && teamDef.getSoleBooleanAttributeValue(ATSAttributes.ACTIVE_ATTRIBUTE.getStoreName())) teamDefs.add(teamDef);
      }
      return teamDefs;
   }

   public static Set<TeamDefinitionArtifact> getImpactedTeamDefs(Set<ActionableItemArtifact> aias) throws SQLException {
      Set<TeamDefinitionArtifact> resultTeams = new HashSet<TeamDefinitionArtifact>();
      for (ActionableItemArtifact aia : aias) {
         Set<TeamDefinitionArtifact> aiaTeams = getImpactedTeamDef(aia);
         if (aiaTeams == null) throw new IllegalArgumentException(
               "No team workflow associated with Actionable Item \"" + aia + "\" (" + aia.getHumanReadableId() + ") or any parent.\n\nAction can not be written against this item.");
         resultTeams.addAll(aiaTeams);
      }
      return resultTeams;
   }

   public static Set<TeamDefinitionArtifact> getImpactedTeamDef(ActionableItemArtifact aia) throws SQLException {
      if (aia.getArtifacts(RelationSide.TeamActionableItem_Team).size() > 0) return aia.getArtifacts(
            RelationSide.TeamActionableItem_Team, TeamDefinitionArtifact.class);
      Artifact parentArt = aia.getParent();
      if (parentArt instanceof ActionableItemArtifact) return getImpactedTeamDef((ActionableItemArtifact) parentArt);
      return null;
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(ActionableItemArtifact aia) throws SQLException {
      Set<TeamDefinitionArtifact> aiaTeams = new HashSet<TeamDefinitionArtifact>();
      getTeamFromItemAndChildren(aia, aiaTeams);
      return aiaTeams;
   }

   public static void getTeamFromItemAndChildren(ActionableItemArtifact aia, Set<TeamDefinitionArtifact> aiaTeams) throws SQLException {
      if (aia.getArtifacts(RelationSide.TeamActionableItem_Team).size() > 0) aiaTeams.addAll(aia.getArtifacts(
            RelationSide.TeamActionableItem_Team, TeamDefinitionArtifact.class));
      for (Artifact childArt : aia.getChildren()) {
         if (childArt instanceof ActionableItemArtifact) getTeamFromItemAndChildren((ActionableItemArtifact) childArt,
               aiaTeams);
      }
   }

   public static Set<TeamDefinitionArtifact> getTeamsFromItemAndChildren(TeamDefinitionArtifact teamDef) throws SQLException {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      getTeamFromItemAndChildren(Arrays.asList(new TeamDefinitionArtifact[] {teamDef}), teamDefs);
      return teamDefs;
   }

   public static void getTeamFromItemAndChildren(Collection<TeamDefinitionArtifact> teamDefs, Set<TeamDefinitionArtifact> returnTeamDefs) throws SQLException {
      for (TeamDefinitionArtifact teamDef : teamDefs) {
         returnTeamDefs.add(teamDef);
         for (Artifact childArt : teamDef.getChildren()) {
            if (childArt instanceof TeamDefinitionArtifact) getTeamFromItemAndChildren(
                  Arrays.asList(new TeamDefinitionArtifact[] {(TeamDefinitionArtifact) childArt}), returnTeamDefs);
         }
      }
   }

   public static TeamDefinitionArtifact getHeadTeamDefinition() throws SQLException {
      return (new ArtifactTypeNameSearch(TeamDefinitionArtifact.ARTIFACT_NAME, AtsConfig.TEAMS_HEADING,
            BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(TeamDefinitionArtifact.class);
   }

   public double getManDayHrsFromItemAndChildren() throws SQLException {
      return getManDayHrsFromItemAndChildren(this);
   }

   /**
    * If man days hours attribute is set, use it, otherwise, walk up the Team Definition tree. Value used in
    * calculations.
    * 
    * @param teamDef
    * @return number of hours per single person per single day
    * @throws SQLException
    */
   public double getManDayHrsFromItemAndChildren(TeamDefinitionArtifact teamDef) throws SQLException {
      Double manDaysHrs = teamDef.getSoleDoubleAttributeValue(ATSAttributes.MAN_DAYS_NEEDED_ATTRIBUTE.getStoreName());
      if (manDaysHrs != null && manDaysHrs != 0) return manDaysHrs;
      if (teamDef.getParent() != null && (teamDef.getParent() instanceof TeamDefinitionArtifact)) return teamDef.getManDayHrsFromItemAndChildren((TeamDefinitionArtifact) teamDef.getParent());
      return StateMachineArtifact.MAN_DAY_HOURS;
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
      return getArtifacts(RelationSide.TeamLead_Lead, User.class);
   }

   /**
    * Returns leads configured first by ActionableItems and only if this is an empty set, THEN defaults to those
    * configured by TeamDefinitions. Use getLeads() to only get the leads configured for this TeamDefinitionArtifact.
    * 
    * @param actionableItems
    * @return users configured as leads by ActionableItems, then by TeamDefinition
    * @throws SQLException
    */
   public Collection<User> getLeads(Collection<ActionableItemArtifact> actionableItems) throws SQLException {
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
      return getArtifacts(RelationSide.TeamMember_Member, User.class);
   }

   public VersionArtifact getVersionArtifact(String name, boolean create) throws SQLException {
      for (VersionArtifact verArt : getVersionsArtifacts()) {
         if (verArt.getDescriptiveName().equals(name)) return verArt;
      }
      if (create) return createVersion(name);
      return null;
   }

   public VersionArtifact createVersion(String name) {
      try {
         VersionArtifact versionArt =
               (VersionArtifact) configurationPersistenceManager.getArtifactSubtypeDescriptor(
                     VersionArtifact.ARTIFACT_NAME).makeNewArtifact(
                     BranchPersistenceManager.getInstance().getAtsBranch());
         versionArt.setDescriptiveName(name);
         versionArt.persistAttributes();

         relate(RelationSide.TeamDefinitionToVersion_Version, versionArt, true);
         return versionArt;
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
      return null;
   }

   public Collection<VersionArtifact> getVersionsArtifacts() throws SQLException {
      return getArtifacts(RelationSide.TeamDefinitionToVersion_Version, VersionArtifact.class);
   }

   public Collection<VersionArtifact> getVersionsArtifacts(VersionReleaseType releaseType) throws SQLException {
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

   public boolean isTeamUsesVersions() throws IllegalStateException, SQLException {
      return getSoleBooleanAttributeValue(ATSAttributes.TEAM_USES_VERSIONS_ATTRIBUTE.getStoreName());
   }

   /**
    * Returns the branch associated with this team. If this team does not have a branch associated then the parent team
    * will be asked, this results in a recursive look at parent teams until a parent artifact has a related branch or
    * the parent of a team is not a team. <br/><br/> If no branch is associated then null will be returned.
    * 
    * @throws SQLException
    */
   public Branch getTeamBranch() throws SQLException {
      Integer branchId = getSoleIntegerAttributeValue(ATSAttributes.PARENT_BRANCH_ID_ATTRIBUTE.getStoreName());
      if (branchId != null && branchId > 0) {
         return branchManager.getBranch(branchId);
      } else {
         Artifact parent = getParent();
         if (parent instanceof TeamDefinitionArtifact) {
            return ((TeamDefinitionArtifact) parent).getTeamBranch();
         }
      }
      return null;
   }
}
