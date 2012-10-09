/*
 * Created on Aug 3, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.config;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.core.client.util.WorkItemUtil;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class AtsTeamDefinitionServiceImpl implements IAtsTeamDefinitionService {

   @Override
   public IAtsTeamDefinition getTeamDefinition(IAtsWorkItem workItem) throws OseeCoreException {
      Artifact art = WorkItemUtil.get(workItem);
      if (art != null) {
         if (art instanceof AbstractWorkflowArtifact) {
            AbstractWorkflowArtifact awa = (AbstractWorkflowArtifact) art;
            return awa.getParentTeamWorkflow().getTeamDefinition();
         }
      }
      return null;
   }

}
