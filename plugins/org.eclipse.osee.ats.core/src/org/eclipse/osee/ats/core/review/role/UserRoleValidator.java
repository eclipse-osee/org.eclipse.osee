/*
 * Created on May 10, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.role;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class UserRoleValidator {

   public static UserRoleError isValid(Artifact artifact) {
      try {
         if (artifact.isOfType(AtsArtifactTypes.PeerToPeerReview)) {
            if (artifact.getAttributeCount(AtsAttributeTypes.Role) == 0) {
               return UserRoleError.OneRoleEntryRequired;
            }
            UserRoleError result = isUserRoleValid(artifact, UserRoleValidator.class.getSimpleName());
            if (!result.isOK()) {
               return result;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
