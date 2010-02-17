/*
 * Created on Feb 3, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.future.MessageService;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
 *
 */
public class ServiceLookupAndRegistrarLifeCycle extends ServiceTracker {

	private RemoteServiceRegistrar registrar;
	private RemoteServiceLookup lookup;
	private ServiceRegistration lookupRegistration;
	private ServiceRegistration registrarRegistration;
	private ClassLoader contextClassLoader;
	/**
	 * @param context
	 * @param filter
	 * @param customizer
	 */
	public ServiceLookupAndRegistrarLifeCycle(BundleContext context, ClassLoader contextClassLoader) {
		super(context, MessageService.class.getName(), null);
		this.contextClassLoader = contextClassLoader;
	}

	@Override
	public Object addingService(ServiceReference reference) {
		MessageService messageService = (MessageService) context.getService(reference);
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		try{
			Thread.currentThread().setContextClassLoader(contextClassLoader);
			RemoteServiceRegistrar registrar = new RemoteServiceRegistrarImpl(messageService.getDefault(), executor);
			registrar.start();
			RemoteServiceLookup lookup = new RemoteServiceLookupImpl(messageService.getDefault(), executor);
			lookup.start();
			
			lookupRegistration = context.registerService(RemoteServiceLookup.class.getName(), lookup, null);
			registrarRegistration = context.registerService(RemoteServiceRegistrar.class.getName(), registrar, null);
		} catch (OseeCoreException ex){
			OseeLog.log(ServiceLookupAndRegistrarLifeCycle.class, Level.SEVERE, ex);
		}
		return super.addingService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		lookupRegistration.unregister();
		registrarRegistration.unregister();
		lookup.stop();
		registrar.stop();
		super.removedService(reference, service);
	}

}
