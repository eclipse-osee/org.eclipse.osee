/*
 * Created on May 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validate;

import java.util.logging.Level;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewState;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class UserRoleValidator {

   public static UserRoleError isValid(Artifact artifact) {
      try {
         if (artifact instanceof PeerToPeerReviewArtifact) {
            if (artifact.getAttributeCount(AtsAttributeTypes.Role) == 0) {
               return UserRoleError.OneRoleEntryRequired;
            }
            UserRoleError result = isUserRoleValid(artifact, UserRoleValidator.class.getSimpleName());
            if (!result.isOK()) {
               return result;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return UserRoleError.ExceptionValidatingRoles;
      }
      return UserRoleError.None;
   }

   private static UserRoleError isUserRoleValid(Artifact artifact, String namespace) throws OseeCoreException {
      if (artifact instanceof PeerToPeerReviewArtifact) {
         PeerToPeerReviewArtifact peerToPeerReviewArtifact = (PeerToPeerReviewArtifact) artifact;

         if (peerToPeerReviewArtifact.getUserRoleManager().getUserRoles(Role.Author).size() <= 0) {
            return UserRoleError.MustHaveAtLeastOneAuthor;
         }
         if (peerToPeerReviewArtifact.getUserRoleManager().getUserRoles(Role.Reviewer).size() <= 0) {
            return UserRoleError.MustHaveAtLeastOneReviewer;
         }
         // If in review state, all roles must have hours spent entered
         if (peerToPeerReviewArtifact.isInState(PeerToPeerReviewState.Review) || peerToPeerReviewArtifact.isInState(PeerToPeerReviewState.Meeting)) {
            for (UserRole uRole : peerToPeerReviewArtifact.getUserRoleManager().getUserRoles()) {
               if (uRole.getHoursSpent() == null) {
                  return UserRoleError.HoursSpentMustBeEnteredForEachRole;
               }
            }
         }
      }
      return UserRoleError.None;
   }

}
