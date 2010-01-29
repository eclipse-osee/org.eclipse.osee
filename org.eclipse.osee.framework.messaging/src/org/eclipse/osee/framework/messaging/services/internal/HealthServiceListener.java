/*
 * Created on Jan 25, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

	private static final int WIGGLE_ROOM = 20000;
	
	private CompositeKeyHashMap<String/* serviceName */, String /* serviceVersion */, Map<String /* serviceUniqueId */,ServiceHealthPlusTimeout>> map;
	private CompositeKeyHashMap<String/* serviceName */, String /* serviceVersion */, List<ServiceNotification>> callbacks;
	
	HealthServiceListener(CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map, CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks){
		super(ServiceHealth.class);
		this.map = map;
		this.callbacks = callbacks;
	}
	
	@Override
	public void process(Object message, Map<String, Object> headers,
			ReplyConnection replyConnection) {
		ServiceHealth health = (ServiceHealth)message;
		Map<String, ServiceHealthPlusTimeout> idMap = map.get(health.getServiceName(), health.getServiceVersion());
		if(idMap == null){
			idMap = new ConcurrentHashMap<String, ServiceHealthPlusTimeout>();
			map.put(health.getServiceName(), health.getServiceVersion(), idMap);
		}
		long shouldHaveRenewedTime = System.currentTimeMillis() + (health.getRefreshRateInSeconds()*1000) + WIGGLE_ROOM;
		idMap.put(health.getServiceUniqueId(), new ServiceHealthPlusTimeout(health, shouldHaveRenewedTime));
		
		List<ServiceNotification> itemsToNotify = callbacks.get(health.getServiceName(), health.getServiceVersion());
		if(itemsToNotify != null){
			for(ServiceNotification notification :itemsToNotify){
				notification.onServiceUpdate(health);
			}
		}
	}

}
