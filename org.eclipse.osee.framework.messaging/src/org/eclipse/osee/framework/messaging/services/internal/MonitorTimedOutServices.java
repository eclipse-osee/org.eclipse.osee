/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;

/**
 * @author b1528444
 *
 */
class MonitorTimedOutServices implements Runnable {

	private CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map;
	private CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks;
	
	public MonitorTimedOutServices(
			CompositeKeyHashMap<String, String, Map<String, ServiceHealthPlusTimeout>> map, CompositeKeyHashMap<String, String, List<ServiceNotification>> callbacks) {
		this.map = map;
		this.callbacks = callbacks;
	}

	@Override
	public void run() {
		List<ThreeItems> toRemove = new ArrayList<ThreeItems>();
		long currentSystemTime = System.currentTimeMillis();
		Set<Pair<String, String>> keySet = map.keySet();
		for(Pair<String, String> pair:keySet){
			Map<String, ServiceHealthPlusTimeout> items = map.get(pair.getFirst(), pair.getSecond());
			for(String key:items.keySet()){
				ServiceHealthPlusTimeout serviceHealthPlusTimeout = items.get(key);
				if(serviceHealthPlusTimeout.isTimedOut(currentSystemTime)){
					System.out.println(pair.getFirst() + pair.getSecond() + key);
					toRemove.add(new ThreeItems(pair.getFirst(), pair.getSecond(), key));
					List<ServiceNotification> list = callbacks.get(pair.getFirst(), pair.getSecond());
					for(ServiceNotification notify:list){
						notify.onServiceGone(serviceHealthPlusTimeout.getServiceHealth());
					}
				}
			}
		}
		for(ThreeItems item:toRemove){
			Map<String, ServiceHealthPlusTimeout> innerMap = map.get(item.first, item.second);
			innerMap.remove(item.key);
			System.out.println(item.key);
			if(innerMap.size() == 0){
				map.remove(item.first, item.second);
				System.out.println("removed " + item.first + item.second);
			}
		}
	}

	private class ThreeItems {
		
		String first;
		String second;
		String key;
		
		ThreeItems(String first, String second, String key){
			this.first = first;
			this.second = second;
			this.key = key;
		}
	}

}
