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
package org.eclipse.osee.ote.client.msg.core.internal;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.osee.ote.client.msg.core.IMessageSubscription;
import org.eclipse.osee.ote.client.msg.core.ISubscriptionListener;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState;
import org.eclipse.osee.ote.client.msg.core.internal.state.UnresolvedState;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.commands.SetElementValue;
import org.eclipse.osee.ote.message.commands.ZeroizeElement;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 *
 */
public class MessageSubscription implements IMessageSubscription {

	private ISubscriptionState currentState = null;
	private final MessageSubscriptionService msgService;
	private final HashSet<ISubscriptionListener> listeners = new HashSet<ISubscriptionListener>();
	
	/**
	 * creates a subscription with no reference to a message
	 * 
	 * @param name
	 * @param subscriber
	 */
	public MessageSubscription(MessageSubscriptionService msgService) {
		this.msgService = msgService;
	}

	public synchronized void bind(String name) {
		bind(name, null, MessageMode.READER);
	}

	private void bind(String name, MemType type, MessageMode mode) {
		currentState = new UnresolvedState(name, this, type, mode);
	}

	@Override
	public synchronized boolean isActive() {
		return currentState != null ? currentState.isActive() : false;
	}

	@Override
	public synchronized boolean isResolved() {
		return currentState != null ? currentState.isResolved() : false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.IMessageSubscription#getMemType()
	 */
	@Override
	public synchronized MemType getMemType() {
		return currentState.getMemType();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.IMessageSubscription#getMessageMode
	 * ()
	 */
	@Override
	public synchronized MessageMode getMessageMode() {
		return currentState.getMode();
	}

	@Override
	public synchronized String getMessageClassName() {
		return currentState.getMsgClassName();
	}

	@Override
	public synchronized Message getMessage() {
		return currentState.getMessage();
	}


	public synchronized void attachMessageDb(AbstractMessageDataBase msgDb) {
		currentState = currentState.onMessageDbFound(msgDb);
	}

	public synchronized void detachMessageDb(AbstractMessageDataBase msgDb) {
		currentState = currentState.onMessageDbClosing(msgDb);
	}

	public synchronized void attachService(IRemoteMessageService service) {
		currentState = currentState.onActivated();
	}

	public synchronized void detachService(IRemoteMessageService service) {
		currentState = currentState.onDeactivated();
	}

	private void doCancel() {
		if (currentState != null) {
			currentState.onCanceled();
			currentState = null;
		}
	}

	@Override
	public synchronized Set<MemType> getAvailableTypes() {
		return currentState.getAvailableTypes();
	}

	@Override
	public synchronized void changeMessageMode(MessageMode mode) {
		if (mode == getMessageMode()) {
			return;
		}
		String name = getMessageClassName();
		MemType type = getMemType();
		notifyCanceled();
		doCancel();
		bind(name, type, mode);
		progressState();
	}

	@Override
	public synchronized void cancel() {
		if (currentState == null) {
			return;
		}
		try {
			notifyCanceled();
		} finally {
			doCancel();
			msgService.removeSubscription(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.IMessageSubscription#changeMemType
	 * (org.eclipse.osee.ote.message.enums.MemType)
	 */
	@Override
	public synchronized void changeMemType(MemType type) {
		if (type == getMemType()) {
			return;
		}
		String name = getMessageClassName();
		MessageMode mode = getMessageMode();
		doCancel();
		bind(name, type, mode);
		progressState();
	}

	private void progressState() {
		if (msgService.getMsgDatabase() != null) {
			attachMessageDb(msgService.getMsgDatabase());
			if (msgService.getService() != null) {
				attachService(msgService.getService());
			}
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.IMessageSubscription#setElementValue
	 * (java.util.List, java.lang.String)
	 */
	@Override
	public void setElementValue(List<Object> path, String value) throws Exception {
		final SetElementValue cmd = new SetElementValue(getMessageClassName(), getMemType(), path, value);
		msgService.getService().setElementValue(cmd);
	}

	@Override
	public void send() throws Exception {
		final SetElementValue cmd = new SetElementValue(getMessageClassName(), getMemType(), null, null);
		msgService.getService().setElementValue(cmd);
	}

	@Override
	public void zeroize(List<Object> path) throws Exception {
		final ZeroizeElement cmd = new ZeroizeElement(getMessageClassName(), getMemType(), path);
		msgService.getService().zeroizeElement(cmd);
	}
	
	public void notifyCanceled() {
		for (ISubscriptionListener listener : listeners) {
			listener.subscriptionCanceled(MessageSubscription.this);
		}
	}
	
	public void notifyActivated() {
		for (ISubscriptionListener listener : listeners) {
			listener.subscriptionActivated(MessageSubscription.this);
		}
	}
	
	
	public void notifyInvalidated() {
		for (ISubscriptionListener listener : listeners) {
			listener.subscriptionInvalidated(MessageSubscription.this);
		}
	}
	
	public void notifyResolved() {
		for (ISubscriptionListener listener : listeners) {
			listener.subscriptionResolved(MessageSubscription.this);
		}
	}
	
	public void notifyUnresolved() {
		for (ISubscriptionListener listener : listeners) {
			listener.subscriptionUnresolved(MessageSubscription.this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.client.msg.core.IMessageSubscription#
	 * addSubscriptionListener
	 * (org.eclipse.osee.ote.client.msg.core.ISubscriptionListener)
	 */
	@Override
	public boolean addSubscriptionListener(ISubscriptionListener listener) {
		boolean result = listeners.add(listener);
		if (currentState == null) {
			listener.subscriptionCanceled(this);
		} else {
			if (msgService.getMsgDatabase() != null) {
				// a database is available
				if (currentState.isResolved()) {
					listener.subscriptionResolved(this);
				} else {
					listener.subscriptionInvalidated(this);
				}
				if (currentState.isActive()) {
					listener.subscriptionActivated(this);
				}
			}

		}
		
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.osee.ote.client.msg.core.IMessageSubscription#
	 * removeSubscriptionListener
	 * (org.eclipse.osee.ote.client.msg.core.ISubscriptionListener)
	 */
	@Override
	public boolean removeSubscriptionListener(ISubscriptionListener listener) {
		return listeners.remove(listener);
	}
}
