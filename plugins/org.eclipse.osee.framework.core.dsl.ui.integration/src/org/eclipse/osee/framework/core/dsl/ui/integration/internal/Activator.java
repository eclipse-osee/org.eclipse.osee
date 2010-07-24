package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator {
	public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.dsl.ui.integration";
	private static BundleContext context;
	private static Activator instance;
	private ServiceTracker cacheServiceTracker;

	static BundleContext getContext() {
		return context;
	}

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		Activator.instance = this;
		Activator.context = bundleContext;
		cacheServiceTracker = new ServiceTracker(context, IOseeCachingService.class.getName(), null);
		cacheServiceTracker.open(true);
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		if (cacheServiceTracker != null) {
			cacheServiceTracker.close();
		}
	}

	public static IOseeCachingService getOseeCacheService() {
		return (IOseeCachingService) Activator.instance.cacheServiceTracker.getService();
	}
}
