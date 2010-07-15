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

import java.util.Date;
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeTypeFactory;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.core.server.internal.BuildTypeIdentifier;
import org.eclipse.osee.framework.core.server.internal.compatibility.OseeSql_0_9_1;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.database.core.OseeSql;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public final class SessionFactory implements IOseeTypeFactory {
	private final BuildTypeIdentifier typeIdentifier;

	public SessionFactory(BuildTypeIdentifier typeIdentifier) {
		this.typeIdentifier = typeIdentifier;
	}

	public Session createOrUpdate(IOseeCache<Session> cache, int uniqueId, StorageState storageState, String guid, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) throws OseeCoreException {
		Session session = cache.getById(uniqueId);
		if (session == null) {
			session =
						create(guid, userId, creationDate, managedByServerId, clientVersion, clientMachineName,
									clientAddress, clientPort, lastInteractionDate, lastInteractionDetails);
			session.setId(uniqueId);
			session.setStorageState(storageState);
		} else {
			cache.decache(session);
			session.setClientMachineName(clientMachineName);
			session.setClientAddress(clientAddress);
			session.setClientPort(clientPort);
			session.setLastInteractionDate(lastInteractionDate);
			session.setLastInteractionDetails(lastInteractionDetails);
		}
		cache.cache(session);
		return session;
	}

	public Session create(String guid, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) {
		return new Session(guid, guid, userId, creationDate, managedByServerId, clientVersion, clientMachineName,
					clientAddress, clientPort, lastInteractionDate, lastInteractionDetails);
	}

	public OseeSessionGrant createSessionGrant(Session session, IOseeUserInfo oseeUserInfo) throws OseeCoreException {
		OseeSessionGrant sessionGrant = new OseeSessionGrant(session.getGuid());
		sessionGrant.setCreationRequired(oseeUserInfo.isCreationRequired());
		sessionGrant.setOseeUserInfo(oseeUserInfo);
		sessionGrant.setDatabaseInfo(DatabaseInfoManager.getDefault());
		if (is_0_9_2_Compatible(session.getClientVersion())) {
			sessionGrant.setSqlProperties(OseeSql.getSqlProperties(ConnectionHandler.getMetaData()));
		} else {
			sessionGrant.setSqlProperties(OseeSql_0_9_1.getSqlProperties(ConnectionHandler.getMetaData()));
		}
		sessionGrant.setDataStorePath(OseeServerProperties.getOseeApplicationServerData());

		sessionGrant.setClientBuildDesination(typeIdentifier.getBuildDesignation(session.getClientVersion()));
		return sessionGrant;
	}

	private static boolean is_0_9_2_Compatible(String clientVersion) {
		boolean result = false;
		if (Strings.isValid(clientVersion)) {
			String toCheck = clientVersion.toLowerCase();
			if (!toCheck.startsWith("0.9.0") && !toCheck.startsWith("0.9.1")) {
				result = true;
			}
		}
		return result;
	}
}
