/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.access.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.IAccessProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;

public class AccessProviderAdmin {

   private final List<IAccessProvider> pendingProviders = new CopyOnWriteArrayList<IAccessProvider>();

   private ILifecycleService lifecycleService;

   public void setLifecycleService(ILifecycleService service) {
      this.lifecycleService = service;
   }

   public void addAccessProvider(IAccessProvider accessProvider) {
      if (isReady()) {
         register(accessProvider);
      } else {
         pendingProviders.add(accessProvider);
      }
   }

   public void removeAccessProvider(IAccessProvider accessProvider) {
      if (isReady()) {
         deregister(accessProvider);
      } else {
         pendingProviders.remove(accessProvider);
      }
   }

   public void start() {
      for (IAccessProvider provider : pendingProviders) {
         register(provider);
      }
   }

   public void stop() {
      for (IAccessProvider provider : pendingProviders) {
         deregister(provider);
      }
   }

   private boolean isReady() {
      return lifecycleService != null;
   }

   private void register(IAccessProvider accessProvider) {
      try {
         lifecycleService.addHandler(AccessProviderVisitor.TYPE, accessProvider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

   private void deregister(IAccessProvider accessProvider) {
      try {
         lifecycleService.removeHandler(AccessProviderVisitor.TYPE, accessProvider);
      } catch (OseeCoreException ex) {
         OseeLog.log(AccessControlHelper.class, Level.SEVERE, ex);
      }
   }

}
