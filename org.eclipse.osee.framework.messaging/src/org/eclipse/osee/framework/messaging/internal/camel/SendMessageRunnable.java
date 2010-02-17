package org.eclipse.osee.framework.messaging.internal.camel;

import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class SendMessageRunnable extends SendMessage {

	private final NodeInfo nodeInfo;
	private final String topic;
	private final Object body;
	private final OseeMessagingStatusCallback statusCallback;
	private final ProducerTemplate template;

	public SendMessageRunnable(String version, String sourceId, ProducerTemplate template, NodeInfo nodeInfo,
			String topic, Object body,
			OseeMessagingStatusCallback statusCallback) {
		super(version, sourceId);
		this.template = template;
		this.nodeInfo = nodeInfo;
		this.topic = topic;
		this.body = body;
		this.statusCallback = statusCallback;
	}

	@Override
	public void run() {
		try {
			Map<String, Object> headers = getBaseHeader();
			template.sendBodyAndHeaders(nodeInfo.getComponentNameForRoutes()
						+ topic, body, headers);
			statusCallback.success();
		} catch (Exception ex) {
			statusCallback.fail(ex);
		}
	}
}