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
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class MessageingServiceToLookupTracker extends ServiceTracker {

	private IConnectionService connectionService;
	private ExportClassLoader exportClassLoader;
	private MessageService messageService;
	private RemoteLookupServiceTracker remoteLookupServiceTracker;
	/**
	 * @param context
	 * @param connectionService
	 * @param exportClassLoader
	 */
	MessageingServiceToLookupTracker(BundleContext context,
			IConnectionService connectionService, ExportClassLoader exportClassLoader) {
		super(context, MessageService.class.getName(), null);
		this.connectionService = connectionService;
		this.exportClassLoader = exportClassLoader;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		messageService = (MessageService)context.getService(reference);
		remoteLookupServiceTracker = new RemoteLookupServiceTracker(context, messageService, connectionService, exportClassLoader);
		remoteLookupServiceTracker.open(true);
		return super.addingService(reference);
	}

	@Override
	public void close() {
		remoteLookupServiceTracker.close();
		super.close();
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		remoteLookupServiceTracker.close();
		super.removedService(reference, service);
	}

}
