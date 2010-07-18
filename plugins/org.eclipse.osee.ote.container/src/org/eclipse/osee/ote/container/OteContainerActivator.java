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
package org.eclipse.osee.ote.container;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.ote.runtimemanager.OteUserLibsNature;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class OteContainerActivator implements BundleActivator{

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.osee.ote.container";

	// The shared instance
	private static OteContainerActivator plugin;
	
	private BundleContext context;

   private JarChangeResourceListener<OteUserLibsNature> userLibResourceListener;

   private LibraryChangeProvider<OteUserLibsNature> userLibListener;
	
	

   /**
	 * The constructor
	 */
	public OteContainerActivator() {
	}

	public void start(BundleContext context) throws Exception {
		plugin = this; 
		this.context = context;
		userLibListener = new LibraryChangeProvider<OteUserLibsNature>();
        this.userLibResourceListener = new JarChangeResourceListener<OteUserLibsNature>(OteUserLibsNature.NATURE_ID, userLibListener);
        
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(userLibResourceListener);
	}

	public void stop(BundleContext context) throws Exception {
	   IWorkspace workspace = ResourcesPlugin.getWorkspace();
	   workspace.removeResourceChangeListener(userLibResourceListener);
	   context = null;
	   plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static OteContainerActivator getDefault() {
		return plugin;
	}
	
	/**
	 * @return the context
	 */
	public BundleContext getContext() {
	   return context;
	}
	
	public LibraryChangeProvider<OteUserLibsNature> getLibraryChangeProvider()
	{
	   return userLibListener;
	}

}
