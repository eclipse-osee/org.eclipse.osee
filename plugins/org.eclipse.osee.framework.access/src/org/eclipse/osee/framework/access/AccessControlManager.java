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

package org.eclipse.osee.framework.access;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.internal.AccessControlHelper;
import org.eclipse.osee.framework.access.internal.AccessControlService;
import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */

public final class AccessControlManager {

   private AccessControlManager() {
      // Hide constructor
   }

   @SuppressWarnings("deprecation")
   public static AccessControlService getService() {
      AccessControlService accessService = null;
      try {
         accessService = AccessControlHelper.getAccessControlService();
      } catch (OseeCoreException ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, "Acquiring Access Control Service", ex);
      }
      if (accessService == null) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, "Access Service was null");
      }
      return accessService;
   }

   public static void clearCaches() {
      getService().clearCache();
   }

   public static void lockObjects(Collection<Artifact> objects, Artifact subject) {
      getService().lockObjects(objects, subject);
   }

   public static void unLockObjects(Collection<Artifact> objects, Artifact subject) {
      getService().unLockObjects(objects, subject);
   }

   public static boolean hasLock(Artifact object) {
      return getService().hasLock(object);
   }

   public static boolean canUnlockObject(Artifact object, Artifact subject) {
      return getService().canUnlockObject(object, subject);
   }

   public static Artifact getSubjectFromLockedObject(Object object) {
      return getService().getSubjectFromLockedObject(object);
   }

   public static boolean hasLockAccess(Artifact object) {
      return getService().hasLockAccess(object);
   }

   public static boolean isOseeAdmin() {
      return UserGroupService.getOseeAdmin().isCurrentUserMember();
   }

   public static Collection<AccessControlData> getAccessControlList(Object object) {
      return getService().getAccessControlList(object);
   }

   /**
    * @return AccessControlData for this user for this object if one exists; otherwise null
    */
   public static AccessControlData getAccessControlForLock(Object object) {
      AccessControlData data = null;
      if (object instanceof Artifact && hasLock((Artifact) object)) {
         ArtifactAccessObject artifactAccessObject = ArtifactAccessObject.getArtifactAccessObject((Artifact) object);
         Artifact subject = getSubjectFromLockedObject(object);
         data = new AccessControlData(subject, artifactAccessObject, PermissionEnum.LOCK, false, false);
         data.setBranchPermission(getService().getBranchPermission(subject, ((Artifact) object).getBranch()));
         data.setArtifactPermission(PermissionEnum.LOCK);
      }
      return data;

   }

   public static AccessObject getAccessObject(Object object) {
      return getService().getAccessObject(object);
   }

   public static void persistPermission(AccessControlData data, boolean isRecursionAllowed) {
      getService().persistPermission(data, isRecursionAllowed);
   }

   public static void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) {
      getService().removeAccessControlDataIf(removeFromDb, data);
   }

   public static boolean hasPermission(Object object, PermissionEnum permission) {
      return getService().hasPermission(object, permission);
   }

   public static void setPermission(Artifact subject, Object object, PermissionEnum permission) {
      getService().setPermission(subject, object, permission);
   }

   public static PermissionEnum getPermission(Object object) {
      return getPermission(UserManager.getUser(), object);
   }

   public static PermissionEnum getPermission(User user, Object object) {
      if (object instanceof BranchId) {
         return getService().getBranchPermission(user, (BranchId) object);
      } else if (object instanceof Artifact) {
         return getService().getArtifactPermission(user, (Artifact) object);
      } else {
         return PermissionEnum.DENY;
      }
   }

   public static AccessDataQuery getAccessData(Collection<?> objectsToCheck) {
      return getService().getAccessData(UserManager.getUser(), objectsToCheck);
   }

}
