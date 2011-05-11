/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow;

import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class EstimatedHoursUtil {

   public static double getEstimatedHours(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         return ((AbstractWorkflowArtifact) object).getEstimatedHoursTotal();
      } else if (Artifacts.isOfType(object, AtsArtifactTypes.Action)) {
         double total = 0;
         for (Artifact team : ActionManagerCore.getTeams(object)) {
            total += getEstimatedHours(team);
         }
         return total;
      }
      return 0.0;
   }

}
