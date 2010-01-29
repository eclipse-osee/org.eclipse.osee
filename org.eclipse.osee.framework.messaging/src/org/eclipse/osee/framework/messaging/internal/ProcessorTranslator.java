package org.eclipse.osee.framework.messaging.internal;

import java.util.Map;

import org.apache.activemq.command.ActiveMQDestination;
import org.apache.camel.Exchange;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;

/**
 * @author b1528444
 */
public class ProcessorTranslator implements org.apache.camel.Processor {

	private final OseeMessagingListener listener;
	private ConnectionNode connectionNode;

	public ProcessorTranslator(ConnectionNode connectionNode, OseeMessagingListener listener) {
		this.listener = listener;
		this.connectionNode = connectionNode;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		Map<String, Object> headers = exchange.getIn().getHeaders();
		Class<?> pojoType = listener.getClazz();
		Object messageBody;
		if (pojoType == null) {
			messageBody = exchange.getIn().getBody();
		} else {
			messageBody = JAXBUtil.unmarshal(exchange.getIn().getBody()
					.toString(), pojoType);
		}
		Object obj = headers.get("JMSReplyTo");
		if(obj != null && obj instanceof ActiveMQDestination){
			Object correlationId = headers.get("JMSDestination");
			ActiveMQDestination q = (ActiveMQDestination)obj;
			final String destString = q.getPhysicalName();
			ReplyConnectionImpl replyConnectionImpl = new ReplyConnectionImpl(destString, connectionNode, correlationId);
			listener.process(messageBody, headers, replyConnectionImpl);
		} else {
			listener.process(messageBody, headers, new ReplyConnectionImpl());
		}
	}
}
