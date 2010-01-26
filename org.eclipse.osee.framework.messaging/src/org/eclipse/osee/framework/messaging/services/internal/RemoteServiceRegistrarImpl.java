/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.messaging.future.ConnectionNode;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.RemoteServiceRegistrar;
import org.eclipse.osee.framework.messaging.services.ServiceInfoPopulator;

/**
 * @author b1528444
 *
 */
public class RemoteServiceRegistrarImpl implements RemoteServiceRegistrar {

	private ConnectionNode connectionNode;
	private CompositeKeyHashMap<String, String, ScheduledFuture<?>> map;
	private ScheduledExecutorService executor;
	
	public RemoteServiceRegistrarImpl(ConnectionNode node, ScheduledExecutorService executor) {
		this.connectionNode = node;
		this.executor = executor;
		map = new CompositeKeyHashMap<String, String, ScheduledFuture<?>>(25, true);
		connectionNode.subscribe(BaseMessages.ServiceHealthRequest,
				new HealthRequestListener(),
				new OseeMessagingStatusImpl("Failed to subscribe to " + BaseMessages.ServiceHealthRequest.getName(),
						RemoteServiceRegistrarImpl.class));
	}

	@Override
	public void registerService(String serviceId, String serviceVersion, URI broker,
			ServiceInfoPopulator infoPopulator, long period, TimeUnit unit) {
		UpdateStatus updateStatus = new UpdateStatus(this.connectionNode, serviceId, serviceVersion, broker, infoPopulator);
		ScheduledFuture<?> scheduled = executor.scheduleAtFixedRate(updateStatus, 0, period, unit);
		map.put(serviceId, serviceVersion, scheduled);
	}

	@Override
	public boolean unregisterService(String serviceId,
			String serviceVersion) {
		ScheduledFuture<?> scheduled = map.remove(serviceId, serviceVersion);
		if(scheduled == null){
			return false; 
		} else {
			return scheduled.cancel(false);
		}
	}

}
