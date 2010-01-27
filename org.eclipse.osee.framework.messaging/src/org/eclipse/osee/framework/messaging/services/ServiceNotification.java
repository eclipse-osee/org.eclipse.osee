/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services;

import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author b1528444
 *
 */
public interface ServiceNotification {
	void onServiceUpdate(ServiceHealth serviceHealth);
	void onServiceGone(ServiceHealth serviceHealth);
}
