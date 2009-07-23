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

import java.util.List;
import java.util.Set;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public interface IMessageSubscription {
   Message getMessage();

   void cancel();

   String getMessageClassName();

   MessageMode getMessageMode();

   MemType getMemType();

   /**
    * returns whether or not the subscription has been activated. A subscription is considered activated if and only if
    * a proper connection to a test server has been established and a successful registration of the subscription is
    * made. A subscription is not bound to any instance of a test server. A subscription is honored even when switching
    * between different test servers.
    */
   boolean isActive();

   boolean isResolved();

   void changeMemType(MemType type);

   void send() throws Exception;

   void setElementValue(List<Object> path, String value) throws Exception;

   void zeroize(List<Object> path) throws Exception;

   void changeMessageMode(MessageMode mode);

   Set<MemType> getAvailableTypes();

   boolean addSubscriptionListener(ISubscriptionListener listener);

   boolean removeSubscriptionListener(ISubscriptionListener listener);
}
