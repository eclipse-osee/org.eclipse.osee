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
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.osee.framework.core.exception.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class SessionDataStoreSync implements ISessionDataStoreSync {

	private static final long DATASTORE_UPDATE = 1000 * 5;

	private final SessionCache sessionCache;
	private Timer updateTimer;

	public SessionDataStoreSync(SessionCache sessionCache) {
		this.sessionCache = sessionCache;
	}

	@Override
	public void start() {
		updateTimer = new Timer("Persist Session Data Timer");
		updateTimer.scheduleAtFixedRate(new UpdateDataStore(), DATASTORE_UPDATE, DATASTORE_UPDATE);
	}

	@Override
	public void stop() {
		updateTimer.cancel();
	}

	private final class UpdateDataStore extends TimerTask {

		private boolean isCacheCurrent = false;

		@Override
		public void run() {
			try {
				if (!isCacheCurrent) {
					sessionCache.reloadCache();
					isCacheCurrent = true;
				}
				sessionCache.storeAllModified();
			} catch (OseeCoreException ex) {
				// Do nothing;
			}
		}
	}
}
