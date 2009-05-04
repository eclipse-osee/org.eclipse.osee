package org.eclipse.osee.framework.messaging.internal;

import java.util.Hashtable;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	private ServiceRegistration registration;
	private MessagingGatewayImpl messaging;

   /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
	   MessagingGatewayImpl messaging = new MessagingGatewayImpl();
	   registration = context.registerService(MessagingGateway.class.getName(), messaging, new Hashtable());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	   registration.unregister();
	   messaging.dispose();
	}
}
