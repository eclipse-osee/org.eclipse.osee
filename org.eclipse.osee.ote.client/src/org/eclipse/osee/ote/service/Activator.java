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
import org.eclipse.osee.ote.service.core.ConnectionServiceTracker;
import org.eclipse.osee.ote.service.core.OteClientEndpointReceive;
import org.eclipse.osee.ote.service.core.OteClientEndpointSend;
import org.eclipse.osee.ote.service.core.TestClientServiceImpl;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private ConnectionServiceTracker connectionServiceTracker;
	
	public void start(BundleContext context) throws Exception {

	      
      connectionServiceTracker = new ConnectionServiceTracker(context);
      connectionServiceTracker.open(true);


	}

	public void stop(BundleContext context) throws Exception {

		// close the service tracker
		connectionServiceTracker.close();
		connectionServiceTracker = null;

	}

	public static void log(Level level, String message, Throwable t) {
		OseeLog.log(Activator.class, level, message, t);
	}

	public static void log(Level level, String message) {
		log(level, message, null);
	}
}
