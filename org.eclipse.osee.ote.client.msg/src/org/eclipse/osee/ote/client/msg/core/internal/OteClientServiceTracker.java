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

import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

public class OteClientServiceTracker extends ServiceTracker{

	private final MessageSubscriptionService messageSubscriptionService;

	OteClientServiceTracker(MessageSubscriptionService messageSubscriptionService) {
		super(Activator.getDefault().getBundleContext(), IOteClientService.class.getName(), null);
		this.messageSubscriptionService = messageSubscriptionService;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		IOteClientService service = (IOteClientService) super.addingService(reference);
		messageSubscriptionService.oteClientServiceAcquired(service);
		return service;
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		messageSubscriptionService.oteClientServiceLost();
		super.removedService(reference, service);
	}
	
	
}
