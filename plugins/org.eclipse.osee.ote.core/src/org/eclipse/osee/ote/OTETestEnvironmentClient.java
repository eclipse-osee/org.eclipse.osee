package org.eclipse.osee.ote;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.eclipse.osee.ote.classserver.HeadlessClassServer;
import org.eclipse.osee.ote.core.BundleInfo;
import org.eclipse.osee.ote.core.ConnectionRequestResult;
import org.eclipse.osee.ote.core.IRemoteUserSession;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.TestEnvironmentConfig;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestCommandResult;
import org.eclipse.osee.ote.core.framework.command.RunTests;
import org.eclipse.osee.ote.endpoint.OteEndpointUtil;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.filetransfer.TcpFileTransfer;
import org.eclipse.osee.ote.filetransfer.TcpFileTransferHandle;
import org.eclipse.osee.ote.message.event.OteEventMessageUtil;
import org.eclipse.osee.ote.message.event.send.OteEndpointSendEventMessage;
import org.eclipse.osee.ote.message.event.send.OteEventMessageFuture;
import org.eclipse.osee.ote.message.event.send.OteSendEventMessage;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.osee.ote.remote.messages.BooleanResponse;
import org.eclipse.osee.ote.remote.messages.ConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.DisconnectRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.RequestHostEnvironmentProperties;
import org.eclipse.osee.ote.remote.messages.RequestRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.RunTestsCancel;
import org.eclipse.osee.ote.remote.messages.RunTestsGetCommandResultReq;
import org.eclipse.osee.ote.remote.messages.RunTestsGetCommandResultResp;
import org.eclipse.osee.ote.remote.messages.RunTestsIsCancelled;
import org.eclipse.osee.ote.remote.messages.RunTestsIsDone;
import org.eclipse.osee.ote.remote.messages.RunTestsSerialized;
import org.eclipse.osee.ote.remote.messages.SerializedConfigurationAndResponse;
import org.eclipse.osee.ote.remote.messages.SerializedConnectionRequestResult;
import org.eclipse.osee.ote.remote.messages.SerializedDisconnectRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.SerializedEnhancedProperties;
import org.eclipse.osee.ote.remote.messages.SerializedOTEJobStatus;
import org.eclipse.osee.ote.remote.messages.SerializedRequestRemoteTestEnvironment;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentServerShutdown;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentSetBatchMode;
import org.eclipse.osee.ote.remote.messages.TestEnvironmentTransferFile;
import org.osgi.service.event.EventAdmin;

public class OTETestEnvironmentClient {

   private OteUdpEndpoint service;
   private InetSocketAddress destinationAddress;
   private ExecutorService pool;
   
   public static OTETestEnvironmentClient getInstance(String endpoint){
      return new OTETestEnvironmentClient(ServiceUtility.getService(OteUdpEndpoint.class), OteEndpointUtil.getAddress(endpoint));
   }
   
