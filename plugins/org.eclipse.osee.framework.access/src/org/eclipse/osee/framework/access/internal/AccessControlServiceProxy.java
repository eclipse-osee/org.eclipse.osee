/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicArtifact;
import org.eclipse.osee.framework.core.model.access.AccessDataQuery;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;

public final class AccessControlServiceProxy implements IAccessControlService {

   private IOseeDatabaseService dbService;
   private IOseeCachingService cachingService;
   private IdentityService identityService;

   private AccessControlService accessService;
   private AccessEventListener accessEventListener;

   public void setDbService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setCachingService(IOseeCachingService cachingService) {
      this.cachingService = cachingService;
   }

   public void setIdentityService(IdentityService identityService) {
      this.identityService = identityService;
   }

   public AccessControlService getProxiedObject() {
      return accessService;
   }

   public void clearCache() {
      getProxiedObject().clearCache();
   }

   public void start() {
      accessService = new AccessControlService(dbService, cachingService, identityService);

      accessEventListener = new AccessEventListener(accessService, new AccessControlCacheHandler());
      OseeEventManager.addPriorityListener(accessEventListener);
   }

   public void stop() {
      if (accessEventListener != null) {
         OseeEventManager.removeListener(accessEventListener);
         accessEventListener = null;
      }
      accessService = null;
   }

   private void checkInitialized() throws OseeCoreException {
      Conditions.checkNotNull(accessService, "accessService", "Access Service not properly initialized");
      Conditions.checkNotNull(accessEventListener, "accessEventListener", "Access Service not properly initialized");
   }

   @Override
   public boolean hasPermission(Object object, PermissionEnum permission) throws OseeCoreException {
      checkInitialized();
      return getProxiedObject().hasPermission(object, permission);
   }

   @Override
   public void removePermissions(IOseeBranch branch) throws OseeCoreException {
      checkInitialized();
      getProxiedObject().removePermissions(branch);
   }

   @Override
   public AccessDataQuery getAccessData(IBasicArtifact<?> userArtifact, Collection<?> itemsToCheck) throws OseeCoreException {
      checkInitialized();
      return getProxiedObject().getAccessData(userArtifact, itemsToCheck);
   }
}