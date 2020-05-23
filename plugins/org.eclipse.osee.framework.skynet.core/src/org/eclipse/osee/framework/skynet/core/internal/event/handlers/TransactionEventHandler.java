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

import org.eclipse.osee.framework.skynet.core.event.FrameworkEventUtil;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.event.EventHandlerLocal;
import org.eclipse.osee.framework.skynet.core.internal.event.Transport;

/**
 * @author Roberto E. Escobar
 */
public class TransactionEventHandler implements EventHandlerLocal<ITransactionEventListener, TransactionEvent> {

   @Override
   public void handle(ITransactionEventListener listener, Sender sender, TransactionEvent event) {
      listener.handleTransactionEvent(sender, event);
   }

   @Override
   public void send(Transport transport, Sender sender, TransactionEvent event) {
      if (transport.isDispatchToLocalAllowed(sender)) {
         transport.sendLocal(sender, event);
      }
      if (sender.isLocal()) {
         transport.sendRemote(FrameworkEventUtil.getRemoteTransactionEvent(event));
      }
   }
}