   public OTETestEnvironmentClient(OteUdpEndpoint service, InetSocketAddress destinationAddress) {
      this.service = service;
      this.destinationAddress = destinationAddress;
      this.pool = Executors.newCachedThreadPool(new ThreadFactory() {
         
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTETestEnvironmentClient Thread");
            return th;
         }
      });
   }
   
   public void shutdownServer(String serverId){
      TestEnvironmentServerShutdown shutdown = new TestEnvironmentServerShutdown();
      shutdown.SERVER_ID.setValue(serverId);
      OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
      sendit.asynchSend(shutdown);
   }
   
   public boolean getServerFile(File localFile, File serverFile) throws IOException, ExecutionException, InterruptedException{
      TcpFileTransferHandle receiveFile = TcpFileTransfer.receiveFile(pool, localFile, 0);
      TestEnvironmentTransferFile transferFile = new TestEnvironmentTransferFile();
      transferFile.ADDRESS.setAddress(receiveFile.getLocalAddress().getAddress());
      transferFile.ADDRESS.setPort(receiveFile.getLocalAddress().getPort());
      transferFile.FILE_PATH.setValue(serverFile.getAbsolutePath());
      OteEventMessageUtil.sendEvent(transferFile);
      return receiveFile.awaitCompletion();      
   }
   
   public void setBatchMode(boolean batchMode) throws URISyntaxException, InterruptedException, ExecutionException, IOException {
      TestEnvironmentSetBatchMode setBatchMode = new TestEnvironmentSetBatchMode();
      setBatchMode.SET_BATCH_MODE.setValue(batchMode);
      OteEventMessageUtil.postEvent(setBatchMode);
   }
   
   public ICommandHandle runScripts(UUID sessionId, PropertyStore globalProperties, List<IPropertyStore> scriptProperties, boolean useOTEBuilderJars, File rootFolderForOTEBuilderJars, SubProgressMonitor monitor, String serverId, ConfigurationStatusCallback configurationStatusCallback) throws URISyntaxException, InterruptedException, ExecutionException, IOException {
      Configuration configuration = null;
      if(useOTEBuilderJars){
         configuration = configureEnvironment(serverId, rootFolderForOTEBuilderJars, monitor, configurationStatusCallback);
      }
      RunTests runTests = new RunTests(UUID.randomUUID().toString(), sessionId, configuration, globalProperties, scriptProperties);
      
      OteEventMessageUtil.postEvent(new RunTestsSerialized(runTests));
      return new EventRunTestsHandle(runTests.getGUID(), new OteSendEventMessage(ServiceUtility.getService(EventAdmin.class)));
   }

   public Configuration configureEnvironment(String serverId, final List<File> bundlesToSend, final boolean installBundles, final SubProgressMonitor monitor, final ConfigurationStatusCallback configurationStatusCallback) {
      HeadlessClassServer classServer = null;
      Configuration localConfiguration = new Configuration();
      try{
       
         classServer = new HeadlessClassServer(PortUtil.getInstance().getValidPort(), InetAddress.getLocalHost(), bundlesToSend);
         for (BundleInfo bundleInfo : classServer.getBundles()) {
            ConfigurationItem item = new ConfigurationItem(bundleInfo.getServerBundleLocation().toString(),bundleInfo.getVersion(),bundleInfo.getSymbolicName(), bundleInfo.getMd5Digest(), installBundles);
            localConfiguration.addItem(item);
         }
         
         if(service != null){
            InetSocketAddress address = service.getLocalEndpoint();
            ConfigurationAndResponse configAndResponse = new ConfigurationAndResponse(address.getAddress().getHostAddress(), address.getPort(), localConfiguration, installBundles, serverId);
            SerializedConfigurationAndResponse ser = new SerializedConfigurationAndResponse();
            ser.setObject(configAndResponse);
            ser.getHeader().RESPONSE_TOPIC.setValue(SerializedOTEJobStatus.EVENT);

            OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
            service.addBroadcast(service.getOteEndpointSender(destinationAddress));//so we can communicate via console before connect
            WaitForCompletion completion = new WaitForCompletion(monitor);
            final OteEventMessageFuture<SerializedConfigurationAndResponse, SerializedOTEJobStatus> asynchSendAndResponse = sendit.asynchSendAndMultipleResponse(SerializedOTEJobStatus.class, ser, completion,  1000 * 60 * 10);
            Thread th = new Thread(new Runnable(){
               @Override
               public void run() {
                  while(true){
                     if(monitor != null && monitor.isCanceled()){
                        asynchSendAndResponse.complete();
                        return;
                     } else if (asynchSendAndResponse.isDone()){
                        return;
                     } else {
                        try {
                           Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                     }
                  }
               }
            });
            th.setDaemon(true);
            th.start();
            asynchSendAndResponse.waitForCompletion();
            if(!asynchSendAndResponse.isTimedOut() && completion.getStatus() != null){
               if(completion.getStatus().isSuccess()){
                  configurationStatusCallback.success();
               } else {
                  String errorLog = completion.getStatus().getErrorLog();
                  if(errorLog.length() > 0){
                     configurationStatusCallback.failure(errorLog);
                  } else {//no diff report, must be the same version
                     configurationStatusCallback.success();
                  }
               }
            }
         }
      } catch (Throwable th){
         th.printStackTrace();
      } finally {
         if(classServer != null){
            classServer.stop();
         }
      }
      return localConfiguration;
   }
   
   private Configuration configureEnvironment(String serverId, final File oteBuilderRootFolder, final SubProgressMonitor monitor, final ConfigurationStatusCallback configurationStatusCallback) {
      HeadlessClassServer classServer = null;
      Configuration localConfiguration = new Configuration();
      try{
         List<File> bundlesToSend = new ArrayList<>();
         bundlesToSend.add(oteBuilderRootFolder);
         classServer = new HeadlessClassServer(PortUtil.getInstance().getValidPort(), InetAddress.getLocalHost(), bundlesToSend);
         File[] jars = oteBuilderRootFolder.listFiles(new FilenameFilter(){
            @Override
            public boolean accept(File arg0, String arg1) {
               return arg1.endsWith(".jar");
            }
         });
         for (File jar : jars) {
            try{
               ConfigurationItem item = new ConfigurationItem(classServer.getHostName() + jar.getName(), "1.0", jar.getName(), Lib.fileToString(new File(jar.getAbsolutePath() + ".md5")), false);
               localConfiguration.addItem(item);
            } catch (IOException ex) {
               OseeLog.log(OTETestEnvironmentClient.class, Level.SEVERE, ex);
            }
         }
         
         if(service != null){
            InetSocketAddress address = service.getLocalEndpoint();
            ConfigurationAndResponse configAndResponse = new ConfigurationAndResponse(address.getAddress().getHostAddress(), address.getPort(), localConfiguration, false, serverId);
            SerializedConfigurationAndResponse ser = new SerializedConfigurationAndResponse();
            ser.setObject(configAndResponse);
            ser.getHeader().RESPONSE_TOPIC.setValue(SerializedOTEJobStatus.EVENT);

            OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
            WaitForCompletion completion = new WaitForCompletion(monitor);
            final OteEventMessageFuture<SerializedConfigurationAndResponse, SerializedOTEJobStatus> asynchSendAndResponse = sendit.asynchSendAndMultipleResponse(SerializedOTEJobStatus.class, ser, completion,  1000 * 60 * 10);
            Thread th = new Thread(new Runnable(){
               @Override
               public void run() {
                  while(true){
                     if(monitor.isCanceled()){
                        asynchSendAndResponse.complete();
                        return;
                     } else if (asynchSendAndResponse.isDone()){
                        return;
                     } else {
                        try {
                           Thread.sleep(2000);
                        } catch (InterruptedException e) {
                        }
                     }
                  }
               }
            });
            th.setDaemon(true);
            th.start();
            asynchSendAndResponse.waitForCompletion();
            if(!asynchSendAndResponse.isTimedOut() && completion.getStatus() != null){
               if(completion.getStatus().isSuccess()){
                  configurationStatusCallback.success();
               } else {
                  String errorLog = completion.getStatus().getErrorLog();
                  if(errorLog.length() > 0){
                     configurationStatusCallback.failure(errorLog);
                  } else {//no diff report, must be the same version
                     configurationStatusCallback.success();
                  }
               }
            }
         }
      } catch (Throwable th){
         th.printStackTrace();
      } finally {
         if(classServer != null){
            classServer.stop();
         }
      }
      return localConfiguration;
   }
   
   private static class EventRunTestsHandle implements ICommandHandle {

      private String guid;
      private OteSendEventMessage sendit;
      
      public EventRunTestsHandle(String guid, OteSendEventMessage sendit){
         this.guid = guid;
         this.sendit = sendit;
      }
      
      @Override
      public boolean cancelAll(boolean mayInterruptIfRunning) {
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsCancel cancel = new RunTestsCancel();
         cancel.CANCEL_ALL.setValue(true);
         cancel.GUID.setValue(guid);
         booleanResponse = sendit.synchSendAndResponse(booleanResponse, cancel, 20000);
         return booleanResponse.VALUE.getValue();
      }

      @Override
      public boolean cancelSingle(boolean mayInterruptIfRunning) {
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsCancel cancel = new RunTestsCancel();
         cancel.CANCEL_ALL.setValue(false);
         cancel.GUID.setValue(guid);
         booleanResponse = sendit.synchSendAndResponse(booleanResponse, cancel, 20000);
         return booleanResponse.VALUE.getValue();
      }

      @Override
      public ITestCommandResult get() {
         RunTestsGetCommandResultReq req = new RunTestsGetCommandResultReq();
         RunTestsGetCommandResultResp resp = new RunTestsGetCommandResultResp();
         req.GUID.setValue(guid);
         resp = sendit.synchSendAndResponse(resp, req, 30000);
         ITestCommandResult result = null;
         try {
            result = resp.getObject();
         } catch (IOException e) {
            e.printStackTrace();
         } catch (ClassNotFoundException e) {
            e.printStackTrace();
         }
         return result;
      }

      @Override
      public boolean isCancelled() {
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsIsCancelled req = new RunTestsIsCancelled();
         req.GUID.setValue(guid);
         booleanResponse = sendit.synchSendAndResponse(booleanResponse, req, 20000);
         return booleanResponse.VALUE.getValue();
      }

      @Override
      public boolean isDone() {
         BooleanResponse booleanResponse = new BooleanResponse();
         RunTestsIsDone req = new RunTestsIsDone();
         req.GUID.setValue(guid);
         booleanResponse = sendit.synchSendAndResponse(booleanResponse, req, 20000);
         return booleanResponse.VALUE.getValue();
      }      

      @Override
      public String getCommandKey() {
         return guid;
      }
      
   }
   
   public EnhancedProperties getProperties(long timeout) {
      SerializedEnhancedProperties props = new SerializedEnhancedProperties();
      RequestHostEnvironmentProperties req = new RequestHostEnvironmentProperties();
      OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
      props = sendit.synchSendAndResponse(props, req, timeout);
      EnhancedProperties returnVal = null;
      try {
         if(props != null){
            returnVal = props.getObject();
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return returnVal;
   }
   
   public ConnectionRequestResult requestEnvironment(IRemoteUserSession session, UUID id, TestEnvironmentConfig config) throws RemoteException {
      Thread.currentThread().setContextClassLoader(new ExportClassLoader());
      try {
         SerializedRequestRemoteTestEnvironment req = new SerializedRequestRemoteTestEnvironment(new RequestRemoteTestEnvironment(session, id, config));
         SerializedConnectionRequestResult result = new SerializedConnectionRequestResult();
         OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
         result = sendit.synchSendAndResponse(result, req, OtePropertiesCore.serverConnectionTimeout.getLongValue(1000 * 10));
         if(result != null){
            return result.getObject();
         }
      } catch (IOException e) {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
         e.printStackTrace();
      }
      return null;
   }
   
   public void disconnect(UUID sessionId) throws RemoteException {
      try {
         SerializedDisconnectRemoteTestEnvironment req = new SerializedDisconnectRemoteTestEnvironment(new DisconnectRemoteTestEnvironment(sessionId));
         OteEndpointSendEventMessage sendit = new OteEndpointSendEventMessage(service, destinationAddress);
         sendit.asynchSend(req);
      } catch (IOException e) {
         e.printStackTrace();
      } 
   }
   
   

}
