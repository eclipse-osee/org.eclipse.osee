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
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.internal.AtsCoreService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
      for (IAtsTeamDefinition teamDef : teamDefs) {
         guids.add(teamDef.getGuid());
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

   public static List<IAtsTeamDefinition> getTeamDefinitions(Active active) throws OseeCoreException {
      return Collections.castAll(getActive(AtsCoreService.getConfig().get(IAtsTeamDefinition.class), active));
   }

   public static List<IAtsTeamDefinition> getTeamTopLevelDefinitions(Active active) throws OseeCoreException {
      IAtsTeamDefinition topTeamDef = getTopTeamDefinition();
      if (topTeamDef == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(getActive(getChildren(topTeamDef, false), active));
   }

   public static IAtsTeamDefinition getTopTeamDefinition() throws OseeCoreException {
      return AtsCoreService.getConfig().getSoleByGuid(TopTeamDefinitionGuid, IAtsTeamDefinition.class);
   }

   public static Set<IAtsTeamDefinition> getTeamReleaseableDefinitions(Active active) throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : getTeamDefinitions(active)) {
         if (teamDef.getVersions().size() > 0) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsActionableItem ai) throws OseeCoreException {
      Set<IAtsTeamDefinition> aiTeams = new HashSet<IAtsTeamDefinition>();
      getTeamFromItemAndChildren(ai, aiTeams);
      return aiTeams;
   }

   public static Set<IAtsTeamDefinition> getTeamsFromItemAndChildren(IAtsTeamDefinition teamDef) throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      teamDefs.add(teamDef);
      for (IAtsTeamDefinition child : teamDef.getChildrenTeamDefinitions()) {
         teamDefs.addAll(getTeamsFromItemAndChildren(child));
      }
      return teamDefs;
   }

   private static void getTeamFromItemAndChildren(IAtsActionableItem ai, Set<IAtsTeamDefinition> aiTeams) throws OseeCoreException {
      aiTeams.add(ai.getTeamDefinition());

      for (IAtsActionableItem childArt : ai.getChildrenActionableItems()) {
         getTeamFromItemAndChildren(childArt, aiTeams);
      }
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitions(Collection<String> teamDefNames) throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : AtsCoreService.getConfig().get(IAtsTeamDefinition.class)) {
         if (teamDefNames.contains(teamDef.getName())) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Set<IAtsTeamDefinition> getTeamDefinitionsNameStartsWith(String prefix) throws OseeCoreException {
      Set<IAtsTeamDefinition> teamDefs = new HashSet<IAtsTeamDefinition>();
      for (IAtsTeamDefinition teamDef : AtsCoreService.getConfig().get(IAtsTeamDefinition.class)) {
         if (teamDef.getName().startsWith(prefix)) {
            teamDefs.add(teamDef);
         }
      }
      return teamDefs;
   }

   public static Collection<IAtsTeamDefinition> getImpactedTeamDefs(Collection<IAtsActionableItem> ais) throws OseeCoreException {
      Set<IAtsTeamDefinition> resultTeams = new HashSet<IAtsTeamDefinition>();
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

   private static Collection<IAtsTeamDefinition> getImpactedTeamDefInherited(IAtsActionableItem ai) throws OseeCoreException {
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
