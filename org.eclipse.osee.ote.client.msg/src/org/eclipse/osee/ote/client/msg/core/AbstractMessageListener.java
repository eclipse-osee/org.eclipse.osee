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
package org.eclipse.osee.ote.client.msg.core;

import org.eclipse.osee.ote.message.MessageSystemException;
import org.eclipse.osee.ote.message.listener.IOSEEMessageListener;

/**
 * @author Ken J. Aguilar
 *
 */
public abstract class AbstractMessageListener implements ISubscriptionListener, IOSEEMessageListener {

	private final IMessageSubscription subscription;

	protected AbstractMessageListener(IMessageSubscription subscription) {
		this.subscription = subscription;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.client.msg.core.ISubscriptionListener#subscriptionCanceled(org.eclipse.osee.ote.client.msg.core.IMessageSubscription)
	 */
	@Override
	public void subscriptionCanceled(IMessageSubscription subscription) {
		if (subscription.isResolved()) {
			subscription.getMessage().removeListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.client.msg.core.ISubscriptionListener#subscriptionResolved(org.eclipse.osee.ote.client.msg.core.IMessageSubscription)
	 */
	@Override
	public void subscriptionResolved(IMessageSubscription subscription) {
		if (subscription.isResolved()) {
			subscription.getMessage().addListener(this);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.client.msg.core.ISubscriptionListener#subscriptionUnresolved(org.eclipse.osee.ote.client.msg.core.IMessageSubscription)
	 */
	@Override
	public void subscriptionUnresolved(IMessageSubscription subscription) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.osee.ote.message.listener.IOSEEMessageListener#onInitListener()
	 */
	@Override
	public void onInitListener() throws MessageSystemException {
	}

	public IMessageSubscription getSubscription() {
		return subscription;
	}
}
