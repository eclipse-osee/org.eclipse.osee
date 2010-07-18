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
package org.eclipse.osee.ote.service;

import org.eclipse.osee.connection.service.IConnectionService;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class JmsJiniBridgeConnectionServiceTracker extends ServiceTracker {

	private IConnectionService connectionService;
	private ExportClassLoader exportClassLoader;
	private MessageingServiceToLookupTracker messageingServiceToLookupTracker;
	
	JmsJiniBridgeConnectionServiceTracker(BundleContext context, ExportClassLoader exportClassLoader){
		super(context, IConnectionService.class.getName(), null);
		this.exportClassLoader = exportClassLoader;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		connectionService = (IConnectionService) context.getService(reference);
		messageingServiceToLookupTracker = new MessageingServiceToLookupTracker(context, connectionService, exportClassLoader);
		messageingServiceToLookupTracker.open(true);
		return super.addingService(reference);
	}

	@Override
	public void close() {
		messageingServiceToLookupTracker.close();
		super.close();
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		messageingServiceToLookupTracker.close();
		super.removedService(reference, service);
	}
}
