/*
 * Created on Apr 16, 2018
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.program.operations;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.JaxTeamDefinition;
import org.eclipse.osee.ats.api.config.JaxVersion;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.program.ProgramVersions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;

public class AtsProgramOperations {

   private final AtsApi atsApi;

   public AtsProgramOperations(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   public List<ProgramVersions> getProgramVersions(IArtifactType programArtifactType, boolean onlyActive) {

      Collection<ArtifactToken> programsArts =
         atsApi.getQueryService().getArtifacts(atsApi.getAtsBranch(), true, programArtifactType);
      List<ProgramVersions> progVers = new LinkedList<>();
      for (ArtifactToken program : programsArts) {

         boolean active = atsApi.getAttributeResolver().getSoleAttributeValue(program, AtsAttributeTypes.Active, true);
         if (onlyActive && !active) {
            continue;
         }
         ProgramVersions progVer = new ProgramVersions();
         progVer.setProgram(program);
         progVers.add(progVer);

         ArtifactId teamDefId = atsApi.getAttributeResolver().getSoleAttributeValue(program,
            AtsAttributeTypes.TeamDefinitionReference, ArtifactId.SENTINEL);
         if (teamDefId.isValid()) {
            JaxTeamDefinition jaxTeamDef =
               atsApi.getConfigService().getConfigurations().getIdToTeamDef().get(teamDefId.getId());

            if (jaxTeamDef != null) {
               progVer.setTeam(ArtifactToken.valueOf(jaxTeamDef.getId(), jaxTeamDef.getName(), atsApi.getAtsBranch()));

               for (Long versionId : jaxTeamDef.getVersions()) {
                  JaxVersion version = atsApi.getConfigService().getConfigurations().getIdToVersion().get(versionId);

                  boolean verActive = version.isActive();
                  if (onlyActive && !verActive) {
                     continue;
                  }

                  progVer.addVersion(ArtifactToken.valueOf(version.getId(), version.getName(), atsApi.getAtsBranch()));
               }
            }
         }

      }
      return progVers;
   }

}
