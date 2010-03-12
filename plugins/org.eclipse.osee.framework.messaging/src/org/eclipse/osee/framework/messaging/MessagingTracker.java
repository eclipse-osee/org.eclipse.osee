/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;


import org.eclipse.osee.framework.messaging.internal.Activator;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author b1528444
 *
 */
public class MessagingTracker extends ServiceTracker {

	public MessagingTracker() {
		super(Activator.getInstance().getContext(), OseeMessaging.class.getName(), null);
	}

}
