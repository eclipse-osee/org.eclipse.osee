/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.version;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.version.IAtsVersionStore;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.config.store.TeamDefinitionArtifactStore;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class AtsVersionStoreImpl implements IAtsVersionStore {

   public AtsVersionStoreImpl() {
   }

   @Override
   public IAtsTeamWorkflow setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
      VersionArtifactStore store = new VersionArtifactStore(version);
      Artifact versionArt = store.getArtifact();
      if (versionArt != null) {
         TeamWorkFlowArtifact teamArt = TeamWorkFlowManager.getTeamWorkflowArt(teamWf);
         if (teamArt != null) {
            teamArt.setRelations(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version,
               Collections.singleton(versionArt));
            AtsVersionService.get().setTargetedVersion(teamWf, version);
            return teamArt;
         } else {
            throw new OseeStateException("Team Workflow artifact does not exist [%s]", teamWf.toStringWithId());
         }
      } else {
         throw new OseeStateException("Version artifact does not exist [%s]", version.toStringWithId());
      }
   }

   @Override
   public IAtsVersion getTargetedVersion(Object object) throws OseeCoreException {
      IAtsVersion version = null;
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            if (teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
               List<Artifact> verArts =
                  teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               if (verArts.size() > 1) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     "Multiple targeted versions for artifact " + teamArt.toStringWithId());
                  version = AtsConfigCache.getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
               } else {
                  version = AtsConfigCache.getSoleByGuid(verArts.iterator().next().getGuid(), IAtsVersion.class);
               }
            }
         }
      }
      return version;
   }

   @Override
   public void setTeamDefinition(IAtsVersion version, IAtsTeamDefinition teamDef) throws OseeCoreException {
      VersionArtifactStore verStore = new VersionArtifactStore(version);
      Artifact verArt = verStore.getArtifact();
      if (verArt == null) {
         throw new OseeStateException("Version [%s] does not exist.", version);
      }
      TeamDefinitionArtifactStore teamDefStore = new TeamDefinitionArtifactStore(teamDef);
      Artifact teamDefArt = teamDefStore.getArtifact();
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      if (!verArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition).contains(teamDefArt)) {
         verArt.addRelation(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition, teamDefArt);
      }
   }

   @Override
   public Collection<IAtsVersion> getVersions(IAtsTeamDefinition teamDef) throws OseeCoreException {
      List<IAtsVersion> versions = new ArrayList<IAtsVersion>();
      TeamDefinitionArtifactStore teamDefStore = new TeamDefinitionArtifactStore(teamDef);
      Artifact teamDefArt = teamDefStore.getArtifact();
      if (teamDefArt == null) {
         throw new OseeStateException("Team Definition [%s] does not exist.", teamDef);
      }
      for (Artifact verArt : teamDefArt.getRelatedArtifacts(AtsRelationTypes.TeamDefinitionToVersion_Version)) {
         IAtsVersion version = AtsConfigCache.getSoleByGuid(verArt.getGuid(), IAtsVersion.class);
         versions.add(version);
      }
      return versions;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsVersion version) throws OseeCoreException {
      IAtsTeamDefinition result = null;
      VersionArtifactStore store = new VersionArtifactStore(version);
      Artifact verArt = store.getArtifact();
      if (verArt != null) {
         Artifact teamDefArt = null;
         try {
            teamDefArt = verArt.getRelatedArtifact(AtsRelationTypes.TeamDefinitionToVersion_TeamDefinition);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
         if (teamDefArt != null) {
            result = AtsConfigCache.getSoleByGuid(teamDefArt.getGuid(), IAtsTeamDefinition.class);
         }
      }
      return result;
   }
}
