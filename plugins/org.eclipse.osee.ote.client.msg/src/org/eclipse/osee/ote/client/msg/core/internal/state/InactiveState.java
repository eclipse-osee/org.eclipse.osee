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
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.MemType;

/**
 * @author Ken J. Aguilar
 *
 */
public class InactiveState extends AbstractSubscriptionState {


	private final MessageInstance instance;
	private final AbstractMessageDataBase msgDb;
	
	public InactiveState(MessageInstance instance, AbstractMessageDataBase msgDb, AbstractSubscriptionState previousState) {
		super(previousState);
		this.instance = instance;
		this.msgDb = msgDb;
	}
	

	@Override
	public Message getMessage() {
		return instance.getMessage();
	}

	@Override
	public String getMsgClassName() {
		return instance.getMessage().getClass().getName();
	}

	@Override
	public ISubscriptionState onMessageDbFound(AbstractMessageDataBase msgDB) {
		throw new Error("Unexpected input for this state");
	}

	@Override
	public ISubscriptionState onMessageDbClosing(AbstractMessageDataBase msgDb) {
		assert this.msgDb == msgDb;
		getSubscription().notifyUnresolved();
		try {
			msgDb.releaseInstance(instance);
		} catch (Exception e) {
			OseeLog.log(ActivateState.class, Level.SEVERE, "problem releasing instance of " + getMsgClassName());
		}
		return new UnresolvedState(instance.getMessage().getName(), this);
	}
	
	@Override
	public void onCanceled() {
		super.onCanceled();
		try {
			msgDb.releaseInstance(instance);
		} catch (Exception e) {
			OseeLog.log(ActivateState.class, Level.SEVERE, "problem releasing instance of " + getMsgClassName());
		}
	}

	@Override
	public ISubscriptionState onActivated() {
		if (instance.isSupported()) {
			getSubscription().notifyActivated();
			return new ActivateState(instance, msgDb, this);
		} else {
			return this;
		}

	}

	@Override
	public ISubscriptionState onDeactivated() {
		return this;
	}


	@Override
	public Set<MemType> getAvailableTypes() {
		return EnumSet.noneOf(MemType.class);
	}


	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public boolean isResolved() {
		return true;
	}
}
