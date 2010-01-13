/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.util.Properties;
import java.util.concurrent.Executors;


import org.eclipse.osee.framework.messaging.OseeMessaging;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author b1528444
 *
 */
public class OseeMessagingImplService {
	private ServiceRegistration registration;
	private OseeMessagingImpl oseeMessaging;
	private BundleContext context;
	
	public OseeMessagingImplService(BundleContext context){
		this.context = context;
		String commaSeperatedListOfuris = getActiveMQAddresses();
		oseeMessaging = new OseeMessagingImpl(commaSeperatedListOfuris, Executors.newCachedThreadPool());
	}
	
	public void start() throws Exception{
		oseeMessaging.start();
		
		registration = context.registerService(OseeMessaging.class.getName(),
				oseeMessaging, new Properties());
	}
	
	public void stop() throws Exception{
		if (registration != null) {
			registration.unregister();
			registration = null;
		}
		if (oseeMessaging != null) {
			oseeMessaging.stop();
			oseeMessaging = null;
		}
	}
	
	private String getActiveMQAddresses() {
		String uri = System.getProperty("osee.messaging.activemq.uris");
		if(uri == null){
			return "tcp://localhost:61616";//default localhost activemq port
		}
		return uri;
	}
}
