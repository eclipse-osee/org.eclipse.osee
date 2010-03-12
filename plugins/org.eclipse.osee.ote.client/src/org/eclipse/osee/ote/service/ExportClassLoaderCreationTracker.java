/*
 * Created on Feb 4, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.service;

import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.packageadmin.PackageAdmin;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
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
