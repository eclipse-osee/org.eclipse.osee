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
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
   private final ArtifactId ConnectionId;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;

   public InterfaceMessageFilterEndpointImpl(BranchId branch, ArtifactId connectionId, UserId account, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi) {
      this.account = account;
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
      this.ConnectionId = connectionId;
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages() {
      try {
         List<InterfaceMessageToken> messageList =
            (List<InterfaceMessageToken>) messageApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceConnectionContent_Connection, ConnectionId, InterfaceMessageToken.class);
         for (InterfaceMessageToken message : messageList) {
            List<InterfaceSubMessageToken> submessages = new LinkedList<InterfaceSubMessageToken>();
            for (InterfaceSubMessageToken submessage : this.subMessageApi.getAccessor().getAllByRelation(branch,
               CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
               InterfaceSubMessageToken.class)) {
               submessages.add(submessage);
            }
            message.setSubMessages(submessages);
         }
         return messageList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   private List<AttributeTypeId> createMessageAttributes() {
      List<AttributeTypeId> messageAttributes = new LinkedList<AttributeTypeId>();
      messageAttributes.add(CoreAttributeTypes.Name);
      messageAttributes.add(CoreAttributeTypes.Description);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageNumber);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessagePeriodicity);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageRate);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageWriteAccess);
      messageAttributes.add(CoreAttributeTypes.InterfaceMessageType);
      return messageAttributes;
   }

   private List<AttributeTypeId> createSubmessageAttributes() {
      List<AttributeTypeId> subMessageAttributes = new LinkedList<AttributeTypeId>();
      subMessageAttributes.add(CoreAttributeTypes.Name);
      subMessageAttributes.add(CoreAttributeTypes.Description);
      subMessageAttributes.add(CoreAttributeTypes.InterfaceSubMessageNumber);
      return subMessageAttributes;
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages(String filter) {
      List<AttributeTypeId> messageAttributes = this.createMessageAttributes();
      List<AttributeTypeId> subMessageAttributes = this.createSubmessageAttributes();
      try {
         List<InterfaceMessageToken> messageList =
            (List<InterfaceMessageToken>) messageApi.getAccessor().getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceConnectionContent_Connection, ConnectionId, filter, messageAttributes,
               InterfaceMessageToken.class);
         for (InterfaceMessageToken message : messageList) {
            message.setSubMessages((List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelation(
               branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
               InterfaceSubMessageToken.class));
         }

         List<InterfaceSubMessageToken> subMessages =
            (List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByFilter(branch, filter,
               subMessageAttributes, InterfaceSubMessageToken.class);
         for (InterfaceSubMessageToken subMessage : subMessages) {
            List<InterfaceMessageToken> alternateMessageList =
               (List<InterfaceMessageToken>) messageApi.getAccessor().getAllByRelation(branch,
                  CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage,
                  ArtifactId.valueOf(subMessage.getId()), InterfaceMessageToken.class);
            for (InterfaceMessageToken alternateMessage : alternateMessageList) {
               alternateMessage.setSubMessages(
                  (List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelationAndFilter(branch,
                     CoreRelationTypes.InterfaceMessageSubMessageContent_Message,
                     ArtifactId.valueOf(alternateMessage.getId()), filter, subMessageAttributes,
                     InterfaceSubMessageToken.class));
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
