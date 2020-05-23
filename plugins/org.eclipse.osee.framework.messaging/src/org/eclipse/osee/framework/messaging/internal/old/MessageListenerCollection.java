/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.messaging.internal.old;

import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.ReceiveListener;
import org.eclipse.osee.framework.messaging.SendListener;
import org.eclipse.osee.framework.messaging.id.MessageId;

/**
 * @author Andrew M. Finkbeiner
 */
public class MessageListenerCollection {

   private final ConcurrentListMap<MessageId, ReceiveListener> receiveListeners;
   private final ConcurrentListMap<MessageId, SendListener> sendListeners;

   public MessageListenerCollection() {
      receiveListeners = new ConcurrentListMap<>();
      sendListeners = new ConcurrentListMap<>();
   }

   public boolean addReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      return receiveListeners.add(messageId, receiveListener);
   }

   public boolean addSendListener(MessageId messageId, SendListener sendListener) {
      return sendListeners.add(messageId, sendListener);
   }

   public boolean removeReceiveListener(MessageId messageId, ReceiveListener receiveListener) {
      return receiveListeners.remove(messageId, receiveListener);
   }

   public boolean removeSendListener(MessageId messageId, SendListener sendListener) {
      return sendListeners.remove(messageId, sendListener);
   }

   public void notifyReceiveListeners(Message message) {
      for (ReceiveListener listener : receiveListeners.get(message.getId())) {
         listener.handle(message);
      }
   }

   public void dispose() {
      receiveListeners.clear();
      sendListeners.clear();
   }

}
