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

import org.eclipse.osee.framework.core.OrcsTokenService;
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
   private final OrcsTokenService tokenService;

   public TransactionRemoteEventHandler(OrcsTokenService tokenService) {
      this.tokenService = tokenService;
   }

   @Override
   public void handle(Transport transport, Sender sender, RemoteTransactionEvent1 remoteEvent) {
      TransactionEvent transEvent = FrameworkEventUtil.getTransactionEvent(remoteEvent, tokenService);
      if (transEvent.getEventType() == TransactionEventType.Purged) {
         PurgeTransactionEventUtil.handleRemotePurgeTransactionEvent(transEvent);
         transport.send(sender, transEvent);
      } else {
         EventUtil.eventLog("REM: handleTransactionEvent - unhandled mod type [%s] ", transEvent.getEventType());
      }
   }
}
