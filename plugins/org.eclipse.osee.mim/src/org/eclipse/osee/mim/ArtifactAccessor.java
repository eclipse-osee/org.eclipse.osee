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
package org.eclipse.osee.mim;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.orcs.core.ds.FollowRelation;

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class used for storing/presenting artifact information
 */
public interface ArtifactAccessor<T> {

   T get(BranchId branch, ArtifactId artId) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T get(BranchId branch, ArtifactId artId, Collection<FollowRelation> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   T get(BranchId branch, ArtifactId artId, Collection<FollowRelation> followRelations, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> get(BranchId branch, Collection<ArtifactId> artIds)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> get(BranchId branch, Collection<ArtifactId> artIds, Collection<FollowRelation> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, ArtifactId viewId) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute, ArtifactId viewId) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<FollowRelation> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, long pageCount,
      long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   int getAllByFilterAndCount(BranchId branch, String filter, Collection<AttributeTypeId> attributes);

   int getAllByFilterAndCount(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      ArtifactId viewId);

   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, long pageCount,
      long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<FollowRelation> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter,
      Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   int getAllByRelationAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   int getAllByRelationAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount,
      long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute, ArtifactId viewId) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations,
      Collection<AttributeTypeId> followAttributes) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<FollowRelation> followRelations,
      Collection<AttributeTypeId> followAttributes, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<FollowRelation> followRelations,
      boolean isExact) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<FollowRelation> followRelations,
      boolean isExact, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<FollowRelation> followRelations,
      boolean isExact, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<FollowRelation> followRelations,
      boolean isExact, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException;

   Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId,
      Collection<RelationTypeSide> relations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
}
