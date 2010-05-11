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
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.messaging.services.ServiceNotification;

/**
 * @author Andrew M. Finkbeiner
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
			for(Entry<String, ServiceHealthPlusTimeout> key:items.entrySet()){
				if(key.getValue().isTimedOut(currentSystemTime)){
					toRemove.add(new ThreeItems(pair.getFirst(), pair.getSecond(), key.getKey()));
					List<ServiceNotification> list = callbacks.get(pair.getFirst(), pair.getSecond());
					for(ServiceNotification notify:list){
						notify.onServiceGone(key.getValue().getServiceHealth());
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

	private static class ThreeItems {
		
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
