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

import org.eclipse.osee.ote.client.msg.core.internal.MessageSubscription;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 *
 */
public abstract class AbstractSubscriptionState implements ISubscriptionState {

	private final MemType type;
	private final MessageMode mode;
	private final MessageSubscription subscription;
	
	protected AbstractSubscriptionState(MessageSubscription subscription, MemType type, MessageMode mode) {
		this.subscription = subscription;
		this.type = type;
		this.mode = mode;
	}
	
	protected AbstractSubscriptionState(AbstractSubscriptionState otherState) {
		this.subscription = otherState.getSubscription();
		this.type = otherState.getMemType();
		this.mode = otherState.getMode();
	}
	

	@Override
	public MemType getMemType() {
		return type;
	}

	@Override
	public MessageMode getMode() {
		return mode;
	}
	
	protected MessageSubscription getSubscription() {
		return subscription;
	}
	
	@Override
	public void onCanceled() {
	}
	
}
