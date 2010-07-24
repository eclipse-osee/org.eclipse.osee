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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.osee.ote.core.environment.interfaces.RuntimeManagerHandler;
import org.eclipse.osee.ote.core.environment.status.OTEStatusBoard;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.eclipse.osee.ote.server.TestEnvironmentServiceConfigImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

   private BundleContext context;
   private ServiceTracker oteStatusBoardTracker;
   private ServiceTracker consoleCommandtracker;
   private ServiceDependencyTracker oteServiceTracker;
   private ServiceDependencyTracker runtimeManagerHandler;
   private ServiceDependencyTracker oteServiceStarterHandler;
   private static Activator instance;

   @Override
   public void start(BundleContext context) throws Exception {
      instance = this;
      this.context = context;
      
      oteStatusBoardTracker = new ServiceTracker(context, OTEStatusBoard.class.getName(), null);
      oteStatusBoardTracker.open(true);
      
      consoleCommandtracker =
         new ServiceTracker(context, ICommandManager.class.getName(), null);
      consoleCommandtracker.open(true);
      
      runtimeManagerHandler = new ServiceDependencyTracker(context, new RuntimeManagerHandler());
      runtimeManagerHandler.open();
      
      oteServiceStarterHandler = new ServiceDependencyTracker(context, new OteServiceStarterCreationHandler());
      oteServiceStarterHandler.open();
      
      startServer();
   }
   
   public void startServer() throws ClassNotFoundException, SecurityException, NoSuchMethodException{
      String oteServerFactoryClass = System.getProperty("osee.ote.server.factory.class");
      if(oteServerFactoryClass != null){
         String outfileLocation = System.getProperty("osee.ote.outfiles");
         if(outfileLocation == null){
            outfileLocation = System.getProperty("java.io.tmpdir");
         }
         String title = System.getProperty("osee.ote.server.title");
         String name = System.getProperty("user.name");
         String keepEnvAliveWithNoUsersStr = System.getProperty("osee.ote.server.keepAlive");
         boolean keepEnvAliveWithNoUsers = true;
         if(keepEnvAliveWithNoUsersStr != null){
        	 keepEnvAliveWithNoUsers = Boolean.parseBoolean(keepEnvAliveWithNoUsersStr);
         }
         TestEnvironmentServiceConfigImpl config = new TestEnvironmentServiceConfigImpl(keepEnvAliveWithNoUsers, title, name, outfileLocation, null);
        
         String version = context.getBundle().getHeaders().get("Bundle-Version").toString();
         String comment = context.getBundle().getHeaders().get("Bundle-Description").toString();
         String station = "unknown";
         try {
            station = InetAddress.getLocalHost().getHostName();
         } catch (UnknownHostException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         boolean useJiniLookup = System.getProperty("osee.ote.use.lookup") != null;
         boolean isLocalConnector = false;
         
         int index = oteServerFactoryClass.indexOf('.');
         String type = oteServerFactoryClass.substring(index > 0 ? index+1:0);
         PropertyParamter propertyParameter = new PropertyParamter(version, comment, station, type, useJiniLookup, isLocalConnector);
         
         oteServiceTracker = new ServiceDependencyTracker(context, new OteServiceCreationHandler(config, propertyParameter, oteServerFactoryClass));
         oteServiceTracker.open();
      }
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      oteStatusBoardTracker.close();
      if(oteServiceTracker != null){
         oteServiceTracker.close();
      }
      runtimeManagerHandler.close();
      instance = null;
      this.context = null;
   }

   static Activator getDefault(){
      return instance;
   }
   
   public BundleContext getContext() {
      return context;
   }

   public OTEStatusBoard getOteStatusBoard() {
      return (OTEStatusBoard)oteStatusBoardTracker.getService();
   }

   public ICommandManager getCommandManager() {
      return (ICommandManager)consoleCommandtracker.getService();
   }

}
