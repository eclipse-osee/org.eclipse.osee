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
package org.eclipse.osee.framework.core.dsl.integration.internal;

import org.eclipse.osee.framework.core.util.ServiceDependencyTracker;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.dsl.integration";

	private ServiceDependencyTracker dependencyTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		dependencyTracker = new ServiceDependencyTracker(context, new OseeModelingServiceRegHandler());
		dependencyTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		Lib.close(dependencyTracker);
	}
}
