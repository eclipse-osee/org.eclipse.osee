package org.eclipse.osee.ote.service.core;

import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.IOteRuntimeLibraryProvider;
import org.eclipse.osee.ote.service.MessagingGatewayBindTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class ConnectionServiceTracker extends ServiceTracker{

	private final OteClientEndpointReceive endpointReceive = new OteClientEndpointReceive();
	private final OteClientEndpointSend endpointSend = new OteClientEndpointSend();
	private final MessagingGatewayBindTracker messagingGatewayTracker;

	private ServiceRegistration registration;
	private TestClientServiceImpl testClientService;

	public ConnectionServiceTracker(BundleContext context) {
		super(context, IConnectionService.class.getName(), null);
		messagingGatewayTracker = new MessagingGatewayBindTracker(context, endpointSend, endpointReceive);
		messagingGatewayTracker.open(true);
	}


	@Override
	public Object addingService(ServiceReference reference) {		
		IConnectionService connectionService = (IConnectionService)  super.addingService(reference);
		testClientService = new TestClientServiceImpl(connectionService, endpointSend, endpointReceive);
		ExtensionDefinedObjects<IOteRuntimeLibraryProvider> definedObjects = new ExtensionDefinedObjects<IOteRuntimeLibraryProvider>(
				"org.eclipse.osee.ote.client.libraryProvidier", "LibraryProvider", "className");
		try {
			List<IOteRuntimeLibraryProvider> providers = definedObjects.getObjects();
			testClientService.addLibraryProvider(providers);
		} catch (Exception ex) {
			OseeLog.log(ConnectionServiceTracker.class, Level.SEVERE, "failed to process OTE runtime library provider extensions", ex);
		}
		testClientService.init();
		// register the service
		registration = context.registerService(IOteClientService.class.getName(), testClientService, null);
		return connectionService;
	}

	private void shutdownClientService() {
		if (testClientService != null) {
			registration.unregister();
			try {
				testClientService.stop();
			} catch (Exception e) {
				OseeLog.log(ConnectionServiceTracker.class, Level.SEVERE, "failed to properly stop OTE client service", e);

			}
			testClientService = null;
		}
	}
	
	@Override
	public void close() {
		shutdownClientService();
		messagingGatewayTracker.close();
		super.close();
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		shutdownClientService();
		super.removedService(reference, service);
	}

}
