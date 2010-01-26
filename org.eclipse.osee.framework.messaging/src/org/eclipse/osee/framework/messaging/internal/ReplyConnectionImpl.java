/*
 * Created on Jan 20, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import javax.jms.Destination;
import javax.jms.JMSException;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.component.jms.JmsEndpoint;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;

/**
 * @author b1528444
 * 
 */
public class ReplyConnectionImpl implements ReplyConnection {

	private Destination replyDestination;
	private JmsComponent activeMQComponent;
	private ProducerTemplate template;
	private boolean isReplyRequested;

	ReplyConnectionImpl(){
		isReplyRequested = false;
	}
	
	ReplyConnectionImpl(Destination replyDestination,
			JmsComponent activeMQComponent, ProducerTemplate template) {
		this.replyDestination = replyDestination;
		this.activeMQComponent = activeMQComponent;
		this.template = template;
		isReplyRequested = true;
	}

	@Override
	public void send(Object body, OseeMessagingStatusCallback statusCallback) {
		try {
			JmsEndpoint endpoint = JmsEndpoint.newInstance(replyDestination,
					activeMQComponent);
			template.sendBody(endpoint, body);
		} catch (JMSException ex) {
			statusCallback.fail(ex);
		}

	}

	@Override
	public boolean isReplyRequested() {
		return isReplyRequested;
	}

}
