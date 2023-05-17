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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.types.ArtifactMatch;
import org.eclipse.osee.mim.types.MimAttributeQuery;
import org.eclipse.osee.mim.types.MimAttributeQueryElement;
import org.eclipse.osee.mim.types.MimRelatedArtifact;
import org.eclipse.osee.mim.types.PLGenericDBObject;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class for storing/presenting artifact
 */
public class ArtifactAccessorImpl<T extends PLGenericDBObject> implements ArtifactAccessor<T> {
   private ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   private final OrcsApi orcsApi;

   private Class<T> getType() {
      return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
   }

   public ArtifactAccessorImpl(ArtifactTypeToken artifactType, OrcsApi orcsApi) {
      this.setArtifactType(artifactType);
      this.orcsApi = orcsApi;
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.get(branch, artId, new LinkedList<RelationTypeSide>());
   }

   @Override
   public Collection<T> getAll(BranchId branch, Class<T> clazz) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getByRelation(branch, artId, relation, relatedId, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>(), clazz);
   }

   /**
    * @return the artifactType
    */
   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   /**
    * @param artifactType the artifactType to set
    */
   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   @Override
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getByRelationWithoutId(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), clazz);
   }

   private boolean hasSetApplic(Class<?> type) {
      if (getSetApplic(type) != null) {
         return true;
      }
      return false;
   }

   private Method getSetApplic(Class<?> type) {
      for (Method method : type.getMethods()) {
         if (method.getName().startsWith("set") && method.getParameterTypes().length == 1 && void.class.equals(
            method.getReturnType())) {
            //is a setter
            if (method.getName().endsWith("Applicability")) {
               return method;
            }
         }
      }
      return null;
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), false, clazz);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getByRelation(branch, artId, relation, relatedId, followRelations);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, followRelations);
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.get(branch, artId, followRelations);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, followRelations);
   }

   @Override
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getByRelationWithoutId(branch, relation, relatedId, followRelations);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query,
      Collection<RelationTypeSide> followRelations, boolean isExact, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, followRelations, isExact);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact, clazz);
   }

   private Collection<T> fetchCollection(QueryBuilder query, BranchId branch)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid()) {
            T returnObj = this.getType().getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(this.getType()) && !query.areApplicabilityTokensIncluded()) {
               getSetApplic(this.getType()).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   private T fetchSingle(QueryBuilder query, BranchId branch) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactReadable artifact = query.asArtifactOrSentinel();
      if (artifact.isValid()) {
         T returnObj = this.getType().getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
         if (hasSetApplic(this.getType()) && !query.areApplicabilityTokensIncluded()) {
            getSetApplic(this.getType()).invoke(returnObj,
               orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
         }
         return returnObj;
      }
      return this.getType().getDeclaredConstructor().newInstance();
   }

   @Override
   public Collection<ArtifactMatch> getAffectedArtifacts(BranchId branch, ArtifactId relatedId,
      Collection<RelationTypeSide> relations) throws IllegalArgumentException, SecurityException {
      List<ArtifactMatch> artifactList = new LinkedList<ArtifactMatch>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      for (RelationTypeSide relation : relations) {
         query = query.andRelatedTo(relation, relatedId);
      }
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid()) {
            artifactList.add(new ArtifactMatch(artifact));
         }
      }
      return artifactList;
   }

   @Override
   public T get(BranchId branch, ArtifactId artId) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.get(branch, artId, new LinkedList<RelationTypeSide>());
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Collection<RelationTypeSide> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType).andId(
            artId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchSingle(query, branch);
   }

   @Override
   public Collection<T> getAll(BranchId branch) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<RelationTypeSide>());
   }

   @Override
   public Collection<T> getAll(BranchId branch, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<>(), "", new LinkedList<>(), 0L, 0L, AttributeTypeId.SENTINEL, viewId);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, 0L, 0L);
   }

   @Override
   public Collection<T> getAll(BranchId branch, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<RelationTypeSide>(), pageCount, pageSize);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, long pageCount,
      long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, pageCount, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<T> getAll(BranchId branch, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAll(BranchId branch, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<RelationTypeSide>(), pageCount, pageSize, orderByAttribute);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, "", new LinkedList<>(), pageCount, pageSize, orderByAttribute);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, "", new LinkedList<>(), pageCount, pageSize, orderByAttribute,
         viewId);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAll(branch, followRelations, filter, attributes, pageCount, pageSize, orderByAttribute,
         ArtifactId.SENTINEL);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, String filter,
      Collection<AttributeTypeId> attributes, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch, viewId).includeApplicabilityTokens().andIsOfType(artifactType);
      if (Strings.isValid(filter)) {
         query = query.and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY);
      }
      if (orderByAttribute != null && orderByAttribute.isValid()) {
         query = query.setOrderByAttribute(orderByAttribute);
      }
      if (pageCount != 0L && pageSize != 0L) {
         query = query.isOnPage(pageCount, pageSize);
      }
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchCollection(query, branch);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>());
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, 0L, 0L);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>(), pageCount, pageSize);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<RelationTypeSide> followRelations, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, followRelations, pageCount, pageSize,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>(), orderByAttribute);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<RelationTypeSide> followRelations, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, followRelations, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>(), pageCount, pageSize,
         orderByAttribute);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes,
      Collection<RelationTypeSide> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType).and(
            attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY);
      if (orderByAttribute != null && orderByAttribute.isValid()) {
         query = query.setOrderByAttribute(orderByAttribute);
      }
      if (pageCount != 0L && pageSize != 0L) {
         query = query.isOnPage(pageCount, pageSize);
      }
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchCollection(query, branch);
   }

   @Override
   public int getAllByFilterAndCount(BranchId branch, String filter, Collection<AttributeTypeId> attributes) {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType);
      if (Strings.isValid(filter)) {
         query = query.and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY);
      }
      return query.getCount();
   }

   @Override
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getByRelationWithoutId(branch, relation, relatedId, new LinkedList<RelationTypeSide>());
   }

   @Override
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andRelatedTo(relation, relatedId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchSingle(query, branch);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getByRelation(branch, artId, relation, relatedId, new LinkedList<RelationTypeSide>());
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getByRelation(branch, artId, relation, relatedId, followRelations, ArtifactId.SENTINEL);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, ArtifactId viewId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch, viewId).includeApplicabilityTokens().andRelatedTo(relation,
            relatedId).andId(artId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchSingle(query, branch);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>());
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, followRelations, 0L, 0L);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), pageCount,
         pageSize);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, followRelations, pageCount, pageSize,
         AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, followRelations, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), pageCount, pageSize,
         orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, "", new LinkedList<>(), followRelations, pageCount,
         pageSize, orderByAttribute, new LinkedList<>(), ArtifactId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      Collection<RelationTypeSide> followRelations, long pageCount, long pageSize, AttributeTypeId orderByAttribute,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, "", new LinkedList<>(), followRelations, pageCount,
         pageSize, orderByAttribute, new LinkedList<>(), viewId);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch, viewId).includeApplicabilityTokens().andIsOfType(
            artifactType).andRelatedTo(relation, relatedId);
      if (Strings.isValid(filter)) {
         query = query.and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY);
         if (followAttributes.size() > 0) {
            query = query.followSearch(followAttributes, filter);
         }
      }
      if (orderByAttribute != null && orderByAttribute.isValid()) {
         query = query.setOrderByAttribute(orderByAttribute);
      }
      if (pageCount != 0L && pageSize != 0L) {
         query = query.isOnPage(pageCount, pageSize);
      }
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchCollection(query, branch);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>());
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations, 0L, 0L);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>(), 0L, 0L);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      long pageCount, long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations, pageCount,
         pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>(), orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations, 0L, 0L,
         orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>(), 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations, pageCount,
         pageSize, orderByAttribute, new LinkedList<AttributeTypeId>());
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByRelationAndFilter(branch, relation, relatedId, filter, attributes, followRelations, pageCount,
         pageSize, orderByAttribute, followAttributes, ArtifactId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      long pageCount, long pageSize, AttributeTypeId orderByAttribute, Collection<AttributeTypeId> followAttributes,
      ArtifactId viewId) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch, viewId).includeApplicabilityTokens().andRelatedTo(relation,
            relatedId).and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
               QueryOption.TOKEN_MATCH_ORDER__ANY);
      if (followAttributes.size() > 0) {
         query = query.followSearch(followAttributes, filter);
      }
      if (orderByAttribute != null && orderByAttribute.isValid()) {
         query = query.setOrderByAttribute(orderByAttribute);
      }
      if (pageCount != 0L && pageSize != 0L) {
         query = query.isOnPage(pageCount, pageSize);
      }
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return fetchCollection(query, branch);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, false);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query,
      Collection<RelationTypeSide> followRelations, boolean isExact)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, followRelations, isExact, 0L, 0L);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), false, pageCount, pageSize);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, long pageCount,
      long pageSize) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact, pageCount, pageSize);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query,
      Collection<RelationTypeSide> followRelations, boolean isExact, long pageCount, long pageSize)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, followRelations, isExact, pageCount, pageSize, AttributeTypeId.SENTINEL);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, false, orderByAttribute);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return null;
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query,
      Collection<RelationTypeSide> followRelations, boolean isExact, AttributeTypeId orderByAttribute)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
      NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact, 0L, 0L, orderByAttribute);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), false, pageCount, pageSize,
         orderByAttribute);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, long pageCount,
      long pageSize, AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException,
      IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact, pageCount, pageSize,
         orderByAttribute);
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query,
      Collection<RelationTypeSide> followRelations, boolean isExact, long pageCount, long pageSize,
      AttributeTypeId orderByAttribute) throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
      /**
       * Perform a query using the: relation defined in query.getRelated()(if it exists) attribute type id list defined
       * in query.getQueries() value list defined in query.getQueries()
       */
      QueryOption[] queryOptions = isExact ? QueryOption.EXACT_MATCH_OPTIONS : QueryOption.CONTAINS_MATCH_OPTIONS;
      QueryBuilder executeQuery =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType);
      if (!query.getRelated().equals(MimRelatedArtifact.SENTINEL)) {
         executeQuery = executeQuery.andRelatedTo(RelationTypeSide.create(query.getRelated().getRelation(),
            RelationSide.fromString(query.getRelated().getSide())), query.getRelated().getRelatedId());
      }
      for (MimAttributeQueryElement q : query.getQueries()) {
         executeQuery = executeQuery.and(q.getAttributeId(), q.getValue(), queryOptions);
      }
      if (orderByAttribute != null && orderByAttribute.isValid()) {
         executeQuery = executeQuery.setOrderByAttribute(orderByAttribute);
      }
      if (pageCount != 0L && pageSize != 0L) {
         executeQuery = executeQuery.isOnPage(pageCount, pageSize);
      }
      for (RelationTypeSide rel : followRelations) {
         executeQuery = executeQuery.follow(rel);
      }
      return fetchCollection(executeQuery, branch);
   }

   @Override
   public int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes) throws IllegalArgumentException, SecurityException {
      return this.getAllByRelationAndFilterAndCount(branch, relation, relatedId, filter, attributes,
         new LinkedList<RelationTypeSide>());
   }

   @Override
   public int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations)
      throws IllegalArgumentException, SecurityException {
      return this.getAllByRelationAndFilterAndCount(branch, relation, relatedId, filter, attributes, followRelations);
   }

   @Override
   public int getAllByRelationAndFilterAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId,
      String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations,
      Collection<AttributeTypeId> followAttributes) throws IllegalArgumentException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andRelatedTo(relation,
            relatedId).and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
               QueryOption.TOKEN_MATCH_ORDER__ANY);
      if (followAttributes.size() > 0) {
         query = query.followSearch(followAttributes, filter);
      }
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      return query.getCount();
   }

   @Override
   public int getAllByRelationAndCount(BranchId branch, RelationTypeSide relation, ArtifactId relatedId)
      throws IllegalArgumentException, SecurityException {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(
         artifactType).andRelatedTo(relation, relatedId);
      return query.getCount();
   }

}
