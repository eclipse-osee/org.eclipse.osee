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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.access.internal.AccessProviderVisitor;
import org.eclipse.osee.framework.access.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.CmAccessControl;
import org.eclipse.osee.framework.core.services.CmAccessControlProvider;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.core.util.ServiceBindType;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public final class CmAccessProviderRegistrationHandler extends AbstractTrackingHandler {

   private final Map<Class<?>, ServiceBindType> dependencyMap = new LinkedHashMap<Class<?>, ServiceBindType>();
   private final Collection<CmAccessControl> cmServices = new CopyOnWriteArraySet<CmAccessControl>();
   private IAccessProvider accessProvider;
   private ILifecycleService service;

   public CmAccessProviderRegistrationHandler() {
      dependencyMap.put(ILifecycleService.class, ServiceBindType.SINGLETON);
      dependencyMap.put(IAccessControlService.class, ServiceBindType.SINGLETON);
      dependencyMap.put(CmAccessControl.class, ServiceBindType.MANY);
   }

   @Override
   public Class<?>[] getDependencies() {
      return null;
   }

   @Override
   public Map<Class<?>, ServiceBindType> getConfiguredDependencies() {
      return dependencyMap;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      service = getService(ILifecycleService.class, services);
      CmAccessControl cm = getService(CmAccessControl.class, services);
      cmServices.add(cm);
      try {
         CmAccessControlProvider cmProvider = new CmAccessControlProviderImpl(cmServices);
         accessProvider = new CmAccessProvider(cmProvider);
         service.addHandler(AccessProviderVisitor.TYPE, accessProvider);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onServiceAdded(BundleContext context, Class<?> clazz, Object service) {
      cmServices.add((CmAccessControl) service);
   }

   @Override
   public void onServiceRemoved(BundleContext context, Class<?> clazz, Object service) {
      cmServices.remove(service);
   }

   @Override
   public void onDeActivate() {
      cmServices.clear();
      if (accessProvider != null) {
         try {
            service.removeHandler(AccessProviderVisitor.TYPE, accessProvider);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}