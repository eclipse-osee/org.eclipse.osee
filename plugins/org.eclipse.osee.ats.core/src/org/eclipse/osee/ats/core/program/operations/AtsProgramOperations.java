/*********************************************************************
 * Copyright (c) 2018 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.core.program.operations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Donald G. Dunne
 */
public class AtsProgramOperations {

   private final AtsApi atsApi;

   public AtsProgramOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public List<ProgramVersions> getProgramVersionsList(ArtifactTypeToken programArtifactType, boolean onlyActive) {

      Collection<ArtifactToken> programsArts =
         atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), true, programArtifactType);
      List<ProgramVersions> progVers = new LinkedList<>();
      for (ArtifactToken program : programsArts) {

         boolean active = atsApi.getAttributeResolver().getSoleAttributeValue(program, AtsAttributeTypes.Active, true);
         if (onlyActive && !active) {
            continue;
         }
         ProgramVersions progVersions = getProgramVersions(program, onlyActive);
         progVers.add(progVersions);
      }
      return progVers;
   }

   public ArtifactToken getProgramFromVersion(ArtifactId version) {
      ArtifactId teamDef = atsApi.getRelationResolver().getRelatedOrSentinel(version,
         AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
      if (teamDef.isValid()) {
         ArtifactId program = atsApi.getAttributeResolver().getSoleAttributeValue(teamDef, AtsAttributeTypes.ProgramId,
            ArtifactId.SENTINEL);
         String name = atsApi.getAttributeResolver().getSoleAttributeValue(program, CoreAttributeTypes.Name, "");
         return ArtifactToken.valueOf(program, name);
      }
      return null;
   }

   public ProgramVersions getProgramVersions(ArtifactToken program, boolean onlyActive) {
      ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleAttributeValue(program,
         AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
      ProgramVersions progVersions = new ProgramVersions();
      progVersions.setProgram(program);
      if (teamDefId.isValid()) {
         TeamDefinition teamDef = atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());

         if (teamDef != null) {
            progVersions.setTeam(ArtifactToken.valueOf(teamDef.getId(), teamDef.getName(), atsApi.getAtsBranch()));

            for (Long versionId : teamDef.getVersions()) {
               IAtsVersion version = atsApi.getConfigService().getConfigurations().getIdToVersion().get(versionId);

               boolean verActive = version.isActive();
               if (onlyActive && !verActive) {
                  continue;
               }

               progVersions.addVersion(
                  ArtifactToken.valueOf(version.getId(), version.getName(), atsApi.getAtsBranch()));
            }
         }
      }
      return progVersions;
   }
}
