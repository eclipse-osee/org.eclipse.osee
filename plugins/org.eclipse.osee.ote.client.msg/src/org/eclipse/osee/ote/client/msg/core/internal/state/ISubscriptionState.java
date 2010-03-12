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
package org.eclipse.osee.ote.client.msg.core.internal.state;

import java.util.Set;

import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 *
 */
public interface ISubscriptionState {
	MemType getMemType();

	MessageMode getMode();

	String getMsgClassName();

	Message getMessage();

	Set<MemType> getAvailableTypes();
	
	ISubscriptionState onMessageDbFound(AbstractMessageDataBase msgDB);

	ISubscriptionState onMessageDbClosing(AbstractMessageDataBase msgDb);

	ISubscriptionState onActivated();

	ISubscriptionState onDeactivated();
	
	void onCanceled();
	
	boolean isActive();

	boolean isResolved();
	
}
