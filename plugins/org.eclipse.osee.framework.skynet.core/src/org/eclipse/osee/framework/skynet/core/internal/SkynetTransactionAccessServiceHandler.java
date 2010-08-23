/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IAccessControlService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.lifecycle.ILifecycleService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionAccessHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionCheckPoint;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransactionHandler;
import org.osgi.framework.BundleContext;

/**
 * @author Roberto E. Escobar
 */
public final class SkynetTransactionAccessServiceHandler extends AbstractTrackingHandler {

   private static final Class<?>[] DEPENDENCIES = new Class[] {//
      ILifecycleService.class, //
         IAccessControlService.class,//
      };

   private SkynetTransactionHandler handler;
   private ILifecycleService lifecycleService;

   @Override
   public Class<?>[] getDependencies() {
      return DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IAccessControlService accessService = getService(IAccessControlService.class, services);
      lifecycleService = getService(ILifecycleService.class, services);
      try {
         handler = new SkynetTransactionAccessHandler(accessService);
         lifecycleService.addHandler(SkynetTransactionCheckPoint.TYPE, handler);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      if (handler != null) {
         try {
            lifecycleService.removeHandler(SkynetTransactionCheckPoint.TYPE, handler);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }
}