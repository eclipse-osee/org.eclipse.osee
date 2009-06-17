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
package org.eclipse.osee.ote.service;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.ote.service.core.OteClientEndpointReceive;
import org.eclipse.osee.ote.service.core.OteClientEndpointSend;
import org.eclipse.osee.ote.service.core.TestClientServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private ServiceRegistration registration;
	private TestClientServiceImpl service;
	private ServiceTracker connectionServiceTracker;
	private MessagingGatewayBindTracker messagingGatewayTracker;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {

      connectionServiceTracker = new ServiceTracker(context, IConnectionService.class.getName(), null);
      connectionServiceTracker.open();

      OteClientEndpointReceive endpointReceive = new OteClientEndpointReceive();
      OteClientEndpointSend endpointSend = new OteClientEndpointSend();
      
      messagingGatewayTracker = new MessagingGatewayBindTracker(context, endpointSend, endpointReceive);
      messagingGatewayTracker.open(true);
      
      IConnectionService connectionService = (IConnectionService) connectionServiceTracker.getService();
      service = new TestClientServiceImpl(connectionService, endpointSend, endpointReceive);
      
      ExtensionDefinedObjects<IOteRuntimeLibraryProvider> definedObjects = new ExtensionDefinedObjects<IOteRuntimeLibraryProvider>(
            "org.eclipse.osee.ote.client.libraryProvidier", "LibraryProvider", "className");
      try {
         List<IOteRuntimeLibraryProvider> providers = definedObjects.getObjects();
         service.addLibraryProvider(providers);
      } catch (Exception ex) {
         log(Level.SEVERE, "failed to process OTE runtime library provider extensions", ex);
      }

      service.init();
      // register the service
      registration = context.registerService(IOteClientService.class.getName(), service, new Hashtable());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		IConnectionService connectionService = (IConnectionService) connectionServiceTracker.getService();
		connectionService.removeListener(service);

		// close the service tracker
		messagingGatewayTracker.close();
		connectionServiceTracker.close();
		connectionServiceTracker = null;

		service.stop();
		service = null;
		registration.unregister();
	}

	public static void log(Level level, String message, Throwable t) {
		OseeLog.log(Activator.class, level, message, t);
	}

	public static void log(Level level, String message) {
		log(level, message, null);
	}
}
