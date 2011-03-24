/*
 * Created on Mar 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.AtsArtifactTypes;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

public class TeamDefinitionManager {
   public static List<TeamDefinitionArtifact> getTopLevelTeamDefinitions(Active active) throws OseeCoreException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(AtsUtil.getActive(
         Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false), active,
         TeamDefinitionArtifact.class));
   }

   public static List<TeamDefinitionArtifact> getTeamDefinitions(Active active) throws OseeCoreException {
      return Collections.castAll(AtsCacheManager.getArtifactsByActive(AtsArtifactTypes.TeamDefinition, active));
   }

   public static List<TeamDefinitionArtifact> getTeamTopLevelDefinitions(Active active) throws OseeCoreException {
      TeamDefinitionArtifact topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(AtsUtil.getActive(
         Artifacts.getChildrenOfTypeSet(topTeamDef, TeamDefinitionArtifact.class, false), active,
         TeamDefinitionArtifact.class));
   }

   public static TeamDefinitionArtifact getTopTeamDefinition() {
      return (TeamDefinitionArtifact) AtsUtil.getFromToken(AtsArtifactToken.TopTeamDefinition);
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
      if (aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team).size() > 0) {
         return aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team, TeamDefinitionArtifact.class);
      }
      Artifact parentArt = aia.getParent();
      if (parentArt instanceof ActionableItemArtifact) {
         return getImpactedTeamDefInherited((ActionableItemArtifact) parentArt);
      }
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
      if (aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team).size() > 0) {
         aiaTeams.addAll(aia.getRelatedArtifacts(AtsRelationTypes.TeamActionableItem_Team, TeamDefinitionArtifact.class));
      }
      for (Artifact childArt : aia.getChildren()) {
         if (childArt instanceof ActionableItemArtifact) {
            getTeamFromItemAndChildren((ActionableItemArtifact) childArt, aiaTeams);
         }
      }
   }

   public static Set<TeamDefinitionArtifact> getTeamDefinitions(Collection<String> teamDefNames) {
      Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
      for (String teamDefName : teamDefNames) {
         for (Artifact artifact : AtsCacheManager.getArtifactsByName(AtsArtifactTypes.TeamDefinition, teamDefName)) {
            teamDefs.add((TeamDefinitionArtifact) artifact);
         }
      }
      return teamDefs;
   }

}
