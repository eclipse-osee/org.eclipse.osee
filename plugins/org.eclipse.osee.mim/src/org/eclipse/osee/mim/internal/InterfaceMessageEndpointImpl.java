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
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.types.InterfaceMessageToken;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageEndpointImpl implements InterfaceMessageEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceMessageApi messageApi;
   private final InterfaceSubMessageApi subMessageApi;

   public InterfaceMessageEndpointImpl(BranchId branch, UserId account, InterfaceMessageApi interfaceMessageApi, InterfaceSubMessageApi interfaceSubMessageApi) {
      this.account = account;
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.subMessageApi = interfaceSubMessageApi;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllMessages() {
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
   public XResultData addMessage(InterfaceMessageToken token) {
      return messageApi.getInserter().addArtifact(token, account, branch);
   }

   @Override
   public InterfaceMessageToken getInterfaceMessage(ArtifactId messageId) {
      try {
         InterfaceMessageToken message =
            this.messageApi.getAccessor().get(branch, messageId, InterfaceMessageToken.class);
         message.setSubMessages((List<InterfaceSubMessageToken>) this.subMessageApi.getAccessor().getAllByRelation(
            branch, CoreRelationTypes.InterfaceMessageSubMessageContent_Message, ArtifactId.valueOf(message.getId()),
            InterfaceSubMessageToken.class));
         return message;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData updateMessage(InterfaceMessageToken token) {
      return this.messageApi.getInserter().replaceArtifact(token, account, branch);
   }

   @Override
   public XResultData patchMessage(InterfaceMessageToken token) {
      return this.messageApi.getInserter().patchArtifact(token, account, branch);
   }

   @Override
   public XResultData removeInterfaceMessage(ArtifactId messageId) {
      return this.messageApi.getInserter().removeArtifact(messageId, account, branch);
   }

}
