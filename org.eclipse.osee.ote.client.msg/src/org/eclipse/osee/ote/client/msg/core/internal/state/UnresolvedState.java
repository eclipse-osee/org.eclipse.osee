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

import java.util.EnumSet;
import java.util.Set;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.client.msg.core.db.MessageInstance;
import org.eclipse.osee.ote.client.msg.core.internal.MessageSubscription;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 *
 */
public class UnresolvedState extends AbstractSubscriptionState {

	private MemType type;

	private final String msgClassName;
	private MessageInstance instance = null;
	
	public UnresolvedState(String msgClassName, MessageSubscription subscription, MemType type, MessageMode mode) {
		super(subscription, type, mode);
		this.type = type;
		this.msgClassName = msgClassName;
	}

	public UnresolvedState(String msgClassName, AbstractSubscriptionState previousState) {
		super(previousState);
		this.msgClassName = msgClassName;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #getMessage()
	 */
	@Override
	public Message getMessage() {
		return instance != null ? instance.getMessage() : null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #getMsgClassName()
	 */
	@Override
	public String getMsgClassName() {
		return msgClassName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #onLibraryLoaded()
	 */
	@Override
	public ISubscriptionState onMessageDbFound(AbstractMessageDataBase msgDB) {
		try {
			instance = msgDB.acquireInstance(msgClassName, getMode(), getMemType());
			this.type = instance.getType();
			getSubscription().notifyResolved();
			return new InactiveState(instance, msgDB, this);
		} catch (Exception e) {
			OseeLog.log(UnresolvedState.class, Level.SEVERE, "problems acquring instance for " + getMsgClassName(), e);
			getSubscription().notifyInvalidated();
			return this;
		}
		
	}

	@Override
	public MemType getMemType() {
		return type;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #onLibraryUnloaded()
	 */
	@Override
	public ISubscriptionState onMessageDbClosing(AbstractMessageDataBase msgDb) {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #onServiceAttached()
	 */
	@Override
	public ISubscriptionState onActivated() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #onServiceDetached()
	 */
	@Override
	public ISubscriptionState onDeactivated() {
		return this;
	}

	@Override
	public Set<MemType> getAvailableTypes() {
		return EnumSet.noneOf(MemType.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #isActive()
	 */
	@Override
	public boolean isActive() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.osee.ote.client.msg.core.internal.state.ISubscriptionState
	 * #isResolved()
	 */
	@Override
	public boolean isResolved() {
		return instance != null;
	}
}
