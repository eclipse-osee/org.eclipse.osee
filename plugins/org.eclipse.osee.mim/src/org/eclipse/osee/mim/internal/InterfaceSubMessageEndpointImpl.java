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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.mim.InterfaceSubMessageApi;
import org.eclipse.osee.mim.InterfaceSubMessageEndpoint;
import org.eclipse.osee.mim.types.InterfaceSubMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceSubMessageEndpointImpl implements InterfaceSubMessageEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final ArtifactId messageId;
   private final InterfaceSubMessageApi subMessageApi;

   public InterfaceSubMessageEndpointImpl(BranchId branch, UserId account, ArtifactId messageId2, InterfaceSubMessageApi interfaceSubMessageApi) {
      this.account = account;
      this.branch = branch;
      this.messageId = messageId2;
      this.subMessageApi = interfaceSubMessageApi;
   }

   @Override
   public Collection<InterfaceSubMessageToken> getAllSubMessages() {
      try {
         return subMessageApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceMessageSubMessageContent_Message, messageId, InterfaceSubMessageToken.class);
      } catch (Exception ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData createNewSubMessage(InterfaceSubMessageToken token) {
      XResultData createResults = subMessageApi.getInserter().addArtifact(token, account, branch);
      createResults.merge(subMessageApi.getInserter().relateArtifact(ArtifactId.valueOf(createResults.getIds().get(0)),
         messageId, CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage, branch, account));
      return createResults;
   }

   @Override
   public XResultData updateSubMessage(InterfaceSubMessageToken token) {
      return subMessageApi.getInserter().replaceArtifact(token, account, branch);
   }

   @Override
   public XResultData patchSubMessage(InterfaceSubMessageToken token) {
      return subMessageApi.getInserter().patchArtifact(token, account, branch);
   }

   @Override
   public InterfaceSubMessageToken getSubMessage(ArtifactId subMessageId) {
      try {
         return subMessageApi.getAccessor().get(branch, subMessageId, InterfaceSubMessageToken.class);
      } catch (Exception ex) {
         System.out.println(ex);
         return null;
      }
   }

   @Override
   public XResultData relateSubMessage(ArtifactId subMessageId) {
      return subMessageApi.getInserter().relateArtifact(subMessageId, messageId,
         CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage, branch, account);
   }

   @Override
   public XResultData removeSubMessage(ArtifactId subMessageId) {
      return subMessageApi.getInserter().unrelateArtifact(subMessageId, messageId,
         CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage, branch, account);
   }
}
