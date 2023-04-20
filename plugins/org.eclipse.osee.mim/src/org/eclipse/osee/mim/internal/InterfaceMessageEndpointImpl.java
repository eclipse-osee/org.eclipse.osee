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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.types.InterfaceMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageEndpointImpl implements InterfaceMessageEndpoint {

   private final BranchId branch;
   private final ArtifactId connectionId;
   private final InterfaceMessageApi messageApi;

   public InterfaceMessageEndpointImpl(BranchId branch, ArtifactId connectionId, InterfaceMessageApi interfaceMessageApi) {
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.connectionId = connectionId;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllMessages(String filter, ArtifactId viewId, long pageNum, long pageSize, AttributeTypeToken orderByAttributeTypeId) {
      if (Strings.isValid(filter)) {
         return this.messageApi.getAllForConnectionAndFilter(branch, connectionId, filter, viewId, pageNum, pageSize,
            orderByAttributeTypeId);
      }
      return this.messageApi.getAllForConnection(branch, connectionId, viewId, pageNum, pageSize,
         orderByAttributeTypeId);
   }

   @Override
   public InterfaceMessageToken getInterfaceMessage(ArtifactId messageId, ArtifactId viewId) {
      return this.messageApi.getRelatedToConnection(branch, connectionId, messageId, viewId);
   }

   @Override
   public int getAllMessagesCount(String filter, ArtifactId viewId) {
      if (Strings.isValid(filter)) {
         return this.messageApi.getAllForConnectionAndFilterCount(branch, connectionId, filter);
      } else {
         return this.messageApi.getAllForConnectionAndCount(branch, connectionId);
      }
   }

}
