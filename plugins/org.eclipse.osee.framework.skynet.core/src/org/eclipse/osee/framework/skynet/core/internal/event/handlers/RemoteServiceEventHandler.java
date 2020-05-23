/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import org.eclipse.osee.framework.skynet.core.event.listener.IRemoteEventManagerEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.RemoteEventServiceEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class RemoteServiceEventHandler implements EventHandlerLocal<IRemoteEventManagerEventListener, RemoteEventServiceEventType> {

   @Override
   public void handle(IRemoteEventManagerEventListener listener, Sender sender, RemoteEventServiceEventType event) {
      listener.handleRemoteEventManagerEvent(sender, event);
   }

   @Override
   public void send(Transport transport, Sender sender, RemoteEventServiceEventType event) {
      if (sender.isLocal() && event.isLocalEventType()) {
         transport.sendLocal(sender, event);
      }
   }
}
