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
package org.eclipse.osee.framework.database.internal;

import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.trackers.OseeDatabaseServiceRegistrationHandler;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Roberto E. Escobar
 */
public class Activator implements BundleActivator {
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.database";

   private static Activator instance = null;

   private ServiceDependencyTracker databaseServiceTracker;
   private ServiceTracker dbTracker;
   private ServiceTracker infoTracker;

   public Activator() {
   }

   public void start(BundleContext context) throws Exception {
      instance = this;

      databaseServiceTracker = new ServiceDependencyTracker(context, new OseeDatabaseServiceRegistrationHandler());
      databaseServiceTracker.open();

      dbTracker = new ServiceTracker(context, IOseeDatabaseService.class.getName(), null);
      dbTracker.open();

      infoTracker = new ServiceTracker(context, IDbConnectionInformation.class.getName(), null);
      infoTracker.open();
   }

   public IDbConnectionInformation getConnectionInfos() {
      return (IDbConnectionInformation) infoTracker.getService();
   }

   public IOseeDatabaseService getOseeDatabaseService() {
      return (IOseeDatabaseService) dbTracker.getService();
   }

   public void stop(BundleContext context) throws Exception {
      databaseServiceTracker.close();
      dbTracker.close();
      infoTracker.close();
      instance = null;
   }

   public static Activator getInstance() {
      return instance;
   }

}
