package org.eclipse.osee.framework.messaging.internal.old;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;

import org.apache.activemq.camel.CamelConnectionFactory;
import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.Component;
import org.eclipse.osee.framework.messaging.OseeMessaging;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.SystemTopic;
import org.eclipse.osee.framework.messaging.internal.Activator;
import org.eclipse.osee.framework.messaging.internal.ProcessorTranslator;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeMessagingImpl implements OseeMessaging {
	private ProducerTemplate template;
	private final OseeTransportListener jmsTransport;
	private final CamelConnectionFactory activeMqRemoteConnectionFactory;
	private final CamelConnectionFactory vmConnectionFactory;
	private final StatusNotifier notifier;
	private final ExecutorService executor;

	OseeMessagingImpl(String commaSeperatedUriList, ExecutorService executor) {
		this.executor = executor;
		activeMqRemoteConnectionFactory = new CamelConnectionFactory();
		activeMqRemoteConnectionFactory.setBrokerURL(String.format(
				"failover:(%s)", commaSeperatedUriList));

		vmConnectionFactory = new CamelConnectionFactory();
		vmConnectionFactory
				.setBrokerURL("vm://localhost?broker.persistent=false");

		notifier = new StatusNotifier();

		jmsTransport = new OseeTransportListener(Component.JMS, notifier);
	}

	void start() throws Exception {

		DefaultCamelContext context = new DefaultCamelContext();

		activeMqRemoteConnectionFactory.setAlwaysSessionAsync(true);
		activeMqRemoteConnectionFactory.setAlwaysSyncSend(false);
		/*
		 * if we use failover: sends pend not sure about route adding, probably
		 * pend also. Need to thread those operations so we don't block
		 * applications use transport listener for lifecycle events
		 */
		activeMqRemoteConnectionFactory.setTransportListener(jmsTransport);
		activeMqRemoteConnectionFactory.setCamelContext(context);
		activeMqRemoteConnectionFactory.getCamelContext().addComponent(
				Component.JMS.getComponentName(),
				JmsComponent.jmsComponent(activeMqRemoteConnectionFactory));

		vmConnectionFactory.setCamelContext(context);
		vmConnectionFactory.getCamelContext().addComponent(
				Component.VM.getComponentName(),
				ActiveMQComponent.jmsComponent(vmConnectionFactory));
		vmConnectionFactory.setObjectMessageSerializationDefered(true);

		template = activeMqRemoteConnectionFactory.getCamelContext()
				.createProducerTemplate();
		activeMqRemoteConnectionFactory.getCamelContext().start();
		jmsTransport.setAvailable(true);
	}

	void stop() throws Exception {
		template.stop();
		activeMqRemoteConnectionFactory.getCamelContext().stop();
	}

	@Override
	public void addListener(final Component component, final String topic, final OseeMessagingListener listener, final OseeMessagingStatusCallback statusCallback) {
		AddListenerRunnable runnable = new AddListenerRunnable(component, topic, listener, statusCallback);
		if (component.equals(Component.VM)) {
			runnable.run();
		} else {
			executor.execute(runnable);
		}
	}

	class AddListenerRunnable implements Runnable {

		private final Component component;
		private final String topic;
		private final OseeMessagingListener listener;
		private final OseeMessagingStatusCallback statusCallback;

		public AddListenerRunnable(Component component, String topic,
				final OseeMessagingListener listener, OseeMessagingStatusCallback statusCallback) {
			this.component = component;
			this.topic = topic;
			this.listener = listener;
			this.statusCallback = statusCallback;
		}

		@Override
		public void run() {
			try {
				checkTransport(component);
				activeMqRemoteConnectionFactory.getCamelContext().addRoutes(
						new RouteBuilder() {
							@Override
							public void configure() {
								from(component.getComponentNameForRoutes() + topic)
										.process(new ProcessorTranslator(listener));
							}
						});

				if (Component.VM.equals(component)
						&& SystemTopic.JMS_HEALTH_STATUS.equals(topic)) {
					listener.process(jmsTransport.createStatusMessage());
				}
				statusCallback.success();
			} catch (Exception ex) {
				statusCallback.fail(ex);
			}
		}
	}

	
	@Override
	public void sendMessage(final Component component, final String topic, Object body, final OseeMessagingStatusCallback statusCallback) {
		SendMessageRunnable runnable = new SendMessageRunnable(component, topic, body,	statusCallback);
		if (component.equals(Component.VM)) {
			runnable.run();
		} else {
			executor.execute(runnable);
		}
	}

	class SendMessageRunnable implements Runnable {

		private final Component component;
		private final String topic;
		private final Object body;
		private final OseeMessagingStatusCallback statusCallback;

		public SendMessageRunnable(Component component, String topic,
				Object body, OseeMessagingStatusCallback statusCallback) {
			this.component = component;
			this.topic = topic;
			this.body = body;
			this.statusCallback = statusCallback;
		}

		@Override
		public void run() {
			try {
				checkTransport(component);
				template.sendBody(component.getComponentNameForRoutes() + topic);
				statusCallback.success();
			} catch (OseeCoreException ex) {
				statusCallback.fail(ex);
			}
		}
	}

	private void checkTransport(Component component) throws OseeCoreException {
		if (component.equals(Component.JMS) && !jmsTransport.isAvailable()) {
			throw new OseeCoreException("JmsComponent is not available.");
		}
	}

	public final class StatusNotifier {

		public void notify(Properties properties) {
			Object object = properties.get("component");
			if (object instanceof String) {
				// THIS SHOULD GO AWAY
				String component = (String) object;

				String healthTopic = null;
				if (Component.JMS.getComponentName().equals(component)) {
					healthTopic = SystemTopic.JMS_HEALTH_STATUS;
				}
				// -------- //

				if (healthTopic != null) {
					sendMessage(Component.VM, healthTopic, properties, new OseeMessagingStatusCallback() {
						@Override
						public void success() {
						}
						@Override
						public void fail(Throwable th) {
							OseeLog.log(Activator.class, Level.SEVERE, th);
						}
					});
				}
			}
		}

		public void notifyError(Component component, Throwable object) {
			OseeLog.log(Activator.class, Level.SEVERE, object);
		}
	}
}
