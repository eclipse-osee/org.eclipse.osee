package org.eclipse.osee.framework.messaging.internal.camel;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.FromDefinition;
import org.apache.camel.model.RouteDefinition;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.OseeMessagingStatusCallback;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.future.NodeInfo;

class ListenerRunnable implements Runnable {

	private final NodeInfo nodeInfo;
	private final String topic;
	private final OseeMessagingListener listener;
	private final OseeMessagingStatusCallback statusCallback;
	private final CamelContext context;
	private ConnectionNode connectionNode;
	private boolean add;

	public ListenerRunnable(boolean add, CamelContext context,
			NodeInfo nodeInfo, ConnectionNode connectionNode, String topic,
			final OseeMessagingListener listener,
			OseeMessagingStatusCallback statusCallback) {
		this.add = add;
		this.context = context;
		this.nodeInfo = nodeInfo;
		this.topic = topic;
		this.listener = listener;
		this.statusCallback = statusCallback;
		this.connectionNode = connectionNode;
	}

	@Override
	public void run() {
		try {
			if (add) {
				synchronized (context.getRouteDefinitions()) {
					context.addRoutes(new RouteBuilder() {
						@Override
						public void configure() {
							String path = nodeInfo.getComponentNameForRoutes()
									+ topic;
							from(path).id("mySpecialId_" + listener.hashCode())
									.process(
											new ProcessorTranslator(
													connectionNode, listener));
						}
					});
				}
			} else {
				String path = nodeInfo.getComponentNameForRoutes() + topic;
				List<RouteDefinition> toRemove = new ArrayList<RouteDefinition>();
				synchronized (context.getRouteDefinitions()) {
					for (RouteDefinition def : context.getRouteDefinitions()) {
						String id = def.getId();
						if (id == null) {
							System.out.println(def);
						} else if (id.equals("mySpecialId_"
								+ listener.hashCode())) {
							for (FromDefinition from : def.getInputs()) {
								if (from.getUri().equals(path)) {
									toRemove.add(def);
								}
							}
						}
					}
					context.removeRouteDefinitions(toRemove);
				}
			}
			statusCallback.success();
		} catch (Exception ex) {
			statusCallback.fail(ex);
		}
	}
}