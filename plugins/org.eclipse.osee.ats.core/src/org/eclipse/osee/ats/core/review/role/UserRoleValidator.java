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
package org.eclipse.osee.ats.core.review.role;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewState;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
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
            PeerToPeerReviewArtifact peerToPeerReviewArtifact = (PeerToPeerReviewArtifact) artifact;
            UserRoleManager roleMgr = new UserRoleManager(peerToPeerReviewArtifact);
            UserRoleError result =
               isValid(roleMgr, peerToPeerReviewArtifact.getStateDefinition(),
                  peerToPeerReviewArtifact.getStateDefinition().getDefaultToState());
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

   public static UserRoleError isValid(UserRoleManager roleMgr, StateDefinition fromStateDef, StateDefinition toStateDef) throws OseeCoreException {
      if (roleMgr.getUserRoles().isEmpty()) {
         return UserRoleError.OneRoleEntryRequired;
      }
      if (roleMgr.getUserRoles(Role.Author).size() <= 0) {
         return UserRoleError.MustHaveAtLeastOneAuthor;
      }
      if (roleMgr.getUserRoles(Role.Reviewer).size() <= 0) {
         return UserRoleError.MustHaveAtLeastOneReviewer;
      }
      // If in review state, all roles must have hours spent entered
      if ((fromStateDef.getPageName().equals(PeerToPeerReviewState.Review.getPageName())) || (fromStateDef.getPageName().equals(PeerToPeerReviewState.Meeting.getPageName()))) {
         for (UserRole uRole : roleMgr.getUserRoles()) {
            if (uRole.getHoursSpent() == null) {
               return UserRoleError.HoursSpentMustBeEnteredForEachRole;
            }
         }
      }
      return UserRoleError.None;
   }
}
