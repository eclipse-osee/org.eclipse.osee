package org.eclipse.osee.framework.messaging.internal.camel;

import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class SendMessageDestinationRunnable extends SendMessage {

	private final Object body;
	private final OseeMessagingStatusCallback statusCallback;
	private final ProducerTemplate template;
	private final String destination;
	private final Component component;
	private final Object correlationId;
	private NodeInfo nodeInfo;
	
	public SendMessageDestinationRunnable(String version, String sourceId, ProducerTemplate template,
			NodeInfo nodeInfo, String destination, Component component,
			Object body, Object correlationId, OseeMessagingStatusCallback statusCallback) {
		super(version, sourceId);
		this.template = template;
		this.body = body;
		this.nodeInfo = nodeInfo;
		this.statusCallback = statusCallback;
		this.destination = destination;
		this.component = component;
		this.correlationId = correlationId;
	}

	@Override
	public void run() {
		try {
			Map<String, Object> headers = getBaseHeader();
			headers.put("JMSCorrelationID", correlationId);
			template.sendBodyAndHeaders(nodeInfo.getComponentNameForRoutes() + this.destination, body, headers);
			statusCallback.success();
		} catch (Exception ex) {
			ex.printStackTrace();
			statusCallback.fail(ex);
		}
	}
}