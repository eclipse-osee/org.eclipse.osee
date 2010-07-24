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
package org.eclipse.osee.ote.client.msg.core.internal;

import java.io.IOException;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageDbFactory;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class OteClientServiceTracker extends ServiceTracker{

	private final IMessageDbFactory factory;
	
	private MessageSubscriptionService messageSubscriptionService;

	private ServiceRegistration registration;
	
	OteClientServiceTracker(IMessageDbFactory factory) {
		super(Activator.getDefault().getBundleContext(), IOteClientService.class.getName(), null);
		this.factory = factory;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		IOteClientService service = (IOteClientService) super.addingService(reference);
		try {
			messageSubscriptionService = new MessageSubscriptionService(service, factory);
			registration = context.registerService(IOteMessageService.class.getName(), messageSubscriptionService, null);
		} catch (IOException e) {
			OseeLog.log(OteClientServiceTracker.class, Level.SEVERE, "could not start Message Service", e);
		}
		return service;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		shutdownMessageService();
		super.removedService(reference, service);
	}
	
	private void shutdownMessageService() {
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
		if (messageSubscriptionService != null) {
			messageSubscriptionService.shutdown();
			messageSubscriptionService = null;
		}
	}

	@Override
	public void close() {
		shutdownMessageService();
		super.close();
	}
	
	
}
