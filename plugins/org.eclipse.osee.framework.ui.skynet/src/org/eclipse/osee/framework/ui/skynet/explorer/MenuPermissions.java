/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.explorer;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Donald G. Dunne
 */
public class MenuPermissions {
   private boolean isLocked;
   private boolean accessToRemoveLock;
   private boolean writePermission;
   private boolean readPermission;
   private boolean fullAccess;
   private String subjectFromLockedObjectName;
   private final boolean hasArtifacts;

   public MenuPermissions(Artifact artifact) {
      this((artifact == null ? Collections.emptyList() : Arrays.asList(artifact)));
   }

   public MenuPermissions(Collection<Artifact> artifacts) {
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

   public boolean isLocked() {
      return isLocked;
   }

   public boolean isAccessToRemoveLock() {
      return accessToRemoveLock;
   }

   public boolean isWritePermission() {
      return writePermission;
   }

   public boolean isReadPermission() {
      return readPermission;
   }

   public boolean isFullAccess() {
      return fullAccess;
   }

   public String getSubjectFromLockedObjectName() {
      return subjectFromLockedObjectName;
   }

   public boolean isBranchReadable(BranchId branch) {
      return AccessControlManager.hasPermission(branch, PermissionEnum.READ);
   }

   public boolean isHasArtifacts() {
      return hasArtifacts;
   }

}
