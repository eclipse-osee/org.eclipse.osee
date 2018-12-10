/*******************************************************************************
 * Copyright (c) 2017 Boeing.
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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class TeamDefinitionServiceImpl implements IAtsTeamDefinitionService {

   private final AtsApi atsApi;

   public TeamDefinitionServiceImpl(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinitionById(ArtifactId teamDefId) {
      IAtsTeamDefinition teamDef = null;
      if (teamDefId instanceof IAtsTeamDefinition) {
         teamDef = (IAtsTeamDefinition) teamDefId;
      } else {
         ArtifactToken art = atsApi.getQueryService().getArtifact(teamDefId);
         if (atsApi.getStoreService().isOfType(art, AtsArtifactTypes.TeamDefinition)) {
            teamDef = new TeamDefinition(atsApi.getLogger(), atsApi, art);
         }
      }
      return teamDef;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleArtifactIdReference(workItem,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      if (teamDefId.isValid()) {
         teamDef = atsApi.getQueryService().getConfigItem(teamDefId);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) {
      List<IAtsVersion> versions = new ArrayList<>();
      for (ArtifactId verArt : atsApi.getRelationResolver().getRelated(teamDef,
         AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         versions.add(atsApi.getVersionService().getVersion(verArt));
      }
      return versions;
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsTeamDefinition teamDef) {
      return teamDef.getTeamDefinitionHoldingVersions();
   }

   @Override
   public IAtsTeamDefinition getTeamDefHoldingVersions(IAtsProgram program) {
      return atsApi.getProgramService().getTeamDefHoldingVersions(program);
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(String name) {
      IAtsTeamDefinition teamDef = null;
      ArtifactId teamDefArt =
         atsApi.getQueryService().getArtifactByNameOrSentinel(AtsArtifactTypes.TeamDefinition, name);
      if (teamDefArt.isValid()) {
         teamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(teamDefArt);
      }
      return teamDef;
   }

   @Override
   public Collection<IAtsTeamDefinition> getTeamDefinitions(IAgileTeam agileTeam) {
      List<IAtsTeamDefinition> teamDefs = new LinkedList<>();
      for (ArtifactId atsTeamArt : atsApi.getRelationResolver().getRelated(agileTeam,
         AtsRelationTypes.AgileTeamToAtsTeam_AtsTeam)) {
         teamDefs.add(atsApi.getTeamDefinitionService().getTeamDefinitionById(atsTeamArt));
      }
      return teamDefs;
   }

}
