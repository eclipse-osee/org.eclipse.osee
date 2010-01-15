/*
 * Created on Oct 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal.old;

import org.eclipse.osee.framework.messaging.Component;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;

/**
 * @author b1528444
 *
 */
class SendMessageRunnable implements Runnable {

	
	private final Component component;
	private final String topic;
	private final Object body;
	private final OseeMessagingStatusCallback statusCallback;

	/**
	 * @param component
	 * @param topic
	 * @param body
	 * @param statusCallback
	 */
	public SendMessageRunnable(Component component, String topic, Object body,
			OseeMessagingStatusCallback statusCallback) {
		this.component = component;
		this.topic = topic;
		this.body = body;
		this.statusCallback = statusCallback;
	}

	@Override
	public void run() {
	}

}
