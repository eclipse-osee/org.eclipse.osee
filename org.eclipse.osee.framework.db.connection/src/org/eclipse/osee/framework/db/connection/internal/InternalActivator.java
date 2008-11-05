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
package org.eclipse.osee.framework.db.connection.internal;

import org.eclipse.osee.framework.db.connection.IConnection;
import org.eclipse.osee.framework.db.connection.IDbConnectionFactory;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformation;
import org.eclipse.osee.framework.db.connection.IDbConnectionInformationContributer;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Roberto E. Escobar
 */
public class InternalActivator implements BundleActivator {

   private static InternalActivator instance = null;
   private DbConnectionProviderTracker dbConnectionProviderTracker;
   private DbConnectionInfoTracker dbConnectionInfoTracker;
   private ServiceRegistration dbConnectionFactoryRegistration;
   private ServiceRegistration dbConnectionInfoProviderRegistration;
   private BindTracker connectionTracker;
   private BindTracker infoProviderTracker;

   private static InternalActivator getInstance() {
      return instance;
   }

   public static IDbConnectionFactory getConnectionFactory() {
      return getInstance().dbConnectionProviderTracker.getFactory();
   }

   public static IDbConnectionInformation getConnectionInfos() {
      return getInstance().dbConnectionInfoTracker.get();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
    */
   public void start(BundleContext context) throws Exception {
      instance = this;

      DbConnectionFactory dbConnectionFactory = new DbConnectionFactory();
      connectionTracker = new BindTracker(context, IConnection.class.getName(), dbConnectionFactory);
      connectionTracker.open();
      dbConnectionFactoryRegistration =
            context.registerService(IDbConnectionFactory.class.getName(), dbConnectionFactory, null);

      DbConnectionInformationImpl dbConnectionInfo = new DbConnectionInformationImpl();
      infoProviderTracker =
            new BindTracker(context, IDbConnectionInformationContributer.class.getName(), dbConnectionInfo);
      infoProviderTracker.open();
      dbConnectionInfoProviderRegistration =
            context.registerService(IDbConnectionInformation.class.getName(), dbConnectionInfo, null);

      dbConnectionProviderTracker = new DbConnectionProviderTracker(context);
      dbConnectionProviderTracker.open();
      dbConnectionInfoTracker = new DbConnectionInfoTracker(context);
      dbConnectionInfoTracker.open();
   }

   /*
    * (non-Javadoc)
    * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
    */
   public void stop(BundleContext context) throws Exception {
      instance = null;
      dbConnectionProviderTracker.close();
      dbConnectionInfoTracker.close();
      connectionTracker.close();
      infoProviderTracker.close();
      dbConnectionFactoryRegistration.unregister();
      dbConnectionInfoProviderRegistration.unregister();
   }
}
