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
import org.eclipse.osee.mim.InterfaceConnectionEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.types.InterfaceConnection;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnectionEndpointImpl implements InterfaceConnectionEndpoint {

   private final BranchId branch;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceConnectionEndpointImpl(BranchId branch, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.branch = branch;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
   }

   @Override
   public Collection<InterfaceConnection> getAllConnections(String filter, ArtifactId viewId, long pageNum,
      long pageSize, AttributeTypeToken orderByAttributeType) {
      return interfaceConnectionApi.getAll(branch, filter, pageNum, pageSize, orderByAttributeType, viewId);
   }

   @Override
   public InterfaceConnection getConnection(ArtifactId ConnectionId) {
      return interfaceConnectionApi.get(branch, ConnectionId);
   }

   @Override
   public int getAllConnectionsCount(String filter, ArtifactId viewId) {
      return interfaceConnectionApi.getCount(branch, filter, viewId);
   }

}
