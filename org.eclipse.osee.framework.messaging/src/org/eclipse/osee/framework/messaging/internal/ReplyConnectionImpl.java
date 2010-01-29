/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;

/**
 * @author b1528444
 * 
 */
public class ReplyConnectionImpl implements ReplyConnection {

	private String replyDestination;
	private ConnectionNode connectionNode;
	private boolean isReplyRequested;
	private Object correlationId;

	ReplyConnectionImpl(){
		isReplyRequested = false;
	}
	
	ReplyConnectionImpl(String replyDestination,
			 ConnectionNode connectionNode, Object correlationId) {
		this.replyDestination = replyDestination;
		this.connectionNode = connectionNode;
		this.correlationId = correlationId;
		isReplyRequested = true;
	}

	@Override
	public void send(Object body, Class<?> clazz, OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
		connectionNode.sendWithCorrelationId(replyDestination, body, clazz, correlationId, statusCallback);
	}

	@Override
	public boolean isReplyRequested() {
		return isReplyRequested;
	}

}
