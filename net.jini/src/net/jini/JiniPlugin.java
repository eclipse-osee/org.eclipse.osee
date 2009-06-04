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
package net.jini;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The main plug-in class to be used in the desktop.
 */
public class JiniPlugin implements BundleActivator {

	private static JiniPlugin plugin;
	private String[] serviceGroups;
	private BundleContext context;

	public static JiniPlugin getInstance() {
		return plugin;
	}

	public String[] getJiniVersion() {
		Bundle bundle = context.getBundle();
		serviceGroups = new String[1];
		serviceGroups[0] = (String)bundle.getHeaders().get("Bundle-Version");
		return serviceGroups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		plugin = this;
		this.context = context;
		serviceGroups = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		plugin = null;
		this.context = null;
	}
}
