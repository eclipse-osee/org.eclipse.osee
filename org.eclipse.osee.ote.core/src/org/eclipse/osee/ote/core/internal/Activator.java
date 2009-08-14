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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OteProperties;
import org.eclipse.osee.ote.core.StandardShell;
import org.eclipse.osee.ote.core.cmd.CommandDistributer;
import org.eclipse.osee.ote.core.cmd.CommandDistributerImpl;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.TestEnvironmentInterface;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommandManager;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.osee.ote.core.environment.interfaces.RuntimeConfigurationInitilizer;
import org.eclipse.osee.ote.core.environment.interfaces.RuntimeLibraryListener;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.core.environment.status.StatusBoard;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan D. Brooks
 */
public class Activator implements BundleActivator, RuntimeLibraryListener {

   private ServiceRegistration statusBoardRegistration;
   private ServiceRegistration testEnvironmentRegistration;
   private OTEStatusBoard statusBoard;
   CommandDistributer commandDistributer;
   private static Activator activator;
   private BundleContext bundleContext;
   private List<RuntimeConfigurationInitilizer> initializersToUninitialize = new ArrayList<RuntimeConfigurationInitilizer>();
   private CompositeKeyHashMap<String, ServiceTrackerCustomizer, ServiceTracker> serviceTrackers = new CompositeKeyHashMap<String, ServiceTrackerCustomizer, ServiceTracker>();
   private ConsoleCommandManager consoleCommandManager;
   private StandardShell stdShell;
   private ServiceRegistration consoleCommandRegistration;
   private ServiceRegistration commandDistributerRegistration;

   public void start(BundleContext context) throws Exception {
      activator = this;
      statusBoard =  new StatusBoard();
      bundleContext = context;
      statusBoardRegistration = context.registerService(OTEStatusBoard.class.getName(),statusBoard, new Hashtable());
      consoleCommandManager = new ConsoleCommandManager();
      if (OteProperties.isOteCmdConsoleEnabled()) {
         stdShell = new StandardShell(consoleCommandManager);
         stdShell.start();
      } else {
         stdShell = null;
      }
      consoleCommandRegistration = context.registerService(ICommandManager.class.getName(), consoleCommandManager, new Hashtable());

      commandDistributer = new CommandDistributerImpl();
      commandDistributerRegistration = context.registerService(CommandDistributer.class.getName(), commandDistributer, new Hashtable());
   }

   public void stop(BundleContext context) throws Exception {
      commandDistributer.shutdown();
      closeAllValidServiceTrackers();
      statusBoardRegistration.unregister();
      unregisterTestEnvironment();
      stopRuntimeServices();    
      if (stdShell != null) {
         stdShell.shutdown();
      }
      consoleCommandManager.shutdown();
      consoleCommandRegistration.unregister();
      commandDistributerRegistration.unregister();

   }

   public static Activator getInstance(){
      return activator;
   }

   public OTEStatusBoard getOteStatusBoard(){
      return statusBoard;
   }

   public CommandDistributer getCommandDistributer(){
      return commandDistributer;
   }

   public void registerTestEnvironment(TestEnvironment env){
      if(testEnvironmentRegistration != null){
         testEnvironmentRegistration.unregister();
      }
      testEnvironmentRegistration = bundleContext.registerService(TestEnvironmentInterface.class.getName(), env, new Hashtable());
   }

   public void onPostRuntimeLibraryUpdated(ClassLoader classloader) {
      TestEnvironmentInterface env;
      try {
         env = (TestEnvironmentInterface)getServiceTracker(TestEnvironmentInterface.class.getName(), null).waitForService(2000);
      } catch (InterruptedException ex1) {
         throw new IllegalStateException("interrupted while waiting for service", ex1);
      }
      if (env == null) {
         throw new IllegalStateException("Unable to acquire test environment service");
      }
      Class<? extends RuntimeConfigurationInitilizer> clazz = env.getEnvironmentFactory().getRuntimeClass();
      if (clazz == null) {
         String className = env.getEnvironmentFactory().getRuntimeLibraryConfiguration();
         try {
            clazz = (Class<? extends RuntimeConfigurationInitilizer>)classloader.loadClass(className).asSubclass(RuntimeConfigurationInitilizer.class);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Could not use reflection to load runtime configuration class", ex);
            return;
         }
      }
      try {
         RuntimeConfigurationInitilizer initilizer = clazz.newInstance();
         initializersToUninitialize.add(initilizer);
         initilizer.startRuntimeOsgiServices(bundleContext);
         if (env != null && env instanceof TestEnvironment){
            ((TestEnvironment)env).waitForWorkerThreadsToComplete();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Failed to initialize runtime. Runtime Config Class=" + clazz.getName(), ex);
      } 
   }

   public void onRuntimeLibraryUnload() {
      stopRuntimeServices();
   }   

   private void stopRuntimeServices(){
      for(RuntimeConfigurationInitilizer initializer:initializersToUninitialize){
         initializer.stopRuntimeOsgiServices();
      }
      initializersToUninitialize.clear();
   }

   /**
    * 
    */
   public void unregisterTestEnvironment() {
      if(testEnvironmentRegistration != null){
         testEnvironmentRegistration.unregister();
         testEnvironmentRegistration = null;
      }
   }

   /**
    * @param clazz
    * @param customizer
    */
   public ServiceTracker getServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      ServiceTracker tracker = findServiceTracker(clazz, customizer);
      if(tracker == null){
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

   private ServiceTracker findServiceTracker(String clazz, ServiceTrackerCustomizer customizer){
      return serviceTrackers.get(clazz, customizer);
   }

   private void closeAllValidServiceTrackers(){
      for(ServiceTracker tracker :serviceTrackers.values()){
         if(tracker != null){
            tracker.close();
         }
      }
   }
}
