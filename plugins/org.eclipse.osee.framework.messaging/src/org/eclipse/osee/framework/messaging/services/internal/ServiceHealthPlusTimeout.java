/*
 * Created on Jan 26, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging.services.internal;

import org.eclipse.osee.framework.messaging.services.messages.ServiceHealth;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class ServiceHealthPlusTimeout {

	private ServiceHealth health;
	private long shouldHaveRenewedTime;
	
	public ServiceHealthPlusTimeout(ServiceHealth health,
			long shouldHaveRenewedTime) {
		this.health = health;
		this.shouldHaveRenewedTime = shouldHaveRenewedTime;
	}

	public boolean isTimedOut(long currentSystemTime){
		return (currentSystemTime > shouldHaveRenewedTime);
	}
	
	public ServiceHealth getServiceHealth(){
		return health;
	}
	
}
