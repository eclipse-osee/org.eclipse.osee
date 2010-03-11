/*
 * Created on Feb 4, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.service;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
 *
 */
class RemoteLookupServiceTracker extends ServiceTracker {
	private MessageService messageService;
	private IConnectionService connectionService;
	private ExportClassLoader exportClassLoader;
	private OteJmsServiceConnector oteJmsServiceConnector;
	
	RemoteLookupServiceTracker(BundleContext context,
			MessageService messageService,
			IConnectionService connectionService,
			ExportClassLoader exportClassLoader) {
		super(context, RemoteServiceLookup.class.getName(),null);
		this.messageService = messageService;
		this.connectionService = connectionService;
		this.exportClassLoader = exportClassLoader;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		RemoteServiceLookup remoteServiceLookup = (RemoteServiceLookup)context.getService(reference);
		oteJmsServiceConnector = new OteJmsServiceConnector(remoteServiceLookup, messageService, connectionService, exportClassLoader);
		oteJmsServiceConnector.start();
		return super.addingService(reference);
	}

	@Override
	public void close() {
		if (oteJmsServiceConnector != null) {
			oteJmsServiceConnector.stop();
			oteJmsServiceConnector = null;
		}
		super.close();
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		if (oteJmsServiceConnector != null) {
			oteJmsServiceConnector.stop();
		}
		super.removedService(reference, service);
	}

}
