/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.client.msg.core.internal;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.osee.ote.client.msg.core.db.AbstractMessageDataBase;
import org.eclipse.osee.ote.client.msg.core.db.MessageInstance;
import org.eclipse.osee.ote.message.Message;
import org.eclipse.osee.ote.message.data.MessageData;
import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;

/**
 * @author Ken J. Aguilar
 */
public class MessageDatabase extends AbstractMessageDataBase {

   public MessageDatabase(IMsgToolServiceClient service) {
      super(service);
   }

   @Override
   protected Message createMessage(Class<? extends Message> msgClass) throws Exception {
      Message msg = msgClass.newInstance();
      LinkedList<MessageData> source = new LinkedList<>();
      Map<DataType, Class<? extends Message>[]> messages = msg.getAssociatedMessages();
      Set<Entry<DataType, Class<? extends Message>[]>> entrySet = messages.entrySet();
      for (Entry<DataType, Class<? extends Message>[]> entry : entrySet) {
         for (Class<? extends Message> clazz : entry.getValue()) {
            MessageInstance instance = acquireInstance(clazz.getName());
            Message newMsg = instance.getMessage();
            source.add(newMsg.getActiveDataSource());
            msg.addMessageTypeAssociation(entry.getKey(), newMsg);
         }
      }
      if (!source.isEmpty()) {
         msg.addMessageDataSource(source);
      }
      return msg;
   }

   @Override
   protected void destroyMessage(Message message) throws Exception {
      try {
         Map<DataType, Class<? extends Message>[]> messages = message.getAssociatedMessages();
         Set<Entry<DataType, Class<? extends Message>[]>> entrySet = messages.entrySet();
         for (Entry<DataType, Class<? extends Message>[]> entry : entrySet) {
            for (Class<? extends Message> clazz : entry.getValue()) {
               MessageInstance instance = findInstance(clazz.getName(), MessageMode.READER, entry.getKey());
               if (instance != null) {
                  releaseInstance(instance);
               }
            }
         }
      } finally {
         message.destroy();
      }
   }

}
