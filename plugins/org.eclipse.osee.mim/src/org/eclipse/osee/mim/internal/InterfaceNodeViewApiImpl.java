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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.InterfaceNodeViewApi;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.InterfaceNode;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.PLGenericDBObject;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceNodeViewApiImpl implements InterfaceNodeViewApi {

   private ArtifactAccessor<InterfaceNode> accessor;

   InterfaceNodeViewApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceNodeAccessor(orcsApi));
   }

   @Override
   public ArtifactAccessor<InterfaceNode> getAccessor() {
      return this.accessor;
   }

   /**
    * @param accessor the accessor to set
    */
   public void setAccessor(ArtifactAccessor<InterfaceNode> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<InterfaceNode> query(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, false);
   }

   @Override
   public InterfaceNode getNodeForMessage(BranchId branch, ArtifactId message) {
      try {
         return this.getAccessor().getByRelationWithoutId(branch, CoreRelationTypes.InterfaceMessageSendingNode_Message,
            ArtifactId.valueOf(message.getId()));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return (InterfaceNode) PLGenericDBObject.SENTINEL;
   }

   @Override
   public InterfaceNode get(BranchId branch, ArtifactId nodeId) {
      try {
         return this.getAccessor().get(branch, nodeId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return InterfaceNode.SENTINEL;
   }

   @Override
   public Collection<InterfaceNode> queryExact(BranchId branch, MimAttributeQuery query) {
      return this.query(branch, query, true);
   }

   @Override
   public Collection<InterfaceNode> query(BranchId branch, MimAttributeQuery query, boolean isExact) {
      return this.query(branch, query, isExact, 0L, 0L);
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId) {
      try {
         //nodes currently don't have affected artifacts
         return this.getAccessor().getAffectedArtifacts(branch, relatedId, new LinkedList());
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<ArtifactMatch>();
   }

   @Override
   public Collection<InterfaceNode> query(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, false, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceNode> queryExact(BranchId branch, MimAttributeQuery query, long pageNum, long pageSize) {
      return this.query(branch, query, true, pageNum, pageSize);
   }

   @Override
   public Collection<InterfaceNode> query(BranchId branch, MimAttributeQuery query, boolean isExact, long pageNum, long pageSize) {
      try {
         return this.getAccessor().getAllByQuery(branch, query, isExact, pageNum, pageSize);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<InterfaceNode>();
   }

   @Override
   public Collection<InterfaceNode> getAll(BranchId branch) {
      return this.getAll(branch, 0L, 0L);
   }

   @Override
   public Collection<InterfaceNode> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, pageNum, pageSize, AttributeTypeToken.SENTINEL);
   }

   @Override
   public Collection<InterfaceNode> getAll(BranchId branch, AttributeTypeToken orderByAttributeType) {
      return this.getAll(branch, 0L, 0L, orderByAttributeType);
   }

   @Override
   public Collection<InterfaceNode> getAll(BranchId branch, long pageNum, long pageSize, AttributeTypeToken orderByAttributeType) {
      try {
         return this.getAccessor().getAll(branch, pageNum, pageSize, orderByAttributeType);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
      }
      return new LinkedList<InterfaceNode>();
   }

}
