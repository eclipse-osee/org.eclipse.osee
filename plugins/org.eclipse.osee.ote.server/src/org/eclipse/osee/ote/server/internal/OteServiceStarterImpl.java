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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.apache.activemq.broker.BrokerService;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.network.PortUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.NodeInfo;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.RegisteredServiceReference;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.eclipse.osee.framework.messaging.services.ServiceInfoPopulator;
import org.eclipse.osee.framework.messaging.services.messages.ServiceDescriptionPair;
import org.eclipse.osee.ote.core.OTESessionManager;
import org.eclipse.osee.ote.core.OteBaseMessages;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentServiceConfig;
import org.eclipse.osee.ote.server.OteServiceStarter;
import org.eclipse.osee.ote.server.PropertyParamter;
import org.eclipse.osee.ote.server.TestEnvironmentFactory;
import org.osgi.service.packageadmin.PackageAdmin;

/**
 * @author Andrew M. Finkbeiner
 */
public class OteServiceStarterImpl implements OteServiceStarter, ServiceInfoPopulator, OseeMessagingStatusCallback {

	private PackageAdmin packageAdmin;
	private IRuntimeLibraryManager runtimeLibraryManager;
	private IConnectionService connectionService;
	private RemoteServiceRegistrar remoteServiceRegistrar;
	private MessageService messageService;

	private BrokerService brokerService;
	private OteService service;
	private final ListenForHostRequest listenForHostRequest;

	private IServiceConnector serviceSideConnector;
   private OTESessionManager oteSessions;

	
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
	
	public void bindRemoteServiceRegistrar(RemoteServiceRegistrar remoteServiceRegistrar){
	   this.remoteServiceRegistrar = remoteServiceRegistrar;
	}
	
	public void unbindRemoteServiceRegistrar(RemoteServiceRegistrar remoteServiceRegistrar){
      this.remoteServiceRegistrar = null;
   }
	
	public void bindMessageService(MessageService messageService){
	   this.messageService = messageService;
	}

	public void unbindMessageService(MessageService messageService){
	   this.messageService = null;
	}
	
	public void bindIConnectionService(IConnectionService connectionService){
	   this.connectionService = connectionService;
	}
	
	public void unbindIConnectionService(IConnectionService connectionService){
	   this.connectionService = null;
	}
	   
	public void bindPackageAdmin(PackageAdmin packageAdmin){
	   this.packageAdmin = packageAdmin;
	}
	
	public void unbindPackageAdmin(PackageAdmin packageAdmin){
      this.packageAdmin = null;
   }
	
	OteServiceStarterImpl() {
		listenForHostRequest = new ListenForHostRequest();
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
		brokerService = new BrokerService();

		String strUri;
		try {
			String addressAsString = getAddress();
			int port = getServerPort();
			strUri = String.format("tcp://%s:%d", addressAsString, port);
			try {
				brokerService.addConnector(strUri);
				OseeLog.log(Activator.class, Level.INFO, "Added TCP connector: " + strUri);			
			} catch (Exception e) {
				OseeLog.log(Activator.class, Level.SEVERE, "could not add connector for " + strUri, e);
				strUri = "vm://localhost?broker.persistent=false";
			}
		} catch (Exception e) {
			OseeLog.log(Activator.class, Level.SEVERE, "could acquire a TCP address", e);
			strUri = "vm://localhost?broker.persistent=false";
		}
		
		brokerService.setEnableStatistics(false);
		brokerService.setBrokerName("OTEServer");
		brokerService.setPersistent(false);
		brokerService.setUseJmx(false);
		brokerService.start();
		URI uri = new URI(strUri);

		NodeInfo nodeInfo = new NodeInfo("OTEEmbeddedBroker", uri);

		EnvironmentCreationParameter environmentCreationParameter =
				new EnvironmentCreationParameter(runtimeLibraryManager, nodeInfo, serviceSideConnector, config, factory,
						environmentFactoryClass, packageAdmin);

		service =
				new OteService(runtimeLibraryManager, environmentCreationParameter, oteSessions, propertyParameter,
						serviceSideConnector.getProperties());

		serviceSideConnector.init(service);

		if (propertyParameter.isLocalConnector() || propertyParameter.useJiniLookup()) {
			connectionService.addConnector(serviceSideConnector);
		}
		if (!propertyParameter.isLocalConnector()) {
			messageService.get(nodeInfo).subscribe(OteBaseMessages.RequestOteHost, listenForHostRequest, this);
			RegisteredServiceReference ref = remoteServiceRegistrar.registerService("osee.ote.server", "1.0", service.getServiceID().toString(), uri, this, 60 * 3);
			service.set(ref);
		} else {
			serviceSideConnector.setProperty("OTEEmbeddedBroker", nodeInfo);
		}
		
		Activator.getDefault().getContext().registerService(IHostTestEnvironment.class, service, null);
		
		return service;
	}

