/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.ConnectionNode;
import org.eclipse.osee.framework.messaging.services.BaseMessages;
import org.eclipse.osee.framework.messaging.services.RemoteServiceLookup;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealthRequest;

/**
 * @author Andrew M. Finkbeiner
 * 
 */
public class RemoteServiceLookupImpl implements RemoteServiceLookup {

	private ConnectionNode connectionNode;
	private CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map;
	private CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks;
	private HealthServiceListener healthServiceListener;

   public RemoteServiceLookupImpl(ConnectionNode node, ScheduledExecutorService executor) {
		this.connectionNode = node;
		map = new CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>>(25, true);
		callbacks = new CompositeKeyHashMap<String, String, List<ServiceNotification>>(
				25, true);
		healthServiceListener = new HealthServiceListener(map, callbacks);
		connectionNode.subscribeToReply(BaseMessages.ServiceHealthRequest, 
				healthServiceListener);
		executor.scheduleAtFixedRate(new MonitorTimedOutServices(map, callbacks), 30, 30, TimeUnit.SECONDS);
	}
	
	public void start(){
		connectionNode.subscribe(BaseMessages.ServiceHealth,
				healthServiceListener,
				new OseeMessagingStatusImpl("Failed to subscribe to " + BaseMessages.ServiceHealth.getName(), 
						RemoteServiceLookupImpl.class));
		
	}
	
	public void stop(){
		connectionNode.unsubscribe(BaseMessages.ServiceHealth,
				healthServiceListener,
				new OseeMessagingStatusImpl("Failed to subscribe to " + BaseMessages.ServiceHealth.getName(), 
						RemoteServiceLookupImpl.class));
	}
	

	@Override
	public void register(String serviceName, String serviceVersion,
			ServiceNotification notification) {
		addListener(serviceName, serviceVersion, notification);
		Map<String, ServiceHealthPlusTimeout> healthMap = map.get(serviceName, serviceVersion);
		if (healthMap != null) {
			for(ServiceHealthPlusTimeout serviceHealth:healthMap.values()){
				notification.onServiceUpdate(serviceHealth.getServiceHealth());
			}
		} else {
		   sendOutRequest(serviceName, serviceVersion);
		}
	}
	
	public void sendOutRequestsForServiceHealth(){
	   Set<Pair<String, String>> pairs = callbacks.keySet();
	   for(Pair<String, String> pair:pairs){
	      sendOutRequest(pair.getFirst(), pair.getSecond());
	   }
	}
	
	private void sendOutRequest(String serviceName, String serviceVersion){
	   ServiceHealthRequest request = new ServiceHealthRequest();
      request.setServiceName(serviceName);
      request.setServiceVersion(serviceVersion);
      try {
         connectionNode.send(BaseMessages.ServiceHealthRequest, request, new OseeMessagingStatusImpl(String.format("Failed to send Health Request for %s [%s]", serviceName, serviceVersion), RemoteServiceLookup.class));
      } catch (OseeCoreException ex) {
         OseeLog.log(RemoteServiceLookupImpl.class, Level.SEVERE, ex);
      }
	}

	private void addListener(String serviceName, String serviceVersion,
			ServiceNotification notification) {
		List<ServiceNotification> itemsToNotify = callbacks.get(serviceName,
				serviceVersion);
		if (itemsToNotify == null) {
			itemsToNotify = new CopyOnWriteArrayList<ServiceNotification>();
			callbacks.put(serviceName, serviceVersion, itemsToNotify);
		}
		itemsToNotify.add(notification);
	}

	@Override
	public boolean unregister(String serviceName,
			String serviceVersion, ServiceNotification notification) {
		return removeListener(serviceName, serviceVersion, notification);
	}

	private boolean removeListener(String serviceName,
			String serviceVersion, ServiceNotification notification) {
		List<ServiceNotification> itemsToNotify = callbacks.get(serviceName,
				serviceVersion);
		boolean removed = false;
		if (itemsToNotify != null) {
			removed = itemsToNotify.remove(notification);
		}
		return removed;
	}

}
