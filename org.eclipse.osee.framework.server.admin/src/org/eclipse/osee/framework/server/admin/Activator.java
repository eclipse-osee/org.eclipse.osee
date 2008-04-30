package org.eclipse.osee.framework.server.admin;

import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {

	private static Activator instance;
	private ServiceTracker resourceManagementTracker;
	private ServiceTracker resourceLocatorManagerTracker;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		instance = this;
		resourceManagementTracker = new ServiceTracker(context,
				IResourceManager.class.getName(), null);
		resourceManagementTracker.open();

		resourceLocatorManagerTracker = new ServiceTracker(context,
				IResourceLocatorManager.class.getName(), null);
		resourceLocatorManagerTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		resourceManagementTracker.close();
		resourceManagementTracker = null;

		resourceLocatorManagerTracker.close();
		resourceLocatorManagerTracker = null;
	}

	public IResourceManager getResourceManager() {
		return (IResourceManager) resourceManagementTracker.getService();
	}

	public IResourceLocatorManager getResourceLocatorManager() {
		return (IResourceLocatorManager) resourceLocatorManagerTracker
				.getService();
	}
	 public static Activator getInstance() {
	      return Activator.instance;
	   }
}
