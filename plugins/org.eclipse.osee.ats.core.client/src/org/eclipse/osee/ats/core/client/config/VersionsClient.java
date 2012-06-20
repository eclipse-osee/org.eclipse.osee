/*
 * Created on Jun 6, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config;

import java.util.Collection;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class VersionsClient {

   public static Collection<TeamWorkFlowArtifact> getTargetedForTeamWorkflows(IAtsVersion verArt) throws OseeCoreException {
      Artifact artifact = AtsObjectsClient.getSoleArtifact(verArt);
      return artifact.getRelatedArtifactsOfType(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow,
         TeamWorkFlowArtifact.class);
   }

}
