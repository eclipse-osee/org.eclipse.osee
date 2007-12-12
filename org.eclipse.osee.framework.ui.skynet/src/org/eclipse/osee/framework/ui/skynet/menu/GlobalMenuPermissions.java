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
package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;

/**
 * @author Donald G. Dunne
 */
public class GlobalMenuPermissions {

   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final AccessControlManager accessManager = AccessControlManager.getInstance();

   private boolean isLocked;
   private boolean accessToRemoveLock;
   private boolean writePermission;
   private boolean readPermission;
   private boolean fullAccess;
   private boolean defaultBranchReadable;
   private String subjectFromLockedObjectName;
   private boolean hasArtifacts;

   /**
    * Return permission set. This is a one-shot deal at the time of instantiation. Permissions are not kept up to date
    * with changes to getSelectedArtifacts in given globalMenuHelper. Must be called again to get refreshed permissions.
    * 
    * @param globalMenuHelper
    */
   public GlobalMenuPermissions(IGlobalMenuHelper globalMenuHelper) {
      this(globalMenuHelper.getArtifacts());
   }

   /**
    * Return permission set for given artifact.
    * 
    * @param artifact
    */
   public GlobalMenuPermissions(Artifact artifact) {
      this(Arrays.asList(new Artifact[] {artifact}));
   }

   /**
    * Return permission set for given artifacts.
    * 
    * @param artifacts
    */
   public GlobalMenuPermissions(Collection<Artifact> artifacts) {
      hasArtifacts = artifacts.size() > 0;
      writePermission = true;
      readPermission = true;
      fullAccess = true;
      isLocked = false;
      defaultBranchReadable = true;
      accessToRemoveLock = true;
      Artifact combinationSubject = null;

      for (Artifact objectArtifact : artifacts) {

         writePermission &=
               accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), objectArtifact,
                     PermissionEnum.WRITE);
         readPermission &=
               accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), objectArtifact,
                     PermissionEnum.READ);
         fullAccess &=
               accessManager.checkObjectPermission(skynetAuth.getAuthenticatedUser(), objectArtifact,
                     PermissionEnum.FULLACCESS);
         isLocked |= accessManager.hasLock(objectArtifact);
         defaultBranchReadable =
               accessManager.checkObjectPermission(branchManager.getDefaultBranch(), PermissionEnum.READ);
         accessToRemoveLock &= accessManager.canUnlockObject(objectArtifact, skynetAuth.getAuthenticatedUser());

         // acquire the name of the subject that has the lock
         Artifact subject = accessManager.getSubjectFromLockedObject(objectArtifact);

         if (isLocked && subject != null) {
            if (combinationSubject == null) {
               combinationSubject = subject;
               subjectFromLockedObjectName = combinationSubject.getDescriptiveName();
            } else if (!combinationSubject.equals(subject)) {
               subjectFromLockedObjectName = "Mixed names";
            }
         }
      }
   }

   /**
    * @return the isLocked
    */
   public boolean isLocked() {
      return isLocked;
   }

   /**
    * @return the accessToRemoveLock
    */
   public boolean isAccessToRemoveLock() {
      return accessToRemoveLock;
   }

   /**
    * @return the writePermission
    */
   public boolean isWritePermission() {
      return writePermission;
   }

   /**
    * @return the readPermission
    */
   public boolean isReadPermission() {
      return readPermission;
   }

   /**
    * @return the fullAccess
    */
   public boolean isFullAccess() {
      return fullAccess;
   }

   /**
    * @return the subjectFromLockedObjectName
    */
   public String getSubjectFromLockedObjectName() {
      return subjectFromLockedObjectName;
   }

   /**
    * @return the branchReadable
    */
   public boolean isDefaultBranchReadable() {
      return defaultBranchReadable;
   }

   /**
    * @return the hasArtifacts
    */
   public boolean isHasArtifacts() {
      return hasArtifacts;
   }

}
