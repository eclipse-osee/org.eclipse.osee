/*
 * Created on May 18, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.review.defect;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.core.review.defect.ReviewDefectItem.Severity;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ReviewDefectValidator {

   public static ReviewDefectError isValid(Artifact artifact) {
      try {
         if (artifact instanceof PeerToPeerReviewArtifact) {
            PeerToPeerReviewArtifact peerToPeerReviewArtifact = (PeerToPeerReviewArtifact) artifact;
            ReviewDefectError result = isValid(ReviewDefectManager.getDefectItems(peerToPeerReviewArtifact));
            if (!result.isOK()) {
               return result;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return ReviewDefectError.ExceptionValidatingRoles;
      }
      return ReviewDefectError.None;
   }

   public static ReviewDefectError isValid(Set<ReviewDefectItem> defectItems) {
      try {
         for (ReviewDefectItem item : defectItems) {
            if (item.isClosed() == false || item.getDisposition() == Disposition.None || item.getSeverity() == Severity.None && item.getDisposition() != Disposition.Duplicate && item.getDisposition() != Disposition.Reject) {
               return ReviewDefectError.AllItemsMustBeMarkedAndClosed;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         return ReviewDefectError.ExceptionValidatingRoles;
      }
      return ReviewDefectError.None;
   }

}
