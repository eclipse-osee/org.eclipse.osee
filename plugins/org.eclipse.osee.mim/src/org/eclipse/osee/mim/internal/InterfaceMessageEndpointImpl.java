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
import org.eclipse.osee.mim.InterfaceMessageApi;
import org.eclipse.osee.mim.InterfaceMessageEndpoint;
import org.eclipse.osee.mim.types.InterfaceMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageEndpointImpl implements InterfaceMessageEndpoint {

   private final BranchId branch;
   private final ArtifactId ConnectionId;
   private final InterfaceMessageApi messageApi;

   public InterfaceMessageEndpointImpl(BranchId branch, ArtifactId connectionId, InterfaceMessageApi interfaceMessageApi) {
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.ConnectionId = connectionId;
   }

   @Override
   public Collection<InterfaceMessageToken> getAllMessages(long pageNum, long pageSize) {
      return this.messageApi.getAllForConnection(branch, ConnectionId, pageNum, pageSize);
   }

   @Override
   public InterfaceMessageToken getInterfaceMessage(ArtifactId messageId) {
      return this.messageApi.getRelatedToConnection(branch, ConnectionId, messageId);
   }

}
