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
package org.eclipse.osee.ote.core.internal;

import java.util.Hashtable;
import java.util.logging.Level;

import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OteProperties;
import org.eclipse.osee.ote.core.StandardShell;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommandManager;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.osee.ote.message.internal.MessageIoManagementStarter;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator {

   private ServiceRegistration testEnvironmentRegistration;
   private static Activator activator;
   private BundleContext bundleContext;
   private final CompositeKeyHashMap<String, ServiceTrackerCustomizer, ServiceTracker> serviceTrackers =
      new CompositeKeyHashMap<String, ServiceTrackerCustomizer, ServiceTracker>();
   private ConsoleCommandManager consoleCommandManager;
   private StandardShell stdShell;
   private ServiceRegistration consoleCommandRegistration;
   private ServiceDependencyTracker serviceDependencyTracker;
   private MessageIoManagementStarter messageIoManagementStarter;
   private ServiceTracker testEnvTracker;

   @Override
   public void start(BundleContext context) throws Exception {
      activator = this;
      bundleContext = context;
      consoleCommandManager = new ConsoleCommandManager();
      if (OteProperties.isOteCmdConsoleEnabled()) {
         stdShell = new StandardShell(consoleCommandManager);
         stdShell.start();
      } else {
         stdShell = null;
      }
      consoleCommandRegistration =
         context.registerService(ICommandManager.class.getName(), consoleCommandManager, new Hashtable());

      serviceDependencyTracker = new ServiceDependencyTracker(bundleContext, new StatusBoardRegistrationHandler());
      serviceDependencyTracker.open();
      
      testEnvTracker = new ServiceTracker(context, TestEnvironmentInterface.class.getName(), null);
      testEnvTracker.open(true);
      
      messageIoManagementStarter = new MessageIoManagementStarter(context);
      messageIoManagementStarter.open(true);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      serviceDependencyTracker.close();
      closeAllValidServiceTrackers();
      unregisterTestEnvironment();
      if (stdShell != null) {
         stdShell.shutdown();
      }
      consoleCommandManager.shutdown();
      consoleCommandRegistration.unregister();
      
      messageIoManagementStarter.close();
      testEnvTracker.close();
   }

   public static Activator getInstance() {
      return activator;
   }

   public void registerTestEnvironment(TestEnvironment env) {
      if (testEnvironmentRegistration != null) {
         testEnvironmentRegistration.unregister();
      }
      testEnvironmentRegistration =
         bundleContext.registerService(TestEnvironmentInterface.class.getName(), env, new Hashtable());
   }

   public void unregisterTestEnvironment() {
      if (testEnvironmentRegistration != null) {
         testEnvironmentRegistration.unregister();
         testEnvironmentRegistration = null;
      }
   }

   public ServiceTracker getServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      ServiceTracker tracker = findServiceTracker(clazz, customizer);
      if (tracker == null) {
         tracker = createNewServiceTracker(clazz, customizer);
      }
      return tracker;
   }

   private ServiceTracker createNewServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      ServiceTracker tracker = new ServiceTracker(bundleContext, clazz, customizer);
      tracker.open(true);
      serviceTrackers.put(clazz, customizer, tracker);
      return tracker;
   }

   private ServiceTracker findServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      return serviceTrackers.get(clazz, customizer);
   }

   private void closeAllValidServiceTrackers() {
      for (ServiceTracker tracker : serviceTrackers.values()) {
         if (tracker != null) {
            tracker.close();
         }
      }
   }
   
   public static TestEnvironmentInterface getTestEnvironment() {
	   try {
		   return (TestEnvironmentInterface) getInstance().testEnvTracker.waitForService(20000);
	   } catch (InterruptedException e) {
		   OseeLog.log(Activator.class, Level.SEVERE, e);
	   }
	   return null;
   }
}
