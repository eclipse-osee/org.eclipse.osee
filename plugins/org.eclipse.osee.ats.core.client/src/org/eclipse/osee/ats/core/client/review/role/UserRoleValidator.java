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
package org.eclipse.osee.ats.core.client.review.role;

import java.util.logging.Level;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.review.IAtsPeerReviewRoleManager;
import org.eclipse.osee.ats.api.review.IAtsPeerToPeerReview;
import org.eclipse.osee.ats.api.review.Role;
import org.eclipse.osee.ats.api.review.UserRole;
import org.eclipse.osee.ats.api.workdef.IAtsStateDefinition;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewState;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
            IAtsPeerReviewRoleManager roleMgr = ((IAtsPeerToPeerReview) artifact).getRoleManager();
            UserRoleError result = isValid(roleMgr, peerToPeerReviewArtifact.getStateDefinition(),
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

   public static UserRoleError isValid(IAtsPeerReviewRoleManager roleMgr, IAtsStateDefinition fromStateDef, IAtsStateDefinition toStateDef)  {
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
      if (fromStateDef.getName().equals(PeerToPeerReviewState.Review.getName()) || fromStateDef.getName().equals(
         PeerToPeerReviewState.Meeting.getName())) {
         for (UserRole uRole : roleMgr.getUserRoles()) {
            if (uRole.getHoursSpent() == null) {
               return UserRoleError.HoursSpentMustBeEnteredForEachRole;
            }
         }
      }
      return UserRoleError.None;
   }
}
