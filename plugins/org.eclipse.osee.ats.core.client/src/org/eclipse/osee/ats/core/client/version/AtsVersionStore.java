/*
 * Created on Jul 16, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.version;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.config.store.VersionArtifactStore;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.AtsVersionService;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class AtsVersionStore {

   public static TeamWorkFlowArtifact setTargetedVersionLink(IAtsTeamWorkflow teamWf, IAtsVersion version) throws OseeCoreException {
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

   public static IAtsVersion getTargetedVersion(Object object) throws OseeCoreException {
      IAtsVersion version = NullVersion.instance;
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
}
