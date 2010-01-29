/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
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
	private ConcurrentHashMap<String, ScheduledFuture<?>> map;
	private CompositeKeyHashMap<String, String, UpdateStatus> mapForReplys;
	private ScheduledExecutorService executor;
	
	public RemoteServiceRegistrarImpl(ConnectionNode node, ScheduledExecutorService executor) {
		this.connectionNode = node;
		this.executor = executor;
		map = new ConcurrentHashMap<String, ScheduledFuture<?>>();
		mapForReplys = new CompositeKeyHashMap<String, String, UpdateStatus>(8,	true);
		connectionNode.subscribe(BaseMessages.ServiceHealthRequest,
				new HealthRequestListener(mapForReplys),
				new OseeMessagingStatusImpl("Failed to subscribe to " + BaseMessages.ServiceHealthRequest.getName(),
						RemoteServiceRegistrarImpl.class));
	}

	@Override
	public void registerService(String serviceName, String serviceVersion, String serviceUniqueId, URI broker,
			ServiceInfoPopulator infoPopulator, int refreshRateInSeconds) {
		UpdateStatus updateStatus = new UpdateStatus(this.connectionNode, serviceName, serviceVersion, serviceUniqueId, broker, refreshRateInSeconds, infoPopulator);
		ScheduledFuture<?> scheduled = executor.scheduleAtFixedRate(updateStatus, 0, refreshRateInSeconds, TimeUnit.SECONDS);
		map.put(serviceName+serviceVersion+serviceUniqueId, scheduled);
		
		UpdateStatus updateStatusForReply = new UpdateStatus(this.connectionNode, serviceName, serviceVersion, serviceUniqueId, broker, refreshRateInSeconds, infoPopulator);
		mapForReplys.put(serviceName, serviceVersion, updateStatusForReply);
	}

	@Override
	public boolean unregisterService(String serviceName,
			String serviceVersion, String serviceUniqueId) {
		ScheduledFuture<?> scheduled = map.remove(serviceName+serviceVersion+serviceUniqueId);
		if(scheduled == null){
			return false; 
		} else {
			return scheduled.cancel(false);
		}
	}

}