	private int getServerPort() throws IOException {
		String portFromLaunch = System.getProperty("ote.server.broker.uri.port");
		int port = 0;
		if (portFromLaunch != null) {
			try {
				port = Integer.parseInt(portFromLaunch);
			} catch (NumberFormatException ex) {
			}
		}
		if (port == 0) {
			port = PortUtil.getInstance().getValidPort();
		}
		return port;
	}

	@Override
	public void stop() {
		if (service != null) {
			try {
				service.updateDynamicInfo();
				remoteServiceRegistrar.unregisterService("osee.ote.server", "1.0", service.getServiceID().toString());
			} catch (RemoteException ex) {
				OseeLog.log(Activator.class, Level.SEVERE, ex);
			}
		}
		if (brokerService != null) {
			try {
				brokerService.stop();
			} catch (Exception ex) {
				OseeLog.log(Activator.class, Level.SEVERE, ex);
			}
		}
		if (serviceSideConnector != null) {
			try {
				connectionService.removeConnector(serviceSideConnector);
			} catch (Exception ex) {
				OseeLog.log(Activator.class, Level.SEVERE, ex);
			}
		}
		service = null;
		brokerService = null;
	}

	private String getAddress() throws UnknownHostException {
		InetAddress[] all = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
		String defaultAddress = all[0].getHostAddress();
		for (InetAddress address : all ) {
		   if(!address.isSiteLocalAddress())
		   {
		      String firstRealLocalAddress = address.getHostAddress();
		      if (address instanceof Inet6Address) {
		         firstRealLocalAddress = "[" + firstRealLocalAddress + "]";
		      }
		      return firstRealLocalAddress;
		   }
		}
		return defaultAddress;
	}

	private class ListenForHostRequest extends OseeMessagingListener {

		@Override
		public void process(Object message, Map<String, Object> headers, ReplyConnection replyConnection) {
			if (replyConnection.isReplyRequested()) {
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(message);
					oos.writeObject(serviceSideConnector.getService());
					replyConnection.send(baos.toByteArray(), null, OteServiceStarterImpl.this);
				} catch (OseeCoreException ex) {
					OseeLog.log(Activator.class, Level.SEVERE, ex);
				} catch (IOException ex) {
					OseeLog.log(Activator.class, Level.SEVERE, ex);
				}
			}
		}
	}

	@Override
	public void updateServiceInfo(List<ServiceDescriptionPair> serviceDescription) {
		for (Entry<String, Serializable> entry : serviceSideConnector.getProperties().entrySet()) {
			ServiceDescriptionPair pair = new ServiceDescriptionPair();
			if (entry.getKey() != null && entry.getValue() != null) {
				pair.setName(entry.getKey());
				pair.setValue(entry.getValue().toString());
				serviceDescription.add(pair);
			}
		}
	}

	@Override
	public void fail(Throwable th) {
		OseeLog.log(Activator.class, Level.SEVERE, th);
	}

	@Override
	public void success() {
	}

}
