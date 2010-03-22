package org.eclipse.osee.ote.server.internal;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import net.jini.core.lookup.ServiceID;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jini.service.interfaces.IService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.services.RegisteredServiceReference;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.ReturnStatus;
import org.eclipse.osee.ote.core.environment.BundleDescription;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.server.PropertyParamter;


public class OteService implements IHostTestEnvironment, IService {

   private final ServiceID serviceID;
   private final EnhancedProperties enhancedProperties;
   private MessageSystemTestEnvironment currentEnvironment;
   private ITestEnvironment remoteEnvironment;
   private EnvironmentCreationParameter environmentCreation;
   private final IRuntimeLibraryManager runtimeLibraryManager;
   private RegisteredServiceReference registeredServiceReference;
   
   public OteService(IRuntimeLibraryManager runtimeLibraryManager, EnvironmentCreationParameter environmentCreation, PropertyParamter parameterObject){
      this.runtimeLibraryManager = runtimeLibraryManager;
      this.environmentCreation = environmentCreation;
      Uuid uuid = UuidFactory.generate();
      Long lsb = Long.valueOf(uuid.getLeastSignificantBits());
      Long msb = Long.valueOf(uuid.getMostSignificantBits());
      serviceID = new ServiceID(msb.longValue(), lsb.longValue());
      
      enhancedProperties = new EnhancedProperties();
      enhancedProperties.setProperty("name", environmentCreation.getServerTitle());
      enhancedProperties.setProperty("station", parameterObject.getStation());
      enhancedProperties.setProperty("version", parameterObject.getVersion());
      enhancedProperties.setProperty("type", parameterObject.getType());
      enhancedProperties.setProperty("maxUsers", Integer.toString(environmentCreation.getMaxUsersPerEnvironment()));
      enhancedProperties.setProperty("comment", parameterObject.getComment());
      enhancedProperties.setProperty("date", new Date().toString());
      enhancedProperties.setProperty("group", "OSEE Test Environment");
      enhancedProperties.setProperty("owner", System.getProperty("user.name"));
   }
   
   @Override
   public byte[] getOutfileResultSummary() throws RemoteException {
      return null;
   }

   @Override
   public EnhancedProperties getProperties() throws RemoteException {
      return enhancedProperties;
   }

   @Override
   public ITestEnvironment[] getRemoteEnvironments() throws RemoteException {
      if(isEnvironmentAvailable()){
         return new ITestEnvironment[]{remoteEnvironment};
      } else {
         return new ITestEnvironment[0];
      }
   }

   @Override
   public ConnectionRequestResult requestEnvironment(IRemoteUserSession session, TestEnvironmentConfig config) throws RemoteException {
      try {
         OseeLog.log(OteService.class, Level.INFO,"received request for test environment from user " + session.getUser().getName());
         if (!isEnvironmentAvailable()){
            createEnvironment();
            
         } else {
            ReturnStatus status = currentEnvironment.getRuntimeManager().isRunningJarVersions(config.getJarVersions());
            if (!status.getStatus()) {
               return new ConnectionRequestResult(null, null,
                     new ReturnStatus(String.format("Unable to connect to environment because users already connected are using different runtime jars.  Connected users [%s].  %s",
                           currentEnvironment.getUserList().toString(), status.getMessage()), false));
            }
         }
         UserTestSessionKey key = currentEnvironment.addUser(session);
         updateDynamicInfo();
         return new ConnectionRequestResult(remoteEnvironment, key, new ReturnStatus("Success", true));
      } catch (Throwable ex) {
         OseeLog.log(OteService.class, Level.SEVERE,
               "Exception while requesting environment for user " + session.getUser().getName(), ex);
         throw new RemoteException("Exception while requesting environment for user ", ex);
      }
   }

   private void createEnvironment() throws Throwable {
      currentEnvironment = environmentCreation.createEnvironment();
      remoteEnvironment = environmentCreation.createRemoteTestEnvironment(currentEnvironment);
      remoteEnvironment.startup(environmentCreation.getOutfileLocation());
   }

   private boolean isEnvironmentAvailable() {
      return remoteEnvironment != null;
   }

   public void updateDynamicInfo() throws RemoteException {
      Collection<OSEEPerson1_4> userList = new LinkedList<OSEEPerson1_4>();
      StringBuilder sb = new StringBuilder();
      if(isEnvironmentAvailable()){
         userList.addAll(remoteEnvironment.getUserList());
      }
      for(OSEEPerson1_4 person:userList){
         sb.append(person.getName());
         sb.append(", ");
      }
      if(sb.length()>2){
         String list = sb.toString().substring(0, sb.length()-2);
         environmentCreation.getServiceConnector().setProperty("user_list", list);
      } else {
         environmentCreation.getServiceConnector().setProperty("user_list", "");
      }
      registeredServiceReference.update();
   }
   
   @Override
   public ServiceID getServiceID() throws RemoteException {
      return serviceID;
   }

   @Override
   public void kill() throws RemoteException {
   }

   @Override
   public void cleanupRuntimeBundles() throws RemoteException {
      runtimeLibraryManager.cleanup();
   }

   @Override
   public boolean isBundleAvailable(String symbolicName, String version, byte[] md5Digest) throws RemoteException {
      return runtimeLibraryManager.isBundleAvailable(symbolicName, version, md5Digest);
   }

   @Override
   public void sendRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException {
      try {
         runtimeLibraryManager.loadBundles(bundles);
      } catch (Exception ex) {
         OseeLog.log(OteService.class, Level.SEVERE, ex);
         throw new RemoteException(ex.getMessage());
      }
   }

   @Override
   public void updateRuntimeBundle(Collection<BundleDescription> bundles) throws RemoteException {
      try {
         runtimeLibraryManager.updateBundles(bundles);
      } catch (Exception ex) {
         OseeLog.log(OteService.class, Level.SEVERE, ex);
         throw new RemoteException(ex.getMessage());
      }
   }

   public void set(RegisteredServiceReference ref) {
      this.registeredServiceReference = ref;
   }
}
