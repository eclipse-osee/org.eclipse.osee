/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessageFilterEndpoint;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageFilterEndpointImpl implements InterfaceMessageFilterEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;

   public InterfaceMessageFilterEndpointImpl(BranchId branch, UserId account, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi) {
      this.account = account;
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages() {
      try {
         List<InterfaceMessageToken> messageList =
            (List<InterfaceMessageToken>) messageApi.getAccessor().getAll(branch, InterfaceMessageToken.class);
         for (InterfaceMessageToken message : messageList) {
            message.setSubMessages((List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelation(
               branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
               InterfaceSubMessageToken.class));
         }
         return messageList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages(String filter) {
      try {
         List<InterfaceMessageToken> messageList =
            (List<InterfaceMessageToken>) messageApi.getAccessor().getAllByFilter(branch, filter,
               InterfaceMessageToken.class);
         for (InterfaceMessageToken message : messageList) {
            message.setSubMessages((List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelation(
               branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
               InterfaceSubMessageToken.class));
         }

         List<InterfaceSubMessageToken> subMessages =
            (List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByFilter(branch, filter,
               InterfaceSubMessageToken.class);
         for (InterfaceSubMessageToken subMessage : subMessages) {
            List<InterfaceMessageToken> alternateMessageList =
               (List<InterfaceMessageToken>) messageApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage,
                  ArtifactId.valueOf(subMessage.getId()), InterfaceMessageToken.class);
            for (InterfaceMessageToken alternateMessage : alternateMessageList) {
               alternateMessage.setSubMessages(
                  (List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelationAndFilter(branch,
                     CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
                     ArtifactId.valueOf(alternateMessage.getId()), filter, InterfaceSubMessageToken.class));
               if (!messageList.contains(alternateMessage)) {
                  messageList.add(alternateMessage);
               }
            }
         }
         return messageList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

}
