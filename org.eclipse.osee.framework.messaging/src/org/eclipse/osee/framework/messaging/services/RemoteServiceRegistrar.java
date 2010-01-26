/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import java.net.URI;
import java.util.concurrent.TimeUnit;

/**
 * @author b1528444
 *
 */
public interface RemoteServiceRegistrar {
	void registerService(String serviceId, String serviceVersion, URI broker, ServiceInfoPopulator infoPopulator, long period,
            TimeUnit unit);
	boolean unregisterService(String serviceId, String serviceVersion);
}
