/**
 * <copyright> Copyright (c) Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This
 * program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html </copyright>
 */
package org.eclipse.osee.framework.authentication.ldap.core;

/**
 * @author Swapna
 */
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {

	/**
	 * plug-in ID
	 */
	public static final String PLUGIN_ID = "org.eclipse.osee.framework.authentication.ldap.core"; //$NON-NLS-1$

	// The Bundle context instance
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		// Default constructor
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		Activator.context = null;
	}

}
