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

import java.util.Map;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class AccessControlServiceRegHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class[] {IOseeDatabaseService.class, IOseeCachingService.class};

   private AccessControlService accessService;
   private ServiceRegistration serviceRegistration;;
   private AccessEventListener accessEventListener;

   public AccessControlService getService() {
      return accessService;
   }

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IOseeDatabaseService databaseService = getService(IOseeDatabaseService.class, services);
      IOseeCachingService cachingService = getService(IOseeCachingService.class, services);

      //		IAccessCacheAccessor accessor = new DatabaseAccessCacheAccessor(databaseService, cachingService);
      //		ObjectBasedAccessCache cache = new ObjectBasedAccessCache(accessor);

      accessService = new AccessControlService(databaseService, cachingService);
      serviceRegistration = context.registerService(IAccessControlService.class.getName(), accessService, null);

      accessEventListener = new AccessEventListener(accessService);
      OseeEventManager.addPriorityListener(accessEventListener);
   }

   @Override
   public void onDeActivate() {
      if (accessEventListener != null) {
         OseeEventManager.removeListener(accessEventListener);
      }
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }
}