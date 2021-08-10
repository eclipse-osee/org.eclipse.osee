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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.mim.InterfaceConnectionEndpoint;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceConnection;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceConnectionEndpointImpl implements InterfaceConnectionEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceConnectionEndpointImpl(BranchId branch, UserId account, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.account = account;
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi;
   }

   @Override
   public Collection<InterfaceConnection> getAllConnections() {
      try {
         return interfaceConnectionApi.getAccessor().getAll(branch, InterfaceConnection.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

   @Override
   public InterfaceConnection getConnection(ArtifactId ConnectionId) {
      try {
         return interfaceConnectionApi.getAccessor().get(branch, ConnectionId, InterfaceConnection.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

}
