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

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		plugin = this; 
		this.context = context;
		userLibListener = new LibraryChangeProvider<OteUserLibsNature>();
        this.userLibResourceListener = new JarChangeResourceListener<OteUserLibsNature>(OteUserLibsNature.NATURE_ID, userLibListener);
        
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        workspace.addResourceChangeListener(userLibResourceListener);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
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
