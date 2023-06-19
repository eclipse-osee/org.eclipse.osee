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
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.InterfaceConnectionViewApi;
import org.eclipse.osee.mim.InterfaceNodeEndpoint;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.InterfaceNode;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNodeEndpointImpl implements InterfaceNodeEndpoint {

   private final BranchId branch;
   private final InterfaceNodeViewApi interfaceNodeApi;
   private final InterfaceConnectionViewApi interfaceConnectionApi;

   public InterfaceNodeEndpointImpl(BranchId branch, InterfaceNodeViewApi interfaceNodeApi, InterfaceConnectionViewApi interfaceConnectionViewApi) {
      this.branch = branch;
      this.interfaceNodeApi = interfaceNodeApi;
      this.interfaceConnectionApi = interfaceConnectionViewApi; //leaving this in here in case it's needed at a future date
   }

   @Override
   public Collection<InterfaceNode> getAllNodes(long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
      return interfaceNodeApi.getAll(branch, pageNum, pageSize, orderByAttributeType);
   }

   @Override
   public InterfaceNode getNode(ArtifactId nodeId) {
      try {
         return interfaceNodeApi.getAccessor().get(branch, nodeId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

   @Override
   public Collection<InterfaceNode> getNodes(ArtifactId connectionId) {
      try {
         List<InterfaceNode> nodes = new LinkedList<>();
         nodes.addAll(interfaceNodeApi.getAccessor().getAllByRelation(branch,
            CoreRelationTypes.InterfaceConnectionNode_Connection, connectionId));
         return nodes;
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return null;
   }

   @Override
   public Collection<InterfaceNode> getNodesByName(String name, long pageNum, long pageSize) {
      return this.interfaceNodeApi.getNodesByName(branch, name, pageNum, pageSize);
   }

   @Override
   public int getNodesByNameCount(String name) {
      return this.interfaceNodeApi.getNodesByNameCount(branch, name);
   }

}