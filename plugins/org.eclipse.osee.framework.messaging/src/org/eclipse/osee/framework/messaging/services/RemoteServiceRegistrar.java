/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import java.net.URI;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public interface RemoteServiceRegistrar {
	void start();
	void stop();
	RegisteredServiceReference registerService(String serviceName, String serviceVersion, String serviceUniqueId, URI broker, ServiceInfoPopulator infoPopulator, int refreshRateInSeconds);
	boolean unregisterService(String serviceName, String serviceVersion, String serviceUniqueId);
}
