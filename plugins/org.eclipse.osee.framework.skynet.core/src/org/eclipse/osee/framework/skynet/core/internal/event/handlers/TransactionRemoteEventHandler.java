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

import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.EventUtil;
import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.PurgeTransactionEventUtil;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEventType;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerRemote;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class TransactionRemoteEventHandler implements EventHandlerRemote<RemoteTransactionEvent1> {

   @Override
   public void handle(Transport transport, Sender sender, RemoteTransactionEvent1 remoteEvent) {
      TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(remoteEvent);
      if (transEvent.getEventType() == TransactionEventType.Purged) {
         PurgeTransactionEventUtil.handleRemotePurgeTransactionEvent(transEvent);
         transport.send(sender, transEvent);
      } else {
         EventUtil.eventLog("REM: handleTransactionEvent - unhandled mod type [%s] ", transEvent.getEventType());
      }
   }
}
