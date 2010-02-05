/*
 * Created on Apr 21, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.ui.view;

import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Ken J. Aguilar
 *
 */
public class ClientMessageServiceTracker extends ServiceTracker {

	private final IOteMessageClientView viewer;
	
	/**
	 * @param context
	 * @param filter
	 * @param customizer
	 */
	public ClientMessageServiceTracker(BundleContext context, IOteMessageClientView viewer) {
		super(context, IOteMessageService.class.getName(), null);
		this.viewer = viewer;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#addingService(org.osgi.framework.ServiceReference)
	 */
	@Override
	public synchronized Object addingService(ServiceReference reference) {
		IOteMessageService service = (IOteMessageService)super.addingService(reference);
		try {
			viewer.oteMessageServiceAcquired(service);
		} catch (RuntimeException e) {
			OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service", e);
		}
		return service;
	}

	/* (non-Javadoc)
	 * @see org.osgi.util.tracker.ServiceTracker#removedService(org.osgi.framework.ServiceReference, java.lang.Object)
	 */
	@Override
	public synchronized void removedService(ServiceReference reference, Object service) {
		try {
			viewer.oteMessageServiceReleased();
		} catch (RuntimeException e) {
			OseeLog.log(ClientMessageServiceTracker.class, Level.SEVERE, "exception while notifying viewer of service stop", e);
		} finally {
			super.removedService(reference, service);
		}
		
	}	
}
