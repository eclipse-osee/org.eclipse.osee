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

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
class ExportClassLoaderCreationTracker extends ServiceTracker {

	private ExportClassLoader exportClassLoader;
	private JmsJiniBridgeConnectionServiceTracker jmsJiniBridgeConnectionServiceTracker;

	ExportClassLoaderCreationTracker(BundleContext context){
		super(context, PackageAdmin.class.getName(), null);
	}

	@Override
	public Object addingService(ServiceReference reference) {
		PackageAdmin pa = (PackageAdmin) context.getService(reference);
		exportClassLoader = new ExportClassLoader(pa);
		jmsJiniBridgeConnectionServiceTracker = new JmsJiniBridgeConnectionServiceTracker(context, exportClassLoader);
		jmsJiniBridgeConnectionServiceTracker.open(true);
		return super.addingService(reference);
	}

	@Override
	public void close() {
		jmsJiniBridgeConnectionServiceTracker.close();
		super.close();
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		jmsJiniBridgeConnectionServiceTracker.close();
		super.removedService(reference, service);
	}
	
	
}
