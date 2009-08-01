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

import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.client.msg.core.IMessageDbFactory;
import org.eclipse.osee.ote.service.IOteClientService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.ote.client.msg";

	// The shared instance
	private static Activator plugin;

	private BundleContext context;
	private ServiceRegistration registration;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.context = context;
		plugin = this;
		
		ExtensionDefinedObjects<IMessageDbFactory> definedObjects = new ExtensionDefinedObjects<IMessageDbFactory>(
				"org.wclipse.see.ote.client.msg.dBFactory", "DatabaseFactory", "className");
		try {
			List<IMessageDbFactory> providers = definedObjects.getObjects();
			if (!providers.isEmpty()) {
				MessageSubscriptionService service = new MessageSubscriptionService(providers.get(0));
				registration = context.registerService(IOteMessageService.class.getName(), service, null);
			} else {
				OseeLog.log(Activator.class, Level.WARNING, "no message db factory found. Message Subscription Service not started");
			}
		} catch (Exception ex) {
			OseeLog.log(Activator.class, Level.SEVERE, "failed to process message database factory extensions", ex);
		}
	


	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (registration != null) {
			MessageSubscriptionService service = (MessageSubscriptionService) context.getService(registration.getReference());
			service.shutdown();
			registration.unregister();
			registration = null;
		}
		super.stop(context);
		this.context = null;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	BundleContext getBundleContext() {
		return context;
	}

}
