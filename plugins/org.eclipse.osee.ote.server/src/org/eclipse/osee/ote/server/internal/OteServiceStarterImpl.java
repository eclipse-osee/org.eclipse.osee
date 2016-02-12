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

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.endpoint.OteUdpEndpoint;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServer;
import org.eclipse.osee.ote.master.rest.client.OTEMasterServerResult;
import org.eclipse.osee.ote.master.rest.model.OTEServer;
import org.eclipse.osee.ote.properties.OtePropertiesCore;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServiceStarterImpl implements OteServiceStarter {
   
//	private PackageAdmin packageAdmin;
	private IRuntimeLibraryManager runtimeLibraryManager;
	private IConnectionService connectionService;
//	private MessageService messageService;

	private BrokerService brokerService;
	private OteService service;
//	private final ListenForHostRequest listenForHostRequest;

	private IServiceConnector serviceSideConnector;
   private OTESessionManager oteSessions;
   private OTEMasterServer masterServer;

   private ScheduledExecutorService executor;
   private OTEServer oteServerEntry;
   private ScheduledFuture<?> taskToCancel;
   private LookupRegistration lookupRegistration;
   private URI masterURI;
//   private NodeInfo nodeInfo;
//   private int brokerPort = 0;
   private OteUdpEndpoint receiver;
//   private boolean ENABLE_BROKER;
   
   public OteServiceStarterImpl() {
//      listenForHostRequest = new ListenForHostRequest();
      executor = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(){
         
         @Override
         public Thread newThread(Runnable arg0) {
            Thread th = new Thread(arg0);
            th.setName("OTE Lookup Registration");
            th.setDaemon(true);
            return th;
         }
         
      });
   }

   public void bindOTESessionManager(OTESessionManager oteSessions){
      this.oteSessions = oteSessions;
   }
   
   public void unbindOTESessionManager(OTESessionManager oteSessions){
      this.oteSessions = null;
   } 
   
	public void bindIRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
	   this.runtimeLibraryManager = runtimeLibraryManager;
	}
	
	public void unbindIRuntimeLibraryManager(IRuntimeLibraryManager runtimeLibraryManager){
      this.runtimeLibraryManager = null;
   } 
	
//	public void bindMessageService(MessageService messageService){
//	   this.messageService = messageService;
//	}
//
//	public void unbindMessageService(MessageService messageService){
//	   this.messageService = null;
//	}
	
	public void bindIConnectionService(IConnectionService connectionService){
	   this.connectionService = connectionService;
	}
	
	public void unbindIConnectionService(IConnectionService connectionService){
	   this.connectionService = null;
	}
	
	public void bindOTEMasterServer(OTEMasterServer masterServer){
	   this.masterServer = masterServer;
	}
	
	public void unbindOTEMasterServer(OTEMasterServer masterServer){
      this.masterServer = null;
   }
	
