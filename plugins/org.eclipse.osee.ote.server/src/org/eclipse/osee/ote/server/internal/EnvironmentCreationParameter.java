/*
 * Created on Mar 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.server.internal;

import java.io.Serializable;
import java.rmi.server.ExportException;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
/**
 * @author  Andrew M. Finkbeiner
 */
public class EnvironmentCreationParameter {
   private final NodeInfo oteEmbeddedBroker;
   private final IServiceConnector serviceConnector;
   private final ITestEnvironmentServiceConfig config;
   private final IRuntimeLibraryManager runtimeLibraryManager;
   private ITestEnvironment remoteTestEnvironment;
   private ITestEnvironment exportedRemoteTestEnvironment;
   private final TestEnvironmentFactory factory;
   
   /**
    * @param runtimeLibraryManager2
    * @param nodeInfo
    * @param serviceSideConnector
    * @param config2
    * @param factory
    */
   public EnvironmentCreationParameter(IRuntimeLibraryManager runtimeLibraryManager, NodeInfo oteEmbeddedBroker, IServiceConnector serviceConnector, ITestEnvironmentServiceConfig config, TestEnvironmentFactory factory) {
      this.oteEmbeddedBroker = oteEmbeddedBroker;
      this.serviceConnector = serviceConnector;
      this.config = config;
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.factory = factory;
   }

   public Serializable getServerTitle() {
      return config.getServerTitle();
   }

   public int getMaxUsersPerEnvironment() {
      return config.getMaxUsersPerEnvironment();
   }
   
   public String getOutfileLocation(){
      return config.getOutfileLocation();
   }
   
   public MessageSystemTestEnvironment createEnvironment() throws Throwable {
      MessageSystemTestEnvironment testEnvironment = factory.createEnvironment(runtimeLibraryManager);
      testEnvironment.setOteNodeInfo(oteEmbeddedBroker);
      testEnvironment.init(serviceConnector);
      return testEnvironment;
   }

   public ITestEnvironment createRemoteTestEnvironment(MessageSystemTestEnvironment currentEnvironment) throws ExportException {
      remoteTestEnvironment = new RemoteTestEnvironment(currentEnvironment, serviceConnector);
      exportedRemoteTestEnvironment = (ITestEnvironment)serviceConnector.export(remoteTestEnvironment);
      return exportedRemoteTestEnvironment;
   }

   public IServiceConnector getServiceConnector() {
      return serviceConnector;
   }
}