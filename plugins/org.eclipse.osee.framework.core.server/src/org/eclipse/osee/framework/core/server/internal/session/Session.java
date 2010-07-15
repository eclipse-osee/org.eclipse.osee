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
import org.apache.commons.codec.binary.Base64;
import org.eclipse.osee.framework.core.model.AbstractOseeType;
import org.eclipse.osee.framework.core.model.OseeField;
import org.eclipse.osee.framework.core.server.ISession;

/**
 * @author Roberto E. Escobar
 */
public final class Session extends AbstractOseeType implements ISession, Comparable<ISession> {

	private static final String SESSION_CREATION_DATE = "session.creation.date";
	private static final String SESSION_USER_ID = "session.user.id";
	private static final String SESSION_MANAGED_BY_SERVER_ID = "session.managed.by.server.id";
	private static final String SESSION_CLIENT_MACHINE_NAME = "session.client.machine.name";
	private static final String SESSION_CLIENT_VERSION = "session.client.version";
	private static final String SESSION_CLIENT_ADDRESS = "session.client.address";
	private static final String SESSION_CLIENT_PORT = "session.client.port";
	private static final String SESSION_LAST_INTERACTION_DATE = "session.last.interaction.date";
	private static final String SESSION_LAST_INTERACTION_DETAILS = "session.last.interaction.details";

	public Session(String guid, String name, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String interactionDetails) {
		super(guid, name);
		initializeFields(userId, creationDate, managedByServerId, clientVersion, clientMachineName, clientAddress,
					clientPort, lastInteractionDate, interactionDetails);
	}

	protected void initializeFields(String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String interactionDetails) {
		addField(SESSION_CREATION_DATE, new OseeField<Date>(creationDate));
		addField(SESSION_USER_ID, new OseeField<String>(userId));
		addField(SESSION_MANAGED_BY_SERVER_ID, new OseeField<String>(managedByServerId));
		addField(SESSION_CLIENT_MACHINE_NAME, new OseeField<String>(clientMachineName));
		addField(SESSION_CLIENT_VERSION, new OseeField<String>(clientVersion));
		addField(SESSION_CLIENT_ADDRESS, new OseeField<String>(clientAddress));
		addField(SESSION_CLIENT_PORT, new OseeField<Integer>(clientPort));
		addField(SESSION_LAST_INTERACTION_DATE, new OseeField<Date>(lastInteractionDate));
		addField(SESSION_LAST_INTERACTION_DETAILS, new OseeField<String>(interactionDetails));
	}

	public static int guidAsInteger(String guid) {
		byte[] bytes = Base64.encodeBase64(guid.getBytes());
		int toReturn = 0;
		for (int index = 0; index < bytes.length; index++) {
			byte aByte = bytes[index];
			toReturn += aByte * index;
		}
		return toReturn;
	}

	@Override
	public String getUserId() {
		return getFieldValueLogException("", SESSION_USER_ID);
	}

	@Override
	public Date getLastInteractionDate() {
		return getFieldValueLogException(null, SESSION_LAST_INTERACTION_DATE);
	}

	@Override
	public String getManagedByServerId() {
		return getFieldValueLogException("", SESSION_MANAGED_BY_SERVER_ID);
	}

	@Override
	public String getLastInteractionDetails() {
		return getFieldValueLogException("", SESSION_LAST_INTERACTION_DETAILS);
	}

	@Override
	public String getClientMachineName() {
		return getFieldValueLogException("", SESSION_CLIENT_MACHINE_NAME);
	}

	@Override
	public String getClientVersion() {
		return getFieldValueLogException("", SESSION_CLIENT_VERSION);
	}

	@Override
	public String getClientAddress() {
		return getFieldValueLogException("", SESSION_CLIENT_ADDRESS);
	}

	@Override
	public Date getCreationDate() {
		return getFieldValueLogException(null, SESSION_CREATION_DATE);
	}

	@Override
	public int getClientPort() {
		return getFieldValueLogException(-1, SESSION_CLIENT_PORT);
	}

	@Override
	public void setLastInteractionDetails(String details) {
		setFieldLogException(SESSION_LAST_INTERACTION_DETAILS, details);
	}

	@Override
	public void setLastInteractionDate(Date date) {
		setFieldLogException(SESSION_LAST_INTERACTION_DATE, date);
	}

	public void setManagedByServerId(String managedByServerId) {
		setFieldLogException(SESSION_MANAGED_BY_SERVER_ID, managedByServerId);
	}

	public void setClientMachineName(String clientMachineName) {
		setFieldLogException(SESSION_CLIENT_MACHINE_NAME, clientMachineName);
	}

	public void setClientAddress(String clientAddress) {
		setFieldLogException(SESSION_CLIENT_ADDRESS, clientAddress);
	}

	public void setClientPort(int clientPort) {
		setFieldLogException(SESSION_CLIENT_PORT, clientPort);
	}

	@Override
	public int compareTo(ISession other) {
		int result = -1;
		if (other != null && other.getGuid() != null && getGuid() != null) {
			result = getGuid().compareTo(other.getGuid());
		}
		return result;
	}

}
