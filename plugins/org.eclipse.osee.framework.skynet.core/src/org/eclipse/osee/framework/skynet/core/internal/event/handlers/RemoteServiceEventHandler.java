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
   public void send(Transport transport, Sender sender, RemoteEventServiceEventType event)  {
      if (sender.isLocal() && event.isLocalEventType()) {
         transport.sendLocal(sender, event);
      }
   }
}
