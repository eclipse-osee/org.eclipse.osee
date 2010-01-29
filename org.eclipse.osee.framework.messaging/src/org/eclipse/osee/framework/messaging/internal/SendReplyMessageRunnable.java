package org.eclipse.osee.framework.messaging.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class SendReplyMessageRunnable implements Runnable {

	private final NodeInfo nodeInfo;
	private final String topic;
	private final Object body;
	private final OseeMessagingStatusCallback statusCallback;
	private final ProducerTemplate template; 
	private final String replyMessageId;

	public SendReplyMessageRunnable(ProducerTemplate template,
			NodeInfo nodeInfo, String topic, Object body, String replyMessageId,
			OseeMessagingStatusCallback statusCallback) {
		this.template = template;
		this.nodeInfo = nodeInfo;
		this.topic = topic;
		this.body = body;
		this.statusCallback = statusCallback;
		this.replyMessageId = replyMessageId;
	}

	@Override
	public void run() {
		try {
			Map<String, Object> headers = new HashMap<String, Object>();
			headers.put("JMSReplyTo", replyMessageId);
//			template.requestBodyAndHeaders(arg0, arg1, arg2)
			template.sendBodyAndHeaders(nodeInfo.getComponentNameForRoutes()
					+ topic + "?preserveMessageQos=true", body, headers);
			statusCallback.success();
		} catch (Exception ex) {
			statusCallback.fail(ex);
		}
	}
}