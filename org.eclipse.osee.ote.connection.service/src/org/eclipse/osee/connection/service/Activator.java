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
package org.eclipse.osee.connection.service;

import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ConnectionServiceImpl service;
	private ServiceRegistration registration;

	public void start(BundleContext context) throws Exception {
		service = new ConnectionServiceImpl();

		// register the service
		registration = context.registerService(IConnectionService.class.getName(), service, null);

		// create a tracker and track the service

		ExtensionDefinedObjects<IConnectorContributor> definedObjects = new ExtensionDefinedObjects<IConnectorContributor>(
				"org.eclipse.osee.connection.service.ext", "ConnectorContribution", "className");
		try {
			List<IConnectorContributor> contributors = definedObjects.getObjects();
			for (IConnectorContributor contributor : contributors) {
				try {
					contributor.init();
				} catch (Exception e) {
					log(Level.SEVERE, "exception initializing connector contributor", e);
				}
			}
		} catch (Exception ex) {
			log(Level.SEVERE, "failed to process OTE runtime library provider extensions", ex);
		}
	}

	public void stop(BundleContext context) throws Exception {
		service.stop();
		registration.unregister();
		service = null;
	}

	public static void log(Level level, String message, Throwable t) {
		OseeLog.log(Activator.class, level, message, t);
	}

	public static void log(Level level, String message) {
		log(level, message, null);
	}

}
