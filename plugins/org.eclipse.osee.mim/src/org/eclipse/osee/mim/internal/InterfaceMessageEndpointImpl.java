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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageEndpointImpl implements InterfaceMessageEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId ConnectionId;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;
   private final InterfaceNodeViewApi interfaceNodeApi;

   public InterfaceMessageEndpointImpl(BranchId branch, ArtifactId connectionId, UserId account, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi, InterfaceNodeViewApi interfaceNodeApi) {
      this.account = account;
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
      this.ConnectionId = connectionId;
      this.interfaceNodeApi = interfaceNodeApi;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllMessages() {
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

            message.setInitiatingNode(interfaceNodeApi.getAccessor().getByRelationWithoutId(branch,
               CoreRelationTypes.InterfaceMessageSendingNode_Message, ArtifactId.valueOf(message.getId()),
               InterfaceNode.class));
         }
         return messageList;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public InterfaceMessageToken getInterfaceMessage(ArtifactId messageId) {
      try {
         InterfaceMessageToken message = this.messageApi.getAccessor().getByRelation(branch, messageId,
            CoreRelationTypes.InterfaceConnectionContent_Connection, ConnectionId, InterfaceMessageToken.class);
         List<InterfaceSubMessageToken> submessages = new LinkedList<InterfaceSubMessageToken>();
         for (InterfaceSubMessageToken submessage : this.subMessageApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
            InterfaceSubMessageToken.class)) {
            submessages.add(submessage);
         }
         message.setSubMessages(submessages);

         message.setInitiatingNode(interfaceNodeApi.getAccessor().getByRelationWithoutId(branch,
            CoreRelationTypes.InterfaceMessageSendingNode_Message, ArtifactId.valueOf(message.getId()),
            InterfaceNode.class));

         return message;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

}
