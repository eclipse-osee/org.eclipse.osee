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
package org.eclipse.osee.framework.database.internal.trackers;

import java.util.Map;
import org.eclipse.osee.framework.core.util.AbstractTrackingHandler;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.eclipse.osee.framework.database.internal.core.ConnectionFactoryProvider;
import org.eclipse.osee.framework.database.internal.core.OseeDatabaseServiceImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public final class OseeDatabaseServiceRegistrationHandler extends AbstractTrackingHandler {

   private final static Class<?>[] SERVICE_DEPENDENCIES = new Class<?>[] {IDatabaseInfoProvider.class};

   private ServiceRegistration serviceRegistration;

   public OseeDatabaseServiceRegistrationHandler() {
      super();
   }

   @Override
   public Class<?>[] getDependencies() {
      return SERVICE_DEPENDENCIES;
   }

   @Override
   public void onActivate(BundleContext context, Map<Class<?>, Object> services) {
      IDatabaseInfoProvider dbInfoProvider = getService(IDatabaseInfoProvider.class, services);

      ConnectionFactoryProvider dbConnectionFactory = new ConnectionFactoryProvider(context);
      IOseeDatabaseService databaseService = new OseeDatabaseServiceImpl(dbInfoProvider, dbConnectionFactory);
      serviceRegistration = context.registerService(IOseeDatabaseService.class.getName(), databaseService, null);
   }

   @Override
   public void onDeActivate() {
      if (serviceRegistration != null) {
         serviceRegistration.unregister();
      }
   }
}