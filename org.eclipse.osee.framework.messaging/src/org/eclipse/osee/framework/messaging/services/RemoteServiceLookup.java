/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

/**
 * @author b1528444
 *
 */
public interface RemoteServiceLookup {
	void start();
	void stop();
	void register(String serviceId, String serviceVersion, ServiceNotification notification);
	boolean unregister(String serviceId, String serviceVersion, ServiceNotification notification);
}
