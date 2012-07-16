/*
 * Created on Jun 4, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitions {
   public static String TopTeamDefinitionGuid = "AAABER+35b4A8O7WHrXTiA";

   public static Collection<String> getNames(Collection<? extends IAtsTeamDefinition> teamDefs) {
      ArrayList<String> names = new ArrayList<String>();
      for (IAtsTeamDefinition named : teamDefs) {
         names.add(named.getName());
      }
      return names;
   }

   public static List<String> toGuids(Collection<? extends IAtsTeamDefinition> teamDefs) {
      List<String> guids = new ArrayList<String>(teamDefs.size());
      for (IAtsTeamDefinition artifact : teamDefs) {
         guids.add(artifact.getGuid());
      }
      return guids;
   }

   public static List<IAtsTeamDefinition> getTopLevelTeamDefinitions(Active active) throws OseeCoreException {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   public static List<IAtsTeamDefinition> getActive(Collection<IAtsTeamDefinition> teamDefs, Active active) {
      List<IAtsTeamDefinition> results = new ArrayList<IAtsTeamDefinition>();
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

   public static Set<IAtsTeamDefinition> getChildren(IAtsTeamDefinition topTeamDef, boolean recurse) throws OseeCoreException {
      Set<IAtsTeamDefinition> children = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition child : topTeamDef.getChildrenTeamDefinitions()) {
         children.add(child);
         if (recurse) {
            children.addAll(getChildren(child, recurse));
         }
      }
      return children;
   }

   public static List<IAtsTeamDefinition> getTeamDefinitions(Active active) {
      return Collections.castAll(getActive(AtsConfigCache.get(IAtsTeamDefinition.class), active));
   }

   public static List<IAtsTeamDefinition> getTeamTopLevelDefinitions(Active active) throws OseeCoreException {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   public static IAtsTeamDefinition getTopTeamDefinition() {
      return AtsConfigCache.getSoleByGuid(TopTeamDefinitionGuid, IAtsTeamDefinition.class);
   }

   public static Set<IAtsTeamDefinition> getTeamReleaseableDefinitions(Active active) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(active)) {
         if (teamDef.getVersions().size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem aia) throws OseeCoreException {
      Set<IAtsTeamDefinition> aiaTeams = new HashSet<IAtsTeamDefinition>();
      getTeamFromItemAndChildren(aia, aiaTeams);
      return aiaTeams;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsTeamDefinition teamDef) throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      teamDefs.add(teamDef);
      for (IAtsTeamDefinition child : teamDef.getChildrenTeamDefinitions()) {
         teamDefs.addAll(getTeamsFromItemAndChildren(child));
      }
      return teamDefs;
   }

   private static void getTeamFromItemAndChildren(IAtsActionableItem aia, Set<IAtsTeamDefinition> aiaTeams) throws OseeCoreException {
      aiaTeams.add(aia.getTeamDefinition());

      for (IAtsActionableItem childArt : aia.getChildrenActionableItems()) {
         getTeamFromItemAndChildren(childArt, aiaTeams);
      }
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitions(Collection<String> teamDefNames) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : AtsConfigCache.get(IAtsTeamDefinition.class)) {
         if (teamDefNames.contains(teamDef.getName())) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitionsNameStartsWith(String prefix) {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : AtsConfigCache.get(IAtsTeamDefinition.class)) {
         if (teamDef.getName().startsWith(prefix)) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> aias) throws OseeCoreException {
      Set<IAtsTeamDefinition> resultTeams = new HashSet<IAtsTeamDefinition>();
      for (IAtsActionableItem aia : aias) {
         resultTeams.addAll(getImpactedTeamDefInherited(aia));
      }
      return resultTeams;
   }

   public static IAtsTeamDefinition getImpactedTeamDef(IAtsActionableItem aia) {
      if (aia.getTeamDefinition() != null) {
         return aia.getTeamDefinition();
      }
      if (aia.getParentActionableItem() != null) {
         return getImpactedTeamDef(aia.getParentActionableItem());
      }
      return null;
   }

   private static Collection<IAtsTeamDefinition> getImpactedTeamDefInherited(IAtsActionableItem aia) throws OseeCoreException {
      if (aia == null) {
         return java.util.Collections.emptyList();
      }
      if (aia.getTeamDefinition() != null) {
         return java.util.Collections.singleton(aia.getTeamDefinition());
      }
      IAtsActionableItem parentArt = aia.getParentActionableItem();
      return getImpactedTeamDefInherited(parentArt);
   }

}
