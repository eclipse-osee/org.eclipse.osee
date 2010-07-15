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

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.ISession;

/**
 * @author Roberto E. Escobar
 */
public final class DefaultSessionCollector implements ISessionCollector {
	private final Collection<ISession> sessions;
	private final String serverId;
	private final SessionFactory factory;

	public DefaultSessionCollector(String serverId, SessionFactory factory, Collection<ISession> sessions) {
		this.serverId = serverId;
		this.factory = factory;
		this.sessions = sessions;
	}

	@Override
	public void collect(int sessionId, String guid, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) throws OseeCoreException {
		Session session =
					factory.create(guid, userId, creationDate, serverId, clientVersion, clientMachineName, clientAddress,
								clientPort, lastInteractionDate, lastInteractionDetails);
		session.setId(sessionId);
		session.setStorageState(StorageState.LOADED);
		session.clearDirty();
		sessions.add(session);
	}
}