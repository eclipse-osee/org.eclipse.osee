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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.CrossReferenceApi;
import org.eclipse.osee.mim.types.CrossReference;
import org.eclipse.osee.orcs.OrcsApi;

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
      return this.getAll(branch, connectionId, filter, 0L, 0L);
   }

   @Override
   public CrossReference get(BranchId branch, ArtifactId artId) {
      try {
         return this.accessor.get(branch, artId);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new CrossReference();
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum, long pageSize) {
      return this.getAll(branch, connectionId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, connectionId, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<CrossReference> getAll(BranchId branch, ArtifactId connectionId, String filter, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      List<AttributeTypeId> attributes = Arrays.asList(CoreAttributeTypes.Name, CoreAttributeTypes.CrossReferenceValue,
         CoreAttributeTypes.CrossReferenceArrayValues);
      try {
         if (filter.isEmpty()) {
            return this.accessor.getAllByRelation(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId, pageNum, pageSize,
               orderByAttribute);
         } else {
            return this.accessor.getAllByRelationAndFilter(branch,
               CoreRelationTypes.InterfaceConnectionCrossReference_InterfaceConnection, connectionId, filter,
               attributes, pageNum, pageSize, orderByAttribute);
         }
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         //
      }
      return new LinkedList<CrossReference>();
   }

}
