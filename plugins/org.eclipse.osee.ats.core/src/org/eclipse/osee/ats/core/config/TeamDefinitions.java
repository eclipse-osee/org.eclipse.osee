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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitions {

   public static Collection<String> getNames(Collection<? extends IAtsTeamDefinition> teamDefs) {
      ArrayList<String> names = new ArrayList<>();
      for (IAtsTeamDefinition named : teamDefs) {
         names.add(named.getName());
      }
      return names;
   }

   public static List<IAtsTeamDefinition> getTopLevelTeamDefinitions(Active active, IAtsQueryService queryService)  {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinition(queryService);
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   public static List<IAtsTeamDefinition> getActive(Collection<IAtsTeamDefinition> teamDefs, Active active) {
      List<IAtsTeamDefinition> results = new ArrayList<>();
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

   public static Set<IAtsTeamDefinition> getChildren(IAtsTeamDefinition topTeamDef, boolean recurse)  {
      Set<IAtsTeamDefinition> children = new HashSet<>();
      for (IAtsTeamDefinition child : topTeamDef.getChildrenTeamDefinitions()) {
         children.add(child);
         if (recurse) {
            children.addAll(getChildren(child, recurse));
         }
      }
      return children;
   }

   public static List<IAtsTeamDefinition> getTeamDefinitions(Active active, IAtsQueryService queryService)  {
      return Collections.castAll(
         getActive(queryService.createQuery(AtsArtifactTypes.TeamDefinition).getConfigObjects(), active));
   }

   public static List<IAtsTeamDefinition> getTeamTopLevelDefinitions(Active active, IAtsQueryService queryService)  {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinition(queryService);
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   public static IAtsTeamDefinition getTopTeamDefinition(IAtsQueryService queryService)  {
      return queryService.createQuery(AtsArtifactTypes.TeamDefinition).andId(
         AtsArtifactToken.TopTeamDefinition).getOneOrNull(IAtsTeamDefinition.class);
   }

   public static Set<IAtsTeamDefinition> getTeamReleaseableDefinitions(Active active, IAtsQueryService queryService)  {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(active, queryService)) {
         if (teamDef.getVersions().size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem ai)  {
      Set<IAtsTeamDefinition> aiTeams = new HashSet<>();
      getTeamFromItemAndChildren(ai, aiTeams);
      return aiTeams;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsTeamDefinition teamDef)  {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      teamDefs.add(teamDef);
      for (IAtsTeamDefinition child : teamDef.getChildrenTeamDefinitions()) {
         teamDefs.addAll(getTeamsFromItemAndChildren(child));
      }
      return teamDefs;
   }

   private static void getTeamFromItemAndChildren(IAtsActionableItem ai, Set<IAtsTeamDefinition> aiTeams)  {
      aiTeams.add(ai.getTeamDefinition());

      for (IAtsActionableItem childArt : ai.getChildrenActionableItems()) {
         getTeamFromItemAndChildren(childArt, aiTeams);
      }
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitions(Collection<String> teamDefNames, IAtsQueryService queryService)  {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(Active.Both, queryService)) {
         if (teamDefNames.contains(teamDef.getName())) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitionsNameStartsWith(String prefix, IAtsQueryService queryService)  {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(Active.Both, queryService)) {
         if (teamDef.getName().startsWith(prefix)) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais)  {
      Set<IAtsTeamDefinition> resultTeams = new HashSet<>();
      for (IAtsActionableItem ai : ais) {
         resultTeams.addAll(getImpactedTeamDefInherited(ai));
      }
      return resultTeams;
   }

   public static IAtsTeamDefinition getImpactedTeamDef(IAtsActionableItem ai) {
      if (ai.getTeamDefinition() != null) {
         return ai.getTeamDefinition();
      }
      if (ai.getParentActionableItem() != null) {
         return getImpactedTeamDef(ai.getParentActionableItem());
      }
      return null;
   }

   private static Collection<IAtsTeamDefinition> getImpactedTeamDefInherited(IAtsActionableItem ai)  {
      if (ai == null) {
         return java.util.Collections.emptyList();
      }
      if (ai.getTeamDefinition() != null) {
         return java.util.Collections.singleton(ai.getTeamDefinition());
      }
      IAtsActionableItem parentArt = ai.getParentActionableItem();
      return getImpactedTeamDefInherited(parentArt);
   }

}
