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

import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.service.core.ConnectionServiceTracker;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ServiceRegistration registration;
   private ConnectionServiceTracker connectionServiceTracker;
	private MessagingGatewayBindTracker messagingGatewayTracker;
	private ExportClassLoaderCreationTracker exportClassLoaderCreationTracker;
	
	
	public void start(BundleContext context) throws Exception {
      connectionServiceTracker = new ConnectionServiceTracker(context);
      connectionServiceTracker.open(true);
   
      exportClassLoaderCreationTracker = new ExportClassLoaderCreationTracker(context);
      exportClassLoaderCreationTracker.open(true);
	}

	public void stop(BundleContext context) throws Exception {

		// close the service tracker
		connectionServiceTracker.close();
		exportClassLoaderCreationTracker.close();
		connectionServiceTracker = null;

	}

	public static void log(Level level, String message, Throwable t) {
		OseeLog.log(Activator.class, level, message, t);
	}

	public static void log(Level level, String message) {
		log(level, message, null);
	}
}
