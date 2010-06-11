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
import org.eclipse.osee.framework.messaging.MessageService;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class ServiceLookupAndRegistrarLifeCycle extends ServiceTracker {

	private RemoteServiceRegistrar registrar;
	private ServiceRegistration lookupRegistration;
	private ServiceRegistration registrarRegistration;
	private ClassLoader contextClassLoader;
	private RemoteServiceLookupImpl lookup;
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
	   OseeLog.log(Activator.class, Level.INFO, String.format("Found %s service.", MessageService.class.getName()));
	   OseeLog.log(Activator.class, Level.FINEST, String.format("GOING TO GET THE REF"));
	   MessageService messageService = (MessageService) context.getService(reference);
		OseeLog.log(Activator.class, Level.FINEST, String.format("got the service ref"));
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
		OseeLog.log(Activator.class, Level.FINEST, String.format("got exec"));
		try{
		   OseeLog.log(Activator.class, Level.FINEST, String.format("set context classloader"));
			Thread.currentThread().setContextClassLoader(contextClassLoader);
			OseeLog.log(Activator.class, Level.FINEST, String.format("done set context classloader"));
			registrar = new RemoteServiceRegistrarImpl(messageService.getDefault(), executor);
			OseeLog.log(Activator.class, Level.FINEST, String.format("Getting ready to start %s.", RemoteServiceRegistrarImpl.class.getName()));
			registrar.start();
			OseeLog.log(Activator.class, Level.FINEST, String.format("started %s.", RemoteServiceRegistrarImpl.class.getName()));
			lookup = new RemoteServiceLookupImpl(messageService.getDefault(), executor);
			OseeLog.log(Activator.class, Level.FINEST, String.format("Getting ready to start %s.", RemoteServiceLookupImpl.class.getName()));
			lookup.start();
			OseeLog.log(Activator.class, Level.FINEST, String.format("started %s.", RemoteServiceLookupImpl.class.getName()));
			
			lookupRegistration = context.registerService(RemoteServiceLookup.class.getName(), lookup, null);
			OseeLog.log(Activator.class, Level.INFO, String.format("Registered %s.", RemoteServiceLookup.class.getName()));
			registrarRegistration = context.registerService(RemoteServiceRegistrar.class.getName(), registrar, null);
			OseeLog.log(Activator.class, Level.INFO, String.format("Registered %s.", RemoteServiceRegistrar.class.getName()));
		} catch (OseeCoreException ex){
			OseeLog.log(ServiceLookupAndRegistrarLifeCycle.class, Level.SEVERE, ex);
		}
		return super.addingService(reference);
	}

	@Override
	public void removedService(ServiceReference reference, Object service) {
		commonShutdown();
		super.removedService(reference, service);
	}

	@Override
	public void close() {
		commonShutdown();
		super.close();
	}

	private void commonShutdown(){
		if(lookupRegistration != null){
			lookupRegistration.unregister();
			lookupRegistration = null;
			lookup.stop();
		}
		if(registrarRegistration != null){
			registrarRegistration.unregister();
			registrarRegistration = null;
			registrar.stop();
		}
		
	}
	
	
}
