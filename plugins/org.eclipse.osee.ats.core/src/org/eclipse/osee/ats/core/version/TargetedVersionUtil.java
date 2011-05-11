/*
 * Created on May 12, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.version;

import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class TargetedVersionUtil {

   public static VersionArtifact getTargetedVersion(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            if (teamArt.getRelatedArtifactsCount(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version) > 0) {
               List<Artifact> verArts =
                  teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
               if (verArts.size() > 1) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     "Multiple targeted versions for artifact " + teamArt.toStringWithId());
                  return (VersionArtifact) verArts.iterator().next();
               } else {
                  return (VersionArtifact) verArts.iterator().next();
               }
            }
         }
      }
      return null;
   }

   public static String getTargetedVersionStr(Object object) throws OseeCoreException {
      if (object instanceof AbstractWorkflowArtifact) {
         TeamWorkFlowArtifact teamArt = ((AbstractWorkflowArtifact) object).getParentTeamWorkflow();
         if (teamArt != null) {
            Collection<Artifact> verArts =
               teamArt.getRelatedArtifacts(AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
            if (verArts.isEmpty()) {
               return "";
            }
            if (verArts.size() > 1) {
               String errStr =
                  "Workflow " + teamArt.getHumanReadableId() + " targeted for multiple versions: " + Artifacts.commaArts(verArts);
               OseeLog.log(Activator.class, Level.SEVERE, errStr, null);
               return "!Error " + errStr;
            }
            Artifact verArt = verArts.iterator().next();
            if (!teamArt.isCompleted() && !teamArt.isCancelled() && verArt.getSoleAttributeValue(
               AtsAttributeTypes.Released, false)) {
               String errStr =
                  "Workflow " + teamArt.getHumanReadableId() + " targeted for released version, but not completed: " + verArt;
               if (!teamArt.isTargetedErrorLogged()) {
                  OseeLog.log(Activator.class, Level.SEVERE, errStr, null);
                  teamArt.setTargetedErrorLogged(true);
               }
               return "!Error " + errStr;
            }
            return verArt.getName();
         }
      }
      return "";
   }

}
