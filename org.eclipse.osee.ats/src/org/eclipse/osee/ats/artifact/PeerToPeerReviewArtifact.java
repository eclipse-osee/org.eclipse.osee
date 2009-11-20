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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.widgets.defect.DefectManager;
import org.eclipse.osee.ats.util.widgets.role.UserRole;
import org.eclipse.osee.ats.util.widgets.role.UserRole.Role;
import org.eclipse.osee.ats.world.IWorldViewArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactFactory;
import org.eclipse.osee.framework.skynet.core.artifact.IATSStateMachineArtifact;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Donald G. Dunne
 */
public class PeerToPeerReviewArtifact extends ReviewSMArtifact implements IReviewArtifact, IWorldViewArtifact, IATSStateMachineArtifact {

   public static String ARTIFACT_NAME = "PeerToPeer Review";
   public static enum PeerToPeerReviewState {
      Prepare, Review, Completed
   };

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws OseeDataStoreException
    */
   public PeerToPeerReviewArtifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      super(parentFactory, guid, humanReadableId, branch, artifactType);
      defectManager = new DefectManager(this);
   }

   public static String getDefaultReviewTitle(SMAManager smaMgr) {
      try {
         return "Review \"" + smaMgr.getSma().getArtifactTypeName() + "\" titled \"" + smaMgr.getSma().getName() + "\"";
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }
      return "Review";
   }

   @Override
   public IStatus isUserRoleValid(String namespace) throws OseeCoreException {
      if (getUserRoleManager().getUserRoles(Role.Author).size() <= 0) return new Status(IStatus.ERROR, namespace,
            "Must have at least one Author");
      if (getUserRoleManager().getUserRoles(Role.Reviewer).size() <= 0) return new Status(IStatus.ERROR, namespace,
            "Must have at least one Reviewer");
      // If in review state, all roles must have hours spent entered
      if (smaMgr.getStateMgr().getCurrentStateName().equals(
            PeerToPeerReviewArtifact.PeerToPeerReviewState.Review.name())) {
         for (UserRole uRole : userRoleManager.getUserRoles()) {
            if (uRole.getHoursSpent() == null) new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID,
                  "Hours spent must be entered for each role.");
         }
      }
      return super.isUserRoleValid(namespace);
   }

   @Override
   public String getHelpContext() {
      return "peerToPeerReview";
   }

   @Override
   public Set<User> getPrivilegedUsers() throws OseeCoreException {
      Set<User> users = new HashSet<User>();
      if (getParentTeamWorkflow() != null)
         users.addAll(getParentTeamWorkflow().getPrivilegedUsers());
      else {
         if (AtsUtil.isAtsAdmin()) {
            users.add(UserManager.getUser());
         }
      }
      return users;
   }

   @Override
   public String getHyperName() {
      return getName();
   }

   @Override
   public String getWorldViewDescription() throws OseeCoreException {
      return getSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), "");
   }

   @Override
   public VersionArtifact getWorldViewTargetedVersion() throws OseeCoreException {
      if (getParentSMA() == null) return null;
      return getParentSMA().getWorldViewTargetedVersion();
   }

   @Override
   public Collection<User> getImplementers() throws OseeCoreException {
      Collection<User> users = getImplementersByState(PeerToPeerReviewState.Review.name());
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
