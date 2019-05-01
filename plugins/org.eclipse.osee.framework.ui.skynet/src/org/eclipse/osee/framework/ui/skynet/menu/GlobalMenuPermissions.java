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
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class GlobalMenuPermissions {
   private boolean isLocked;
   private boolean accessToRemoveLock;
   private boolean writePermission;
   private boolean readPermission;
   private boolean fullAccess;
   private String subjectFromLockedObjectName;
   private final boolean hasArtifacts;

   /**
    * Return permission set. This is a one-shot deal at the time of instantiation. Permissions are not kept up to date
    * with changes to getSelectedArtifacts in given globalMenuHelper. Must be called again to get refreshed permissions.
    */
   public GlobalMenuPermissions(IGlobalMenuHelper globalMenuHelper) {
      this(globalMenuHelper.getArtifacts());
   }

   /**
    * Return permission set for given artifact.
    */
   public GlobalMenuPermissions(Artifact artifact) {
      this(Arrays.asList(artifact));
   }

   /**
    * Return permission set for given artifacts.
    */
   public GlobalMenuPermissions(Collection<Artifact> artifacts) {
      hasArtifacts = artifacts.size() > 0;
      writePermission = true;
      readPermission = true;
      fullAccess = true;
      isLocked = false;
      accessToRemoveLock = true;
      Artifact combinationSubject = null;

      for (Artifact objectArtifact : artifacts) {
         writePermission = writePermission && AccessControlManager.hasPermission(objectArtifact,
            PermissionEnum.WRITE) && BranchManager.isEditable(objectArtifact.getBranch());
         readPermission = readPermission && AccessControlManager.hasPermission(objectArtifact, PermissionEnum.READ);
         fullAccess = fullAccess && AccessControlManager.hasPermission(objectArtifact, PermissionEnum.FULLACCESS);
         isLocked = isLocked || AccessControlManager.hasLock(objectArtifact);
         accessToRemoveLock =
            accessToRemoveLock && AccessControlManager.canUnlockObject(objectArtifact, UserManager.getUser());

         // acquire the name of the subject that has the lock
         Artifact subject = AccessControlManager.getSubjectFromLockedObject(objectArtifact);

         if (isLocked && subject != null) {
            if (combinationSubject == null) {
               combinationSubject = subject;
               subjectFromLockedObjectName = combinationSubject.getName();
            } else if (combinationSubject.notEqual(subject)) {
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
   public boolean isBranchReadable(BranchId branch) {
      return AccessControlManager.hasPermission(branch, PermissionEnum.READ);
   }

   /**
    * @return the hasArtifacts
    */
   public boolean isHasArtifacts() {
      return hasArtifacts;
   }

}
