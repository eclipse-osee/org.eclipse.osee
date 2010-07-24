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
