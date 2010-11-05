/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.artifact;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.StateManager;
import org.eclipse.osee.ats.util.widgets.defect.DefectManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewArtifact extends AbstractReviewArtifact implements IReviewArtifact, IATSStateMachineArtifact {

   public static enum PeerToPeerReviewState {
      Prepare,
      Review,
      Completed
   };

   public PeerToPeerReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, IArtifactType artifactType) throws OseeCoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      defectManager = new DefectManager(this);
   }

   public static String getDefaultReviewTitle(TeamWorkFlowArtifact teamArt) {
      return "Review \"" + teamArt.getArtifactTypeName() + "\" titled \"" + teamArt.getName() + "\"";
   }

   @Override
   public IStatus isUserRoleValid(String namespace) throws OseeCoreException {
      if (getUserRoleManager().getUserRoles(Role.Author).size() <= 0) {
         return new Status(IStatus.ERROR, namespace, "Must have at least one Author");
      }
      if (getUserRoleManager().getUserRoles(Role.Reviewer).size() <= 0) {
         return new Status(IStatus.ERROR, namespace, "Must have at least one Reviewer");
      }
      // If in review state, all roles must have hours spent entered
      if (getStateMgr().getCurrentStateName().equals(PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name())) {
         for (UserRole uRole : userRoleManager.getUserRoles()) {
            if (uRole.getHoursSpent() == null) {
               return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Hours spent must be entered for each role.");
            }
         }
      }
      return super.isUserRoleValid(namespace);
   }

   @Override
   public String getHelpContext() {
      return "peerToPeerReview";
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return getSoleAttributeValue(AtsAttributeTypes.Description, "");
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      Collection<User> users = StateManager.getImplementersByState(this, PeerToPeerReviewState.Review.name());
      for (UserRole role : userRoleManager.getUserRoles()) {
         users.add(role.getUser());
      }
      return users;
   }

   @Override
   public String getWorldViewReviewAuthor() throws OseeCoreException {
      return Artifacts.toString("; ", getUserRoleManager().getRoleUsers(Role.Author));
   }

   @Override
   public String getWorldViewReviewModerator() throws OseeCoreException {
      return Artifacts.toString("; ", getUserRoleManager().getRoleUsers(Role.Moderator));
   }

   @Override
   public String getWorldViewReviewReviewer() throws OseeCoreException {
      return Artifacts.toString("; ", getUserRoleManager().getRoleUsers(Role.Reviewer));
   }

   @Override
   public String getWorldViewNumberOfReviewIssueDefects() throws OseeCoreException {
      return String.valueOf(getDefectManager().getNumIssues());
   }

   @Override
   public String getWorldViewNumberOfReviewMajorDefects() throws OseeCoreException {
      return String.valueOf(getDefectManager().getNumMajor());
   }

   @Override
   public String getWorldViewNumberOfReviewMinorDefects() throws OseeCoreException {
      return String.valueOf(getDefectManager().getNumMinor());
   }

}
