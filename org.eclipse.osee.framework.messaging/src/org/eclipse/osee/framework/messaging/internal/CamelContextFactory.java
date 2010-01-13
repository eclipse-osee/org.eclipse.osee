/*
 * Created on Jan 12, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;

/**
 * @author b1528444
 *
 */
public class CamelContextFactory implements ServiceFactory {

	
	
	
	@Override
	public Object getService(Bundle bundle, ServiceRegistration registration) {
		return null;
	}

	@Override
	public void ungetService(Bundle bundle, ServiceRegistration registration,
			Object service) {
	}

}
