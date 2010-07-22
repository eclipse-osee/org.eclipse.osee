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
package org.eclipse.osee.framework.access.internal;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.osee.framework.access";

	private static AccessControlServiceRegHandler handler;

	private final Collection<ServiceDependencyTracker> trackers = new ArrayList<ServiceDependencyTracker>();

	@Override
	public void start(BundleContext context) throws Exception {
		handler = new AccessControlServiceRegHandler();
		trackers.add(new ServiceDependencyTracker(context, handler));
		trackers.add(new ServiceDependencyTracker(context, new ObjectAccessProviderRegistrationHandler()));

		for (ServiceDependencyTracker tracker : trackers) {
			tracker.open();
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		for (ServiceDependencyTracker tracker : trackers) {
			Lib.close(tracker);
		}
	}

	// TODO Deprecate later
	public static AccessControlService getAccessControlService() {
		return handler.getService();
	}

}
