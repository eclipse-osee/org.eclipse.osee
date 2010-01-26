/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author b1528444
 * 
 */
public class RemoteServiceLookupImpl implements RemoteServiceLookup {

	private ConnectionNode connectionNode;
	private CompositeKeyHashMap<String, String, ServiceHealth> map;
	private CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks;

	public RemoteServiceLookupImpl(ConnectionNode node) {
		this.connectionNode = node;
		map = new CompositeKeyHashMap<String, String, ServiceHealth>(25, true);
		callbacks = new CompositeKeyHashMap<String, String, List<ServiceNotification>>(
				25, true);
		connectionNode.subscribe(BaseMessages.ServiceHealth,
				new HealthServiceListener(map, callbacks),
				new OseeMessagingStatusImpl("Failed to subscribe to " + BaseMessages.ServiceHealth.getName(), 
						RemoteServiceLookupImpl.class));
	}

	@Override
	public void register(String serviceId, String serviceVersion,
			ServiceNotification notification) {
		addListener(serviceId, serviceVersion, notification);
		ServiceHealth serviceHealth = map.get(serviceId, serviceVersion);
		if (serviceHealth != null) {
			notification.onHealthUpdate(serviceHealth);
		} else {
			ServiceHealthRequest request = new ServiceHealthRequest();
			request.setServiceId(serviceId);
			request.setServiceVersion(serviceVersion);
			connectionNode.send(BaseMessages.ServiceHealthRequest, request, new OseeMessagingStatusImpl(String.format("Failed to send Health Request for %s [%s]", serviceId, serviceVersion), RemoteServiceLookup.class));
		}
	}

	private void addListener(String serviceId, String serviceVersion,
			ServiceNotification notification) {
		List<ServiceNotification> itemsToNotify = callbacks.get(serviceId,
				serviceVersion);
		if (itemsToNotify == null) {
			itemsToNotify = new CopyOnWriteArrayList<ServiceNotification>();
			callbacks.put(serviceId, serviceVersion, itemsToNotify);
		}
		itemsToNotify.add(notification);
	}

	@Override
	public boolean unregister(String serviceId,
			String serviceVersion, ServiceNotification notification) {
		return removeListener(serviceId, serviceVersion, notification);
	}

	private boolean removeListener(String serviceId,
			String serviceVersion, ServiceNotification notification) {
		List<ServiceNotification> itemsToNotify = callbacks.get(serviceId,
				serviceVersion);
		boolean removed = false;
		if (itemsToNotify != null) {
			removed = itemsToNotify.remove(notification);
		}
		return removed;
	}

}
