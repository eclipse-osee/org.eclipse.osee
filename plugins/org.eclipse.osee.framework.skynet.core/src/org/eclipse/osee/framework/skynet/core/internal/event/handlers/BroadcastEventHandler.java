/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.internal.event.handlers;

import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.IBroadcastEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BroadcastEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class BroadcastEventHandler implements EventHandlerLocal<IBroadcastEventListener, BroadcastEvent> {

   @Override
   public void handle(IBroadcastEventListener listener, Sender sender, BroadcastEvent event) {
      if (!event.getUsers().isEmpty()) {
         listener.handleBroadcastEvent(sender, event);
      }
   }

   @Override
   public void send(Transport transport, Sender sender, BroadcastEvent event) throws OseeCoreException {
      if (!event.getBroadcastEventType().isPingOrPong()) {
         EventUtil.eventLog("IEM: kickBroadcastEvent: type[%s] message[%s] sender[%s]",
            event.getBroadcastEventType().name(), event.getMessage(), sender);
      }
      if (sender.isRemote() || sender.isLocal() && event.getBroadcastEventType().isLocalEventType()) {
         transport.sendLocal(sender, event);
      }

      // Kick REMOTE (If source was Local and this was not a default branch changed event
      if (sender.isLocal() && event.getBroadcastEventType().isRemoteEventType()) {
         transport.sendRemote(FrameworkEventUtil.getRemoteBroadcastEvent(event));
      }
   }
}
