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
package org.eclipse.osee.ote.server.internal;

import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServiceStarterCreationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {IRuntimeLibraryManager.class,
      RemoteServiceRegistrar.class, MessageService.class, IConnectionService.class, PackageAdmin.class};
   private ServiceRegistration registration;

   OteServiceStarterCreationHandler() {
   }

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      RemoteServiceRegistrar remoteServiceRegistrar = getService(RemoteServiceRegistrar.class, services);
      MessageService messageService = getService(MessageService.class, services);
      IConnectionService connectionService = getService(IConnectionService.class, services);
      IRuntimeLibraryManager runtimeLibraryManager = getService(IRuntimeLibraryManager.class, services);
      PackageAdmin packateAdmin = getService(PackageAdmin.class, services);
      try {
         OteServiceStarterImpl oteServiceStarterImpl =
            new OteServiceStarterImpl(packateAdmin, runtimeLibraryManager, connectionService, remoteServiceRegistrar,
               messageService);
         registration = context.registerService(OteServiceStarter.class.getName(), oteServiceStarterImpl, null);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void onDeActivate() {
      registration.unregister();
   }
}
