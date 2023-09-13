/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.CrossReferenceApi;
import org.eclipse.osee.mim.types.CrossReference;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

public class CrossReferenceApiImpl implements CrossReferenceApi {

   private ArtifactAccessor<CrossReference> accessor;

   public CrossReferenceApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new CrossReferenceAccessor(orcsApi));
   }

   /**
    * @param accessor the accessor to set
    */
   private void setAccessor(ArtifactAccessor<CrossReference> accessor) {
      this.accessor = accessor;
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter) {
      return this.getAll(branch, connectionId, filter, ArtifactId.SENTINEL);
   }

   @Override
   public CrossReference get(BranchId branch, ArtifactId artId) {
      try {
         return this.accessor.get(branch, artId,
            FollowRelation.followList(CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection));
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new CrossReference();
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize) {
      return this.getAll(branch, connectionId, filter, pageNum, pageSize, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, connectionId, filter, orderByAttribute, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, connectionId, filter, pageNum, pageSize, orderByAttribute, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter,
      ArtifactId viewId) {
      return this.getAll(branch, connectionId, filter, 0L, 0L, viewId);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter,
      AttributeTypeId orderByAttribute, ArtifactId viewId) {
      return this.getAll(branch, connectionId, filter, 0L, 0L, orderByAttribute, viewId);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, ArtifactId viewId) {
      return this.getAll(branch, connectionId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL, viewId);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId) {
      List<AttributeTypeId> attributes = Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.CrossReferenceValue,
         CoreAttributeTypes.CrossReferenceArrayValues);
      try {
         if (filter.isEmpty() && connectionId.isValid()) {
            return this.accessor.getAllByRelation(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId,
               FollowRelation.followList(CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection),
               pageNum, pageSize, orderByAttribute, viewId);
         } else if (connectionId.isValid()) {
            return this.accessor.getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId, filter,
               attributes,
               FollowRelation.followList(CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection),
               pageNum, pageSize, orderByAttribute, viewId);
         } else if (filter.isEmpty()) {
            return this.accessor.getAll(branch,
               FollowRelation.followList(CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection),
               pageNum, pageSize, orderByAttribute, viewId);
         } else {
            return this.accessor.getAllByFilter(branch, filter, attributes,
               FollowRelation.followList(CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection),
               pageNum, pageSize, orderByAttribute, viewId);
         }
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<CrossReference>();
   }

   @Override
   public int getCount(BranchId branch, ArtifactId connectionId, String filter, ArtifactId viewId) {
      List<AttributeTypeId> attributes = Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.CrossReferenceValue,
         CoreAttributeTypes.CrossReferenceArrayValues);
      try {

         if (filter.isEmpty() && connectionId.isValid()) {
            return this.accessor.getAllByRelationAndCount(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId, viewId);
         } else if (connectionId.isValid()) {
            return this.accessor.getAllByRelationAndFilterAndCount(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId, filter,
               attributes, viewId);
         } else if (filter.isEmpty()) {
            return this.accessor.getAllByFilterAndCount(branch, filter, attributes, viewId);
         } else {
            return this.accessor.getAllByFilterAndCount(branch, filter, attributes, viewId);
         }
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return 0;
   }

}
