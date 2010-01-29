/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.internal;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ProducerTemplate;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.MessageID;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;
import org.eclipse.osee.framework.messaging.services.internal.OseeMessagingStatusImpl;

/**
 * @author b1122182
 */
public class ConnectionNodeImpl implements ConnectionNode {

	private final NodeInfo nodeInfo;
	private final ExecutorService executor;
	private final CamelContext context;
	private final ProducerTemplate template;
	private final SessionMessageId sessionGuid;
	private final Map<String, List<OseeMessagingListener>> replyToListeners;
	
	private class SessionMessageId implements MessageID{
		String guid;
		String messageDestination;
		
		SessionMessageId(String guid){
			this.guid = guid;
			this.messageDestination = "temp:topic:replyResponse" + guid;
		}
		
		@Override
		public String getMessageDestination() {
			return messageDestination;
		}

		@Override
		public Class<?> getSerializationClass() {
			return null;
		}

		@Override
		public boolean isReplyRequired() {
			return false;
		}

		@Override
		public String getName() {
			return guid;
		}

		@Override
		public String getGuid() {
			return guid;
		}
	}
	
	ConnectionNodeImpl(NodeInfo nodeInfo, ExecutorService executor,
			CamelContext context, ProducerTemplate template) {
		this.executor = executor;
		this.nodeInfo = nodeInfo;
		this.context = context;
		this.template = template;
		this.sessionGuid = new SessionMessageId(GUID.create().replaceAll("\\+", "F"));
		replyToListeners = new ConcurrentHashMap<String, List<OseeMessagingListener>>();
		subscribeToSessionReplyQueue();
	}

	@Override
	public void send(MessageID messageId, Object body,
			OseeMessagingStatusCallback statusCallback)
			throws OseeCoreException {
		Object serializedBody = tryToGetSerialized(messageId
				.getSerializationClass(), body);
		if(messageId.isReplyRequired()){
			process(new SendReplyMessageRunnable(template, nodeInfo, messageId
					.getMessageDestination(), serializedBody, sessionGuid.getMessageDestination(), statusCallback));
		} else {
			process(new SendMessageRunnable(template, nodeInfo, messageId
				.getMessageDestination(), serializedBody, statusCallback));
		}
	}

	@Override
	public void sendWithCorrelationId(String message, Object body, Class<?> clazz, Object correlationId,
			OseeMessagingStatusCallback statusCallback) throws OseeCoreException {
		Component component = context.getComponent(nodeInfo.getComponentName());
		Object serializedBody = tryToGetSerialized(clazz, body);
		process(new SendMessageDestinationRunnable(template, nodeInfo, message,
				component, serializedBody, correlationId, statusCallback));
	}

	private Object tryToGetSerialized(Class<?> clazz, Object body)
			throws OseeCoreException {
		if (clazz != null) {
			try {
				return JAXBUtil.marshal(body);
			} catch (UnsupportedEncodingException ex) {
				new OseeWrappedException(ex);
			}
		}
		return body;
	}

	@Override
	public void subscribe(MessageID topic, OseeMessagingListener listener,
			OseeMessagingStatusCallback statusCallback) {
		process(new AddListenerRunnable(context, nodeInfo, this, topic
				.getMessageDestination(), listener, statusCallback));
	}

	@Override
	public void subscribeToReply(MessageID messageId,
			OseeMessagingListener listener) {
		List<OseeMessagingListener> list = replyToListeners.get(messageId.getGuid());
		if(list == null){
			list = new CopyOnWriteArrayList<OseeMessagingListener>();
			replyToListeners.put(messageId.getGuid(), list);
		}
		list.add(listener);
	}
	
	private void subscribeToSessionReplyQueue(){
		process(new AddListenerRunnable(context, nodeInfo, this, 
				sessionGuid.getMessageDestination(), new ReplySubscription(), new OseeMessagingStatusImpl("Failed to add message", ConnectionNodeImpl.class)));
	}
	
	private class ReplySubscription extends OseeMessagingListener{

		@Override
		public void process(Object message, Map<String, Object> headers,
				ReplyConnection replyConnection) {
			Object correlationId = headers.get("JMSCorrelationID");
			if(correlationId != null){
				String topic = correlationId.toString().substring(correlationId.toString().lastIndexOf('/') + 1);
				List<OseeMessagingListener> listeners = replyToListeners.get(topic);
				if(listeners != null){
					for(OseeMessagingListener item:listeners){
						try {
							message = JAXBUtil.unmarshal(message, item);
						} catch (UnsupportedEncodingException ex) {
							ex.printStackTrace();
						}
						item.process(message, headers, replyConnection);
					}
				}
			}
		}
		
	}

	private void process(Runnable runnable) {
		if (nodeInfo.isVMComponent()) {
			runnable.run();
		} else {
			executor.execute(runnable);
		}
	}


}
