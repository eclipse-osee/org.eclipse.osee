package org.eclipse.osee.ote.discovery.internal;

import org.eclipse.osee.framework.messaging.services.ServiceNotification;
import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

public class OteServerNotification implements ServiceNotification{

	private OTEServerStore store;
	
	public OteServerNotification(OTEServerStore store) {
		this.store = store;
	}

	@Override
	public void onServiceUpdate(ServiceHealth serviceHealth) {
	   store.add(serviceHealth);
	}

	@Override
	public void onServiceGone(ServiceHealth serviceHealth) {
	   store.remove(serviceHealth);
	}

	@Override
	public boolean isServiceGone(ServiceHealth serviceHealth) {
		return true;
	}

}
