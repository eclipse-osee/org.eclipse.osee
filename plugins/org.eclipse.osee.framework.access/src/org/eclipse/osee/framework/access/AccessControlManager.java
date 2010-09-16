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
import org.eclipse.osee.framework.access.internal.AccessControlService;
import org.eclipse.osee.framework.access.internal.Activator;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationRequiredException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Provides access control for OSEE. <REM2>
 * 
 * @author Jeff C. Phillips
 */

public final class AccessControlManager {

   private AccessControlManager() {
      // Hide constructor
   }

   public static AccessControlService getService() {
      AccessControlService accessService = Activator.getAccessControlService();
      if (accessService == null) {
         OseeLog.log(Activator.class, Level.SEVERE, "Access Service was null");
      }
      return accessService;
   }

   public static void lockObjects(Collection<Artifact> objects, Artifact subject) {
      getService().lockObjects(objects, subject);
   }

   public static void unLockObjects(Collection<Artifact> objects, Artifact subject) throws OseeCoreException, OseeAuthenticationRequiredException {
      getService().unLockObjects(objects, subject);
   }

   public static boolean hasLock(Artifact object) {
      return getService().hasLock(object);
   }

   public static boolean canUnlockObject(Artifact object, Artifact subject) {
      return getService().canUnlockObject(object, subject);
   }

   public static Artifact getSubjectFromLockedObject(Object object) throws OseeCoreException {
      return getService().getSubjectFromLockedObject(object);
   }

   public static boolean hasLockAccess(Artifact object) {
      return getService().hasLockAccess(object);
   }

   public static boolean isOseeAdmin() throws OseeCoreException {
      return getService().isOseeAdmin();
   }

   public static Collection<AccessControlData> getAccessControlList(Object object) {
      return getService().getAccessControlList(object);
   }

   public static AccessObject getAccessObject(Object object) {
      return getService().getAccessObject(object);
   }

   public static void persistPermission(AccessControlData data, boolean isRecursionAllowed) {
      getService().persistPermission(data, isRecursionAllowed);
   }

   public static void removeAccessControlDataIf(boolean removeFromDb, AccessControlData data) throws OseeCoreException {
      getService().removeAccessControlDataIf(removeFromDb, data);
   }

   public static boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException {
      return getService().hasPermission(object, permission);
   }

   public static void setPermission(Artifact subject, Object object, PermissionEnum permission) {
      getService().setPermission(subject, object, permission);
   }

   public static AccessDataQuery getAccessData(Collection<?> objectsToCheck) throws OseeCoreException {
      return getService().getAccessData(UserManager.getUser(), objectsToCheck);
   }
}
