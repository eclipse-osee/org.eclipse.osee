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
package org.eclipse.osee.framework.core.server;

import java.util.Date;
import org.eclipse.osee.framework.core.enums.StorageState;

/**
 * @author Roberto E. Escobar
 */
public interface ISession {
	String getUserId();

	Date getLastInteractionDate();

	String getManagedByServerId();

	String getLastInteractionDetails();

	String getClientMachineName();

	String getClientVersion();

	String getClientAddress();

	Date getCreationDate();

	int getClientPort();

	void setLastInteractionDetails(String string);

	void setLastInteractionDate(Date date);

	String getGuid();

	StorageState getStorageState();
}
