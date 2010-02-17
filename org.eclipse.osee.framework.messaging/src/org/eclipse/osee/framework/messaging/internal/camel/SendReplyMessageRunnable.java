package org.eclipse.osee.framework.messaging.internal.camel;

import java.util.Map;
import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.NodeInfo;
import org.eclipse.osee.framework.plugin.core.util.ExportClassLoader;

class SendReplyMessageRunnable extends SendMessage {

	private final NodeInfo nodeInfo;
	private final String topic;
	private final Object body;
	private final OseeMessagingStatusCallback statusCallback;
	private final ProducerTemplate template; 
	private final String replyMessageId;

	public SendReplyMessageRunnable(String version, String sourceId, ProducerTemplate template,
			NodeInfo nodeInfo, String topic, Object body, String replyMessageId,
			OseeMessagingStatusCallback statusCallback) {
		super(version, sourceId);
		this.template = template;
		this.nodeInfo = nodeInfo;
		this.topic = topic;
		this.body = body;
		this.statusCallback = statusCallback;
		this.replyMessageId = replyMessageId;
	}

	@Override
	public void run() {
	   Thread.currentThread().setContextClassLoader(ExportClassLoader.getInstance());
		try {
			Map<String, Object> headers = getBaseHeader();
			headers.put("OSEEReplyTo", replyMessageId);
			String msgDest = nodeInfo.getComponentNameForRoutes()+ topic + "?preserveMessageQos=true";
			template.sendBodyAndHeaders(msgDest, body, headers);
			statusCallback.success();
		} catch (Exception ex) {
			statusCallback.fail(ex);
		}
	}
}