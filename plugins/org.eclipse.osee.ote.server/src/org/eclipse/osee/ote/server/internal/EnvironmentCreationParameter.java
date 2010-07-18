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

import java.io.Serializable;
import java.rmi.server.ExportException;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Andrew M. Finkbeiner
 */
class EnvironmentCreationParameter {
   private final NodeInfo oteEmbeddedBroker;
   private final IServiceConnector serviceConnector;
   private final ITestEnvironmentServiceConfig config;
   private final IRuntimeLibraryManager runtimeLibraryManager;
   private ITestEnvironment remoteTestEnvironment;
   private ITestEnvironment exportedRemoteTestEnvironment;
   private TestEnvironmentFactory factory;
   private final PackageAdmin packageAdmin;
   private final String environmentFactoryClass;

   /**
    * @param runtimeLibraryManager2
    * @param nodeInfo
    * @param serviceSideConnector
    * @param config2
    * @param factory
    * @param environmentFactoryClass
    */
   public EnvironmentCreationParameter(IRuntimeLibraryManager runtimeLibraryManager, NodeInfo oteEmbeddedBroker, IServiceConnector serviceConnector, ITestEnvironmentServiceConfig config, TestEnvironmentFactory factory, String environmentFactoryClass, PackageAdmin packageAdmin) {
      this.oteEmbeddedBroker = oteEmbeddedBroker;
      this.serviceConnector = serviceConnector;
      this.config = config;
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.factory = factory;
      this.packageAdmin = packageAdmin;
      this.environmentFactoryClass = environmentFactoryClass;

   }

   public Serializable getServerTitle() {
      return config.getServerTitle();
   }

   public int getMaxUsersPerEnvironment() {
      return config.getMaxUsersPerEnvironment();
   }

   public String getOutfileLocation() {
      return config.getOutfileLocation();
   }

   public MessageSystemTestEnvironment createEnvironment() throws Throwable {
      if (factory == null) {
         ExportClassLoader exportClassLoader = new ExportClassLoader(packageAdmin);
         Class<? extends TestEnvironmentFactory> clazz =
               exportClassLoader.loadClass(environmentFactoryClass).asSubclass(TestEnvironmentFactory.class);
         factory = clazz.newInstance();
      }
      MessageSystemTestEnvironment testEnvironment = factory.createEnvironment(runtimeLibraryManager);
      testEnvironment.setOteNodeInfo(oteEmbeddedBroker);
      testEnvironment.init(serviceConnector);
      return testEnvironment;
   }

   public ITestEnvironment createRemoteTestEnvironment(MessageSystemTestEnvironment currentEnvironment) throws ExportException {
      remoteTestEnvironment =
            new RemoteTestEnvironment(currentEnvironment, serviceConnector, config.keepEnvAliveWithNoUsers());
      exportedRemoteTestEnvironment = (ITestEnvironment) serviceConnector.export(remoteTestEnvironment);
      return exportedRemoteTestEnvironment;
   }

   public IServiceConnector getServiceConnector() {
      return serviceConnector;
   }
   
   public boolean isKeepAliveWithNoUsers(){
	   return config.keepEnvAliveWithNoUsers();
   }
}