/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;
import org.eclipse.osee.framework.messaging.ReplyConnection;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author b1528444
 *
 */
class HealthServiceListener extends OseeMessagingListener {

	private CompositeKeyHashMap<String, String, ServiceHealth> map;
	private CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks;
	
	HealthServiceListener(CompositeKeyHashMap<String, String, ServiceHealth> map, CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks){
		super(ServiceHealth.class);
		this.map = map;
		this.callbacks = callbacks;
	}
	
	@Override
	public void process(Object message, Map<String, Object> headers,
			ReplyConnection replyConnection) {
		ServiceHealth health = (ServiceHealth)message;
		map.put(health.getServiceId(), health.getServiceVersion(), health);
		List<ServiceNotification> itemsToNotify = callbacks.get(health.getServiceId(), health.getServiceVersion());
		if(itemsToNotify != null){
			for(ServiceNotification notification :itemsToNotify){
				notification.onHealthUpdate(health);
			}
		}
	}

}
