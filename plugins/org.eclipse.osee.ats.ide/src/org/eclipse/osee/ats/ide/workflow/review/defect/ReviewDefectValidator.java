/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.review.defect;

import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Disposition;
import org.eclipse.osee.ats.api.review.ReviewDefectItem.Severity;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.workflow.review.PeerToPeerReviewArtifact;
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
