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

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;

import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.services.RegisteredServiceReference;
import org.eclipse.osee.ote.HostServerProperties;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.ReturnStatus;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.io.OTEServerFolder;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.osee.ote.server.PropertyParamter;

public class OteService implements IHostTestEnvironment {

   private final ServiceID serviceID;
   private final EnhancedProperties enhancedProperties;
   private MessageSystemTestEnvironment currentEnvironment;
   private ITestEnvironment remoteEnvironment;
   private final EnvironmentCreationParameter environmentCreation;
   private final IRuntimeLibraryManager runtimeLibraryManager;
   private RegisteredServiceReference registeredServiceReference;
   private OTESessionManager oteSessions;
   
   
   public OteService(IRuntimeLibraryManager runtimeLibraryManager, EnvironmentCreationParameter environmentCreation, OTESessionManager oteSessions, PropertyParamter parameterObject, EnhancedProperties properties, OteUdpEndpoint receiver) {
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.environmentCreation = environmentCreation;
      this.oteSessions = oteSessions;
      
      Uuid uuid = UuidFactory.generate();
      Long lsb = Long.valueOf(uuid.getLeastSignificantBits());
      Long msb = Long.valueOf(uuid.getMostSignificantBits());
      serviceID = new ServiceID(msb.longValue(), lsb.longValue());

      enhancedProperties = properties;
      enhancedProperties.setProperty(HostServerProperties.name.name(), environmentCreation.getServerTitle());
      enhancedProperties.setProperty(HostServerProperties.station.name(), parameterObject.getStation());
      enhancedProperties.setProperty(HostServerProperties.version.name(), parameterObject.getVersion());
      enhancedProperties.setProperty(HostServerProperties.type.name(), parameterObject.getType());
      enhancedProperties.setProperty(HostServerProperties.maxUsers.name(), Integer.toString(environmentCreation.getMaxUsersPerEnvironment()));
      enhancedProperties.setProperty(HostServerProperties.comment.name(), parameterObject.getComment());
      enhancedProperties.setProperty(HostServerProperties.date.name(), new Date().toString());
      enhancedProperties.setProperty(HostServerProperties.group.name(), "OSEE Test Environment");
      enhancedProperties.setProperty(HostServerProperties.owner.name(), OtePropertiesCore.userName.getValue());
      enhancedProperties.setProperty(HostServerProperties.id.name(), serviceID.toString());
      enhancedProperties.setProperty(HostServerProperties.activeMq.name(), environmentCreation.getBroker().getUri().toString());
      try {
         String format = String.format("tcp://%s:%d", receiver.getLocalEndpoint().getAddress().getHostAddress(), receiver.getLocalEndpoint().getPort());
         if(OtePropertiesCore.httpPort.getValue() == null){
            enhancedProperties.setProperty(HostServerProperties.appServerURI.name(), format);
         } else {
            enhancedProperties.setProperty(HostServerProperties.appServerURI.name(), format);
         }
         enhancedProperties.setProperty(HostServerProperties.oteUdpEndpoint.name(), format);
      } catch (Exception e) {
         OseeLog.log(OteService.class, Level.SEVERE, "Failed to set the appServerURI", e);
      }
      
      OTEServerFolder service = ServiceUtility.getService(OTEServerFolder.class);
      if(service != null){
         File dir = service.getCurrentServerFolder();
         dir.mkdirs();
         if(dir.exists() && dir.isDirectory()){
            try{
               Properties serverProperties = new Properties();
               serverProperties.putAll(enhancedProperties.asMap());
               serverProperties.store(new FileOutputStream(new File(dir, "server.properties")), "");
               File running = new File(dir, ".running");
               running.createNewFile();
               running.deleteOnExit();
            } catch (Throwable th){
               th.printStackTrace();
            }
         }
      }
   }
   
   @Override
   public NodeInfo getBroker(){
      return environmentCreation.getBroker();
   }

   @Override
   public EnhancedProperties getProperties() throws RemoteException {
      return enhancedProperties;
   }

   @Override
   public ConnectionRequestResult requestEnvironment(IRemoteUserSession session, UUID sessionId, TestEnvironmentConfig config) throws RemoteException {
      try {
         OseeLog.log(OteService.class, Level.INFO,
            "received request for test environment from user " + session.getUser().getName());
         if (!isEnvironmentAvailable()) {
            createEnvironment();
         }
         
         oteSessions.add(sessionId, session);
         updateDynamicInfo();
         return new ConnectionRequestResult(remoteEnvironment, sessionId, new ReturnStatus("Success", true));
      } catch (Throwable ex) {
         OseeLog.log(OteService.class, Level.SEVERE,
            "Exception while requesting environment for user " + session.getUser().getName(), ex);
         throw new RemoteException("Exception while requesting environment for user ", ex);
      }
   }

   private void createEnvironment() throws Throwable {
      currentEnvironment = environmentCreation.createEnvironment();
      remoteEnvironment = environmentCreation.createRemoteTestEnvironment(currentEnvironment);
      currentEnvironment.startup(environmentCreation.getOutfileLocation());
   }

   private boolean isEnvironmentAvailable() {
      return remoteEnvironment != null;
   }

   public void updateDynamicInfo() throws RemoteException {
      Collection<OSEEPerson1_4> userList = new LinkedList<>();
      StringBuilder sb = new StringBuilder();
      if (isEnvironmentAvailable()) {
         for(UUID sessionId:oteSessions.get()){
            IUserSession session = oteSessions.get(sessionId);
            try {
               userList.add(session.getUser());
            } catch (Exception e) {
               OseeLog.log(OteService.class, Level.WARNING, e);
            }
         }
      }
      for (OSEEPerson1_4 person : userList) {
         sb.append(person.getName());
         sb.append(", ");
      }
      if (sb.length() > 2) {
         String list = sb.toString().substring(0, sb.length() - 2);
         environmentCreation.getServiceConnector().setProperty("user_list", list);
      } else {
         environmentCreation.getServiceConnector().setProperty("user_list", "N/A");
      }
      if (registeredServiceReference != null) {
         registeredServiceReference.update();
      }
   }

   public ServiceID getServiceID() throws RemoteException {
      return serviceID;
   }

   public void kill() throws RemoteException {
      if(currentEnvironment != null){
         currentEnvironment.shutdown();
      }
   }

   public void set(RegisteredServiceReference ref) {
      this.registeredServiceReference = ref;
   }

   @Override
   public void disconnect(UUID sessionId) throws RemoteException {
      if (currentEnvironment != null) {
         oteSessions.remove(sessionId);
         updateDynamicInfo();
      }
   }

   @Override
   public String getHttpURL() throws RemoteException {
      return (String)enhancedProperties.getProperty("appServerURI");
   }
   
}
