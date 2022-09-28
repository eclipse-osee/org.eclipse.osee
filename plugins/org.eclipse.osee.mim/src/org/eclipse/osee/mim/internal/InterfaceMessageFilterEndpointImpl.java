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
import org.eclipse.osee.mim.InterfaceMessageFilterEndpoint;
import org.eclipse.osee.mim.types.InterfaceMessageToken;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageFilterEndpointImpl implements InterfaceMessageFilterEndpoint {

   private final BranchId branch;
   private final ArtifactId ConnectionId;
   private final InterfaceMessageApi messageApi;

   public InterfaceMessageFilterEndpointImpl(BranchId branch, ArtifactId connectionId, InterfaceMessageApi interfaceMessageApi) {
      this.branch = branch;
      this.messageApi = interfaceMessageApi;
      this.ConnectionId = connectionId;
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages(long pageNum, long pageSize) {
      Collection<InterfaceMessageToken> messageList =
         messageApi.getAllForConnection(branch, ConnectionId, pageNum, pageSize);
      return messageList;
   }

   @Override
   public Collection<InterfaceMessageToken> getMessages(String filter, long pageNum, long pageSize) {
      return messageApi.getAllForConnectionAndFilter(branch, ConnectionId, filter);
   }

}