//	public void bindPackageAdmin(PackageAdmin packageAdmin){
//	   this.packageAdmin = packageAdmin;
//	}
//	
//	public void unbindPackageAdmin(PackageAdmin packageAdmin){
//      this.packageAdmin = null;
//   }
	
	public void bindOteUdpEndpoint(OteUdpEndpoint receiver){
	   this.receiver = receiver;
	}
	
	public void unbindOteUdpEndpoint(OteUdpEndpoint receiver){
	   this.receiver = receiver;
	}
	
	@Override
	public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, String environmentFactoryClass) throws Exception {
		return start(serviceSideConnector, config, propertyParameter, null, environmentFactoryClass);
	}

	@Override
	public IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory) throws Exception {
		return start(serviceSideConnector, config, propertyParameter, factory, null);
	}

	private IHostTestEnvironment start(IServiceConnector serviceSideConnector, ITestEnvironmentServiceConfig config, PropertyParamter propertyParameter, TestEnvironmentFactory factory, String environmentFactoryClass) throws Exception {
		if (service != null) {
			throw new OseeStateException("An ote Server has already been started.");
		}
		this.serviceSideConnector = serviceSideConnector;
//		ENABLE_BROKER = Boolean.parseBoolean(System.getProperty("ote.enable.broker", "true"));
//		if(ENABLE_BROKER){
////		   brokerService = new BrokerService();
//
//		   String strUri;
//		   try {
//		      String addressAsString = getAddress();
//		      if(brokerPort <= 0){
//		         brokerPort = getServerPort();
//		      }
//		      strUri = String.format("tcp://%s:%d", addressAsString, brokerPort);
//		      try {
//		         brokerService.addConnector(strUri);
//		         OseeLog.log(getClass(), Level.INFO, "Added TCP connector: " + strUri);			
//		      } catch (Exception e) {
//		         OseeLog.log(getClass(), Level.SEVERE, "could not add connector for " + strUri, e);
//		         strUri = "vm://localhost?broker.persistent=false";
//		      }
//		   } catch (Exception e) {
//		      OseeLog.log(getClass(), Level.SEVERE, "could acquire a TCP address", e);
//		      strUri = "vm://localhost?broker.persistent=false";
//		   }
//		   //necessary for rmi/jini classloading
//		   Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
//
//		   brokerService.setEnableStatistics(false);
//		   brokerService.setBrokerName("OTEServer");
//		   brokerService.setPersistent(false);
//		   brokerService.setUseJmx(false);
//		   brokerService.start();
//		   URI uri = new URI(strUri);
//
////		   nodeInfo = new NodeInfo("OTEEmbeddedBroker", uri);
//		} else {
////		   URI uri = new URI(String.format("tcp://%s:%d", "nohost", 0));
////		   nodeInfo = new NodeInfo("OTEEmbeddedBroker", uri);
//		}
		OteUdpEndpoint oteEndpoint = ServiceUtility.getService(OteUdpEndpoint.class);
		System.out.printf("SERVER CONNECTION URI[\n\ttcp://%s:%d\n]\n", oteEndpoint.getLocalEndpoint().getAddress().getHostAddress(), oteEndpoint.getLocalEndpoint().getPort());

		EnvironmentCreationParameter environmentCreationParameter =
				new EnvironmentCreationParameter(runtimeLibraryManager, serviceSideConnector, config, factory,
						environmentFactoryClass);

		service =
				new OteService(environmentCreationParameter, oteSessions, propertyParameter,
						serviceSideConnector.getProperties(), receiver);

		serviceSideConnector.init(service);

		
		
		if (propertyParameter.isLocalConnector()) {
			connectionService.addConnector(serviceSideConnector);
		}
		if (!propertyParameter.isLocalConnector()) {
			String masterURIStr = OtePropertiesCore.masterURI.getValue();
			if(masterURIStr != null){
			   try{
			      masterURI = new URI(masterURIStr);
			      oteServerEntry = createOTEServer(environmentCreationParameter, propertyParameter, service.getServiceID().toString());
			      lookupRegistration = new LookupRegistration(masterURI, masterServer, oteServerEntry, service);
			      taskToCancel = executor.scheduleWithFixedDelay(lookupRegistration, 0, 30, TimeUnit.SECONDS);
			   } catch(Throwable th){
			      OseeLog.log(getClass(), Level.SEVERE, th);
			   }
			} else {
				OseeLog.log(getClass(), Level.WARNING, "'ote.master.uri' was not set.  You must use direct connect from the client.");
			}
			
		} else {
//			serviceSideConnector.setProperty("OTEEmbeddedBroker", nodeInfo);
		}
		
		FrameworkUtil.getBundle(getClass()).getBundleContext().registerService(IHostTestEnvironment.class, service, null);
		System.out.printf("TEST SERVER INITIALIZATION COMPLETE\n");

		return service;
	}
	
	private OTEServer createOTEServer(EnvironmentCreationParameter environmentCreationParameter, PropertyParamter propertyParameter, String uuid) throws NumberFormatException, UnknownHostException{
	   OTEServer server = new OTEServer();
	   server.setName(environmentCreationParameter.getServerTitle().toString());
	   server.setStation(propertyParameter.getStation());
	   server.setVersion(propertyParameter.getVersion());
	   server.setType(propertyParameter.getType());
	   server.setComment(propertyParameter.getComment());
	   server.setStartTime(new Date().toString());
	   server.setOwner(OtePropertiesCore.userName.getValue());
	   server.setUUID(uuid);
	   server.setOteRestServer(String.format("tcp://%s:%d", receiver.getLocalEndpoint().getAddress().getHostAddress(), receiver.getLocalEndpoint().getPort()));
	   server.setOteActivemqServer(String.format("tcp://%s:%d", receiver.getLocalEndpoint().getAddress().getHostAddress(), receiver.getLocalEndpoint().getPort()));
	   return server;
	}

