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
package org.eclipse.osee.framework.access.internal.cm;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.access.internal.AccessControlHelper;
import org.eclipse.osee.framework.access.internal.AccessControlServiceProxy;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.model.access.AccessData;
import org.eclipse.osee.framework.core.model.access.IAccessControlService;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Roberto E. Escobar
 */
public final class CmAccessProviderProxy implements IAccessProvider {

   private final List<ServiceReference<CmAccessControl>> pending = new CopyOnWriteArrayList<>();

   private final Collection<CmAccessControl> cmServices = new CopyOnWriteArraySet<>();

   private IAccessProvider accessProvider;
   private CmAccessControlProvider cmProvider;
   private BundleContext bundleContext;

   private volatile boolean requiresReload = true;

   public void addCmAccessControl(ServiceReference<CmAccessControl> reference) {
      if (isReady()) {
         register(bundleContext, reference);
      } else {
         pending.add(reference);
      }
   }

   public void removeCmAccessControl(ServiceReference<CmAccessControl> reference) {
      if (isReady()) {
         unregister(bundleContext, reference);
      } else {
         pending.remove(reference);
      }
   }

   private boolean isReady() {
      return bundleContext != null && cmProvider != null;
   }

   private void register(BundleContext bundleContext, ServiceReference<CmAccessControl> reference) {
      CmAccessControl cmAccessControl = bundleContext.getService(reference);
      if (isDefault(reference)) {
         cmProvider.setDefaultAccessControl(cmAccessControl);
      } else {
         cmServices.add(cmAccessControl);
      }
      requiresReload = true;
   }

   private void unregister(BundleContext bundleContext, ServiceReference<CmAccessControl> reference) {
      CmAccessControl cmAccessControl = bundleContext.getService(reference);
      if (isDefault(reference)) {
         cmProvider.setDefaultAccessControl(null);
      } else {
         cmServices.remove(cmAccessControl);
      }
      requiresReload = true;
   }

   private boolean isDefault(ServiceReference<CmAccessControl> reference) {
      boolean toReturn = false;
      String value = String.valueOf(reference.getProperty("default"));
      if (Strings.isValid(value)) {
         toReturn = Boolean.parseBoolean(value);
      }
      return toReturn;
   }

   public void start(BundleContext context) {
      this.bundleContext = context;
      requiresReload = true;

      cmProvider = new CmAccessControlProviderImpl(cmServices);
      accessProvider = new CmAccessProvider(cmProvider);

      for (ServiceReference<CmAccessControl> reference : pending) {
         register(bundleContext, reference);
      }
      pending.clear();
   }

   public void stop() {
      accessProvider = null;
      cmProvider.setDefaultAccessControl(null);
      cmServices.clear();
      bundleContext = null;
   }

   private AccessControlServiceProxy getAccessService() {
      AccessControlServiceProxy toReturn = null;
      ServiceReference<IAccessControlService> reference =
         bundleContext.getServiceReference(IAccessControlService.class);
      IAccessControlService service = bundleContext.getService(reference);
      if (service instanceof AccessControlServiceProxy) {
         toReturn = (AccessControlServiceProxy) service;
      } else {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, "Error initializing ObjectAccessProvider");
      }
      return toReturn;
   }

   private void reloadCache() {
      AccessControlServiceProxy service = getAccessService();
      if (service != null) {
         service.reloadCache();
      }
   }

   private IAccessProvider getAccessProvider() {
      return accessProvider;
   }

   private void checkInitialized() {
      Conditions.checkNotNull(getAccessProvider(), "object access provider",
         "Object Access Provider not properly initialized");
      if (requiresReload) {
         requiresReload = false;
         reloadCache();
      }
   }

   @Override
   public void computeAccess(ArtifactToken userArtifact, Collection<?> objToCheck, AccessData accessData) {
      checkInitialized();
      getAccessProvider().computeAccess(userArtifact, objToCheck, accessData);
   }
}