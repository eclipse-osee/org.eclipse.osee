/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.coverage.internal;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.accessor.ArtifactAccessor;
import org.eclipse.osee.coverage.CoverageProgramApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Stephen J. Molaro
 */
public class CoverageProgramApiImpl implements CoverageProgramApi {
   private ArtifactAccessor<CoverageProgramToken> accessor;
   private final List<AttributeTypeId> attributes;
   public CoverageProgramApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new CoverageProgramAccessor(orcsApi));
      attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
   }

   private void setAccessor(ArtifactAccessor<CoverageProgramToken> CoverageProgramAccessor) {
      this.accessor = CoverageProgramAccessor;
   }

   @Override
   public CoverageProgramToken get(BranchId branch, ArtifactId scriptDefTypeId) {
      try {
         return this.accessor.get(branch, scriptDefTypeId);
      } catch (Exception ex) {
         return new CoverageProgramToken(ex);
      }
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch) {
      return this.getAll(branch, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId) {
      return this.getAll(branch, viewId, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId,
      List<FollowRelation> followRelations) {
      return this.getAll(branch, viewId, followRelations, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, new LinkedList<>(), 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId,
      List<FollowRelation> followRelations, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, followRelations, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, ArtifactId.SENTINEL, new LinkedList<>(), pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize) {
      return this.getAll(branch, viewId, new LinkedList<>(), pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, new LinkedList<>(), pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId,
         FollowRelation.followList(CoreRelationTypes.PartitionDefToPartitionResult_PartitionResult), pageNum, pageSize,
         orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId,
      List<FollowRelation> followRelations, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAll(branch, followRelations, pageNum, pageSize, orderByAttribute, viewId);
      } catch (Exception ex) {
         return new LinkedList<CoverageProgramToken>();
      }
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, Collection<FollowRelation> followRelations,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.accessor.getAll(branch, followRelations, filter, attributes, pageCount, pageSize, orderByAttribute,
         ArtifactId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute, boolean activeOnly) {
      try {
         Collection<CoverageProgramToken> sets = this.accessor.getAll(branch, pageNum, pageSize, orderByAttribute);
         if (activeOnly) {
            sets = sets.stream().filter(set -> set.getActive()).collect(Collectors.toList());
         }
         return sets;
      } catch (Exception ex) {
         return new LinkedList<CoverageProgramToken>();
      }
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter,
      Collection<AttributeTypeId> searchAttributes) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, searchAttributes, 0L, 0L,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.getAllByFilter(branch, viewId, filter, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      long pageNum, long pageSize) {
      return this.getAllByFilter(branch, viewId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, attributes, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      Collection<AttributeTypeId> searchAttributes, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAllByFilter(branch, filter, searchAttributes, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<CoverageProgramToken>();
      }
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, String filter,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      Collection<AttributeTypeId> followAttributes) {
      try {
         return this.accessor.getAllByFilter(branch, filter, followRelations, pageCount, pageSize, orderByAttribute,
            followAttributes, ArtifactId.SENTINEL);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         Collection<CoverageProgramToken> failedList = new LinkedList<>();
         failedList.add(new CoverageProgramToken(ex));
         return failedList;
      }
   }

   @Override
   public Collection<CoverageProgramToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute, boolean activeOnly) {
      try {
         Collection<CoverageProgramToken> sets =
            this.accessor.getAllByFilter(branch, filter, attributes, pageNum, pageSize, orderByAttribute);
         if (activeOnly) {
            sets = sets.stream().filter(set -> set.getActive()).collect(Collectors.toList());
         }
         return sets;
      } catch (Exception ex) {
         return new LinkedList<CoverageProgramToken>();
      }
   }

   @Override
   public int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name), viewId);
   }

}
