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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.accessor.types.ArtifactAccessorResultWithoutGammas;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.mim.InterfaceMessageTypeApi;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Luciano T. Vaglienti
 */
public class InterfaceMessageTypeApiImpl implements InterfaceMessageTypeApi {
   private ArtifactAccessor<ArtifactAccessorResultWithoutGammas> accessor;
   private final List<AttributeTypeId> attributes;
   public InterfaceMessageTypeApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new InterfaceMessageTypeAccessor(orcsApi));
      attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
   }

   private void setAccessor(ArtifactAccessor<ArtifactAccessorResultWithoutGammas> interfaceMessageTypeAccessor) {
      this.accessor = interfaceMessageTypeAccessor;
   }

   @Override
   public ArtifactAccessorResultWithoutGammas get(BranchId branch, ArtifactId messageTypeId) {
      try {
         return this.accessor.get(branch, messageTypeId);
      } catch (Exception ex) {
         return new ArtifactAccessorResultWithoutGammas();
      }
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch) {
      return this.getAll(branch, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId) {
      return this.getAll(branch, viewId, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, ArtifactId.SENTINEL, pageNum, pageSize);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize) {
      return this.getAll(branch, viewId, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAll(branch, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<ArtifactAccessorResultWithoutGammas>();
      }
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.getAllByFilter(branch, viewId, filter, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize) {
      return this.getAllByFilter(branch, viewId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ArtifactAccessorResultWithoutGammas> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAllByFilter(branch, filter, attributes, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<ArtifactAccessorResultWithoutGammas>();
      }
   }

   @Override
   public int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name), viewId);
   }

}
