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
