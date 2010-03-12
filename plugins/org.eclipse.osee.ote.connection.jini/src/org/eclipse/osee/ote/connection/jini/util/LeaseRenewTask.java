/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.connection.jini.util;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;

import org.eclipse.osee.ote.connection.jini.Activator;

public class LeaseRenewTask extends TimerTask {
	/**
	 * The amount of time before a lease expires to first attempt renewal. This
	 * amount of time should be sufficiently large to account for delays in
	 * communication (i.e. network delays), and allow for at least a few retries
	 * in the event the service is not reachable. This time is specified in
	 * milliseconds.
	 */
	private static final long RENEWAL_TIME = 1 * 60 * 1000; // 1 minute

	private final ServiceRegistration registration;
	private volatile boolean canceled = false;
	private ServiceRegistrar registrar;

	public LeaseRenewTask(Timer timer, ServiceRegistration registration,
			ServiceRegistrar registrar) {
		this.registration = registration;
		timer.scheduleAtFixedRate(this, 0, RENEWAL_TIME);
		this.registrar = registrar;
	}

	public void run() {
		if (canceled) {
			return;
		}
		try {
			// Renew for the maximum amount of time allowed
			registration.getLease().renew(Lease.FOREVER);
		} catch (Throwable ex) {
			handleLeaseRenewException(ex);
			try {
				registration.getLease().renew(Lease.FOREVER);
			} catch (Throwable ex1) {
				handleLeaseRenewException(ex1);
			}
		}
	}

	private void handleLeaseRenewException(Throwable th) {
		String host = "unknown";
		int port = 0;
		try {
			host = registrar.getLocator().getHost();
			port = registrar.getLocator().getPort();
		} catch (Throwable th2) {
			th2.printStackTrace();
		}
		System.out.printf("lookup serviceId[%s] host[%s] port[%d]\n", registrar
				.getServiceID().toString(), host, port);
		Activator.log(Level.SEVERE, "error renewing lease", th);
	}

	@Override
	public boolean cancel() {
		canceled = true;
		boolean result = super.cancel();
		try {
			System.out.println("Canceling lookup lease");
			registration.getLease().cancel();
		} catch (Exception ex) {
			throw new RuntimeException("failed to cancel lease", ex);
		}
		return result;
	}

}