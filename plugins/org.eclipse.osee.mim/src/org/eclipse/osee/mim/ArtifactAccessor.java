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

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class used for storing/presenting artifact information
 */
public interface ArtifactAccessor<T> {

   /**
    * @deprecated
    */
   @Deprecated
   T get(BranchId branch, ArtifactId artId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   T get(BranchId branch, ArtifactId artId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T get(BranchId branch, ArtifactId artId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T get(BranchId branch, ArtifactId artId, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAll(BranchId branch, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   /**
    * @deprecated
    */
   @Deprecated
   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<RelationTypeSide> followRelations, boolean isExact, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<RelationTypeSide> followRelations, boolean isExact) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<RelationTypeSide> followRelations, boolean isExact, long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

   Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId, Collection<RelationTypeSide> relations) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;
}
