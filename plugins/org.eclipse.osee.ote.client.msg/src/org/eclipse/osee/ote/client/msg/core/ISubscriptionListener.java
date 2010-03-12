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

import org.eclipse.osee.ote.message.Message;

/**
 * @author Ken J. Aguilar
 *
 */
public interface ISubscriptionListener {
	/**
	 * called when a subscription can no longer be fulfilled by the message
	 * service. this occurs when runtime libraries are changed and the message
	 * does not exist. Can be called when a message exist in the libraries but
	 * is not supported by the test environment. all references and event
	 * listeners must be removed
	 * 
	 * @param subscription
	 */
	void subscriptionInvalidated(IMessageSubscription subscription);

	/**
	 * called upon a successful acquisition of a {@link Message} instance. At
	 * this point, no message traffic is being transmitted but operations on the
	 * message are allowed. Subclasses should register any event listeners on
	 * {@link Message} upon this method being invoked.
	 * 
	 * @param subscription
	 */
	void subscriptionResolved(IMessageSubscription subscription);

	/**
	 * called when the message library has been unloaded. The subscription will
	 * still be honored by the system and can be reactivated upon a reload of a
	 * library assuming the library has a definition for the message. Subclasses
	 * should <B>must no longer reference</B> the ({@link Message} related to
	 * the subscription. Subclasses do not need to de-register any event
	 * listeners since all listeners will be cleared. The message is still in a
	 * valid state during this method invocation but not after.
	 * 
	 * @param subscription
	 */
	void subscriptionUnresolved(IMessageSubscription subscription);
	
	/**
	 * called upon successful registration with the test server. Message traffic
	 * is now possible
	 * 
	 * @param subscription
	 */
	void subscriptionActivated(IMessageSubscription subscription);

	/**
	 * called when the {@link IMessageSubscription#cancel()} method is called.
	 * Any references and event listeners must be removed.
	 * 
	 * @param subscription
	 */
	void subscriptionCanceled(IMessageSubscription subscription);

	/**
	 * called when the subscription is resolved but a connected environment does
	 * not support this type of message. The message can still be referenced but
	 * no updates will be delivered.
	 * 
	 * @param subscription
	 */
	void subscriptionNotSupported(IMessageSubscription subscription);
}
