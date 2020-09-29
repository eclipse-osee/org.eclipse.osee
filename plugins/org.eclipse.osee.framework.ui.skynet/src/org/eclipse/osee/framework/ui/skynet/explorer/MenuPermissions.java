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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.skynet.access.internal.OseeApiService;

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
      ArtifactToken combinationSubject = null;

      for (Artifact objectArtifact : artifacts) {
         writePermission =
            writePermission && OseeApiService.get().getAccessControlService().hasArtifactPermission(objectArtifact,
               PermissionEnum.WRITE, null).isSuccess() && BranchManager.isEditable(objectArtifact.getBranch());
         readPermission =
            readPermission && OseeApiService.get().getAccessControlService().hasArtifactPermission(objectArtifact,
               PermissionEnum.READ, null).isSuccess();
         fullAccess = fullAccess && OseeApiService.get().getAccessControlService().hasArtifactPermission(objectArtifact,
            PermissionEnum.FULLACCESS, null).isSuccess();
         isLocked = isLocked || OseeApiService.get().getAccessControlService().hasLock(objectArtifact);
         accessToRemoveLock =
            accessToRemoveLock && OseeApiService.get().getAccessControlService().canUnlockObject(objectArtifact,
               UserManager.getUser());

         // acquire the name of the subject that has the lock
         ArtifactToken subject =
            OseeApiService.get().getAccessControlService().getSubjectFromLockedObject(objectArtifact);

         if (isLocked && subject.isValid()) {
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
      return OseeApiService.get().getAccessControlService().hasBranchPermission(BranchManager.getBranch(branch),
         PermissionEnum.READ, null).isSuccess();
   }

   public boolean isHasArtifacts() {
      return hasArtifacts;
   }

}
