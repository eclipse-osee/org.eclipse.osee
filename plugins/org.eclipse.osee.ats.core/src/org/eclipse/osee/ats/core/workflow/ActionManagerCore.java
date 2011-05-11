/*
 * Created on May 11, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import java.util.Collection;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ActionManagerCore {

   public static Collection<TeamWorkFlowArtifact> getTeams(Object object) throws OseeCoreException {
      if (object instanceof Artifact && ((Artifact) object).isOfType(AtsArtifactTypes.Action)) {
         return ((Artifact) object).getRelatedArtifacts(AtsRelationTypes.ActionToWorkflow_WorkFlow,
            TeamWorkFlowArtifact.class);
      }
      return java.util.Collections.emptyList();
   }

   public static TeamWorkFlowArtifact getFirstTeam(Object object) throws OseeCoreException {
      Collection<TeamWorkFlowArtifact> arts = getTeams(object);
      if (arts.size() > 0) {
         return arts.iterator().next();
      }
      return null;
   }

}
