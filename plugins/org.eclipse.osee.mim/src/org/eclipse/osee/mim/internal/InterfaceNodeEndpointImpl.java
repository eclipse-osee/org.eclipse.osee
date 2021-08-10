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
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceNodeEndpoint;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceNode;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNodeEndpointImpl implements InterfaceNodeEndpoint {

   private final BranchId branch;
   private final UserId account;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceNodeEndpointImpl(BranchId branch, UserId account, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.account = account;
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi; //leaving this in here in case it's needed at a future date
   }

   @Override
   public Collection<InterfaceNode> getAllNodes() {
      try {
         return interfaceNodeApi.getAccessor().getAll(branch, InterfaceNode.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

   @Override
   public InterfaceNode getNode(ArtifactId nodeId) {
      try {
         return interfaceNodeApi.getAccessor().get(branch, nodeId, InterfaceNode.class);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

}
