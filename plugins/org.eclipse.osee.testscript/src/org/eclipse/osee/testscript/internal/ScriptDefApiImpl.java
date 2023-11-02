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

package org.eclipse.osee.testscript.internal;

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
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.core.ds.FollowRelation;
import org.eclipse.osee.testscript.ScriptDefApi;

/**
 * @author Stephen J. Molaro
 */
public class ScriptDefApiImpl implements ScriptDefApi {
   private ArtifactAccessor<ScriptDefToken> accessor;
   private final List<AttributeTypeId> attributes;
   public ScriptDefApiImpl(OrcsApi orcsApi) {
      this.setAccessor(new ScriptDefAccessor(orcsApi));
      attributes = new LinkedList<AttributeTypeId>();
      attributes.add(CoreAttributeTypes.Name);
   }

   private void setAccessor(ArtifactAccessor<ScriptDefToken> scriptDefTypeAccessor) {
      this.accessor = scriptDefTypeAccessor;
   }

   @Override
   public ScriptDefToken get(BranchId branch, ArtifactId scriptDefTypeId) {
      try {
         return this.accessor.get(branch, scriptDefTypeId);
      } catch (Exception ex) {
         System.out.println(ex);
         return new ScriptDefToken();
      }
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch) {
      return this.getAll(branch, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId) {
      return this.getAll(branch, viewId, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations) {
      return this.getAll(branch, viewId, followRelations, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, new LinkedList<>(), 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId, followRelations, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, long pageNum, long pageSize) {
      return this.getAll(branch, ArtifactId.SENTINEL, new LinkedList<>(), pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize) {
      return this.getAll(branch, viewId, new LinkedList<>(), pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, ArtifactId.SENTINEL, new LinkedList<>(), pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAll(branch, viewId,
         FollowRelation.followList(CoreRelationTypes.TestScriptDefToTestScriptResults_TestScriptResults), pageNum,
         pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, ArtifactId viewId, List<FollowRelation> followRelations,
      long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAll(branch, followRelations, pageNum, pageSize, orderByAttribute, viewId);
      } catch (Exception ex) {
         return new LinkedList<ScriptDefToken>();
      }
   }

   @Override
   public Collection<ScriptDefToken> getAll(BranchId branch, Collection<FollowRelation> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.accessor.getAll(branch, followRelations, filter, attributes, pageCount, pageSize, orderByAttribute,
         ArtifactId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter,
      Collection<AttributeTypeId> searchAttributes) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, searchAttributes, 0L, 0L,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.getAllByFilter(branch, viewId, filter, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize) {
      return this.getAllByFilter(branch, viewId, filter, pageNum, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter, long pageNum, long pageSize,
      AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, ArtifactId.SENTINEL, filter, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter, long pageNum,
      long pageSize, AttributeTypeId orderByAttribute) {
      return this.getAllByFilter(branch, viewId, filter, attributes, pageNum, pageSize, orderByAttribute);
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, ArtifactId viewId, String filter,
      Collection<AttributeTypeId> searchAttributes, long pageNum, long pageSize, AttributeTypeId orderByAttribute) {
      try {
         return this.accessor.getAllByFilter(branch, filter, searchAttributes, pageNum, pageSize, orderByAttribute);
      } catch (Exception ex) {
         return new LinkedList<ScriptDefToken>();
      }
   }

   @Override
   public Collection<ScriptDefToken> getAllByFilter(BranchId branch, String filter,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      Collection<AttributeTypeId> followAttributes) {
      try {
         return this.accessor.getAllByFilter(branch, filter, followRelations, pageCount, pageSize, orderByAttribute,
            followAttributes, ArtifactId.SENTINEL);
      } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
         | NoSuchMethodException | SecurityException ex) {
         System.out.println(ex);
      }
      return new LinkedList<>();
   }

   @Override
   public int getCountWithFilter(BranchId branch, ArtifactId viewId, String filter) {
      return this.accessor.getAllByFilterAndCount(branch, filter, Arrays.asList(CoreAttributeTypes.Name), viewId);
   }

}
