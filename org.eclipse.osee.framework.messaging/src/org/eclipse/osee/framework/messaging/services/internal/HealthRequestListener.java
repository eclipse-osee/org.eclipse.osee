/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.Map;

import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;

/**
 * @author b1528444
 *
 */
public class HealthRequestListener extends OseeMessagingListener {

	/* (non-Javadoc)
	 * @see org.eclipse.osee.framework.messaging.OseeMessagingListener#process(java.lang.Object, java.util.Map, org.eclipse.osee.framework.messaging.ReplyConnection)
	 */
	@Override
	public void process(Object message, Map<String, Object> headers,
			ReplyConnection replyConnection) {
	}

}
