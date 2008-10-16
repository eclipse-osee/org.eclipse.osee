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

package org.eclipse.osee.ats.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.world.search.LegacyPCRActionsWorldSearchItem;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class LegacyPCRActions {

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(TeamDefinitionArtifact teamDef) throws OseeCoreException {
      return getTeamsTeamWorkflowArtifacts(Arrays.asList(teamDef));
   }

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      LegacyPCRActionsWorldSearchItem search = new LegacyPCRActionsWorldSearchItem(teamDefs, false);
      Set<TeamWorkFlowArtifact> teamArts = new HashSet<TeamWorkFlowArtifact>();
      for (Artifact art : search.performSearchGetResults()) {
         if (art instanceof TeamWorkFlowArtifact) {
            teamArts.add((TeamWorkFlowArtifact) art);
         }
      }
      return teamArts;
   }

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(String pcrId, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      return getTeamsTeamWorkflowArtifacts(Arrays.asList(pcrId), teamDefs);
   }

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(String pcrId) throws OseeCoreException {
      return getTeamsTeamWorkflowArtifacts(Arrays.asList(pcrId), (Collection<TeamDefinitionArtifact>) null);
   }

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(Collection<String> pcrIds, TeamDefinitionArtifact teamDef) throws OseeCoreException {
      return getTeamsTeamWorkflowArtifacts(pcrIds, teamDef != null ? Arrays.asList(teamDef) : null);
   }

   public static Collection<TeamWorkFlowArtifact> getTeamsTeamWorkflowArtifacts(Collection<String> pcrIds, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      LegacyPCRActionsWorldSearchItem search = new LegacyPCRActionsWorldSearchItem(pcrIds, teamDefs, false);
      Set<TeamWorkFlowArtifact> teamArts = new HashSet<TeamWorkFlowArtifact>();
      for (Artifact art : search.performSearchGetResults()) {
         if (art instanceof TeamWorkFlowArtifact) {
            teamArts.add((TeamWorkFlowArtifact) art);
         }
      }
      return teamArts;
   }

   public static Collection<ActionArtifact> getTeamsActionArtifacts(TeamDefinitionArtifact teamDef) throws OseeCoreException {
      return getTeamsActionArtifacts(Arrays.asList(teamDef));
   }

   public static Collection<ActionArtifact> getTeamsActionArtifacts(Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      LegacyPCRActionsWorldSearchItem search = new LegacyPCRActionsWorldSearchItem(teamDefs, true);
      Set<ActionArtifact> actArts = new HashSet<ActionArtifact>();
      for (Artifact art : search.performSearchGetResults()) {
         if (art instanceof ActionArtifact) {
            actArts.add((ActionArtifact) art);
         }
      }
      return actArts;
   }

   public static Collection<ActionArtifact> getTeamsActionArtifacts(String pcrId, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      return getTeamsActionArtifacts(Arrays.asList(pcrId), teamDefs);
   }

   public static Collection<ActionArtifact> getTeamsActionArtifacts(Collection<String> pcrIds, Collection<TeamDefinitionArtifact> teamDefs) throws OseeCoreException {
      LegacyPCRActionsWorldSearchItem search = new LegacyPCRActionsWorldSearchItem(pcrIds, teamDefs, true);
      Set<ActionArtifact> actArts = new HashSet<ActionArtifact>();
      for (Artifact art : search.performSearchGetResults()) {
         actArts.add((ActionArtifact) art);
      }
      return actArts;
   }

}