//	private int getServerPort() throws IOException {
//		String portFromLaunch = OtePropertiesCore.brokerUriPort.getValue();
//		int port = 0;
//		if (portFromLaunch != null) {
//			try {
//				port = Integer.parseInt(portFromLaunch);
//			} catch (NumberFormatException ex) {
//			}
//		}
//		if (port == 0) {
//			port = PortUtil.getInstance().getValidPort();
//		}
//		return port;
//	}

	@Override
	public void stop() {
//	   if(messageService != null && nodeInfo != null && service != null){
//	      try {
//            messageService.get(nodeInfo).send(OteBaseMessages.OteHostShutdown, service.getServiceID().toString());
//         } catch (OseeCoreException e) {
//            OseeLog.log(getClass(), Level.SEVERE, e);
//         } catch (RemoteException e) {
//            OseeLog.log(getClass(), Level.SEVERE, e);
//         }
//	   }
		if (service != null) {
			try {
				service.updateDynamicInfo();
				service.kill();
				service = null;
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if (brokerService != null) {
			try {
				brokerService.stopGracefully(".*", ".*", 10000, 500);
				brokerService.stop();
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if (serviceSideConnector != null) {
			try {
				connectionService.removeConnector(serviceSideConnector);
			} catch (Exception ex) {
				OseeLog.log(getClass(), Level.SEVERE, ex);
			}
		}
		if(oteServerEntry != null) {
		   try{
		      lookupRegistration.stop();
		      taskToCancel.cancel(true);
		   } finally {
		      Future<OTEMasterServerResult> removeServer = masterServer.removeServer(masterURI, oteServerEntry);
		      try {
               removeServer.get(1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            } catch (ExecutionException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            } catch (TimeoutException e) {
               OseeLog.log(getClass(), Level.INFO, e);
            }
		   }
		}
		brokerService = null;
	}

//	private String getAddress() throws UnknownHostException {
//		InetAddress[] all = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
//		String defaultAddress = all[0].getHostAddress();
//		for (InetAddress address : all ) {
//		   if(!address.isSiteLocalAddress())
//		   {
//		      String firstRealLocalAddress = address.getHostAddress();
//		      if (address instanceof Inet6Address) {
//		         firstRealLocalAddress = "[" + firstRealLocalAddress + "]";
//		      }
//		      return firstRealLocalAddress;
//		   }
//		}
//		return defaultAddress;
//	}

//	private class ListenForHostRequest extends OseeMessagingListener {
//
//		@Override
//		public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
//			if (replyConnection.isReplyRequested()) {
//				try {
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					ObjectOutputStream oos = new ObjectOutputStream(baos);
//					oos.writeObject(message);
//					oos.writeObject(serviceSideConnector.getService());
//					replyConnection.send(baos.toByteArray(), null, OteServiceStarterImpl.this);
//				} catch (OseeCoreException ex) {
//					OseeLog.log(getClass(), Level.SEVERE, ex);
//				} catch (IOException ex) {
//					OseeLog.log(getClass(), Level.SEVERE, ex);
//				}
//			}
//		}
//	}
//
//	@Override
//	public void updateServiceInfo(List<ServiceDescriptionPair> serviceDescription) {
//		for (Entry<String, Serializable> entry : serviceSideConnector.getProperties().entrySet()) {
//			ServiceDescriptionPair pair = new ServiceDescriptionPair();
//			if (entry.getKey() != null && entry.getValue() != null) {
//				pair.setName(entry.getKey());
//				pair.setValue(entry.getValue().toString());
//				serviceDescription.add(pair);
//			}
//		}
//	}
//
//	@Override
//	public void fail(Throwable th) {
//		OseeLog.log(getClass(), Level.SEVERE, th);
//	}
//
//	@Override
//	public void success() {
//	}
//	
	private static class LookupRegistration implements Runnable {

      private final OTEMasterServer masterServer;
      private final OTEServer server;
      private final URI uri;
      private volatile boolean run = true;
      private final OteService service;

      public LookupRegistration(URI uri, OTEMasterServer masterServer, OTEServer server, OteService service) {
         this.masterServer = masterServer;
         this.server = server;
         this.uri = uri;
         this.service = service;
      }

      @Override
      public void run() {
         try{
            if(run){
               server.setConnectedUsers(service.getProperties().getProperty("user_list", "N.A.").toString());
               Future<OTEMasterServerResult> result = masterServer.addServer(uri, server);
               OTEMasterServerResult addServerResult = result.get(30, TimeUnit.SECONDS);
               if(!(addServerResult != null && addServerResult.isSuccess())){
                  try{
                     Thread.sleep(1000*60*3);//wait 3 minutes before trying again
                  } catch(Throwable th){
                     //don't care if we're woken up
                  }
               }
            }
         } catch (Throwable th){
            th.printStackTrace();
         }
      }
      
      public void stop(){
         run = false;
      }
      
	}

}
