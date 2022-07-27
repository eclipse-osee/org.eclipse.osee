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
import org.eclipse.osee.mim.ArtifactAccessor;
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

   public ArtifactAccessorImpl(ArtifactTypeToken artifactType, OrcsApi orcsApi) {
      this.setArtifactType(artifactType);
      this.orcsApi = orcsApi;
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.get(branch, artId, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAll(BranchId branch, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAll(branch, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getByRelation(branch, artId, relation, relatedId, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByRelation(branch, relation, relatedId, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByFilter(branch, filter, attributes, new LinkedList<RelationTypeSide>(), clazz);
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
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
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), false, clazz);
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andRelatedTo(relation,
            relatedId).andId(artId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      ArtifactReadable artifact = query.asArtifactOrSentinel();
      if (artifact.isValid()) {
         T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
         if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
            getSetApplic(clazz).invoke(returnObj,
               orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artId, branch));
         }
         return returnObj;
      }
      return clazz.getDeclaredConstructor().newInstance();
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(
         artifactType).andRelatedTo(relation, relatedId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid()) {
            T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
               getSetApplic(clazz).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType).andId(
            artId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      ArtifactReadable artifact = query.asArtifactOrSentinel();
      if (artifact.isValid()) {
         T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
         if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
            getSetApplic(clazz).invoke(returnObj,
               orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artId, branch));
         }
         return returnObj;
      }
      return clazz.getDeclaredConstructor().newInstance();
   }

   @Override
   public Collection<T> getAll(BranchId branch, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid()) {
            T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
               getSetApplic(clazz).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();

      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andIsOfType(artifactType).and(
            attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid() && !query.areApplicabilityTokensIncluded()) {
            T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(clazz)) {
               getSetApplic(clazz).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   @Override
   public T getByRelationWithoutId(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andRelatedTo(relation, relatedId);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      ArtifactReadable artifact = query.asArtifactOrSentinel();
      if (artifact.isValid()) {
         T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
         if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
            getSetApplic(clazz).invoke(returnObj,
               orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
         }
         return returnObj;
      }
      return clazz.getDeclaredConstructor().newInstance();
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Collection<AttributeTypeId> attributes, Collection<RelationTypeSide> followRelations, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).includeApplicabilityTokens().andRelatedTo(relation,
            relatedId).and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
               QueryOption.TOKEN_MATCH_ORDER__ANY);
      for (RelationTypeSide rel : followRelations) {
         query = query.follow(rel);
      }
      for (ArtifactReadable artifact : query.asArtifacts()) {
         if (artifact.isValid()) {
            T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(clazz) && !query.areApplicabilityTokensIncluded()) {
               getSetApplic(clazz).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, Collection<RelationTypeSide> followRelations, boolean isExact, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
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
      for (RelationTypeSide rel : followRelations) {
         executeQuery = executeQuery.follow(rel);
      }
      for (ArtifactReadable artifact : executeQuery.asArtifacts()) {
         if (artifact.isValid()) {
            T returnObj = clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
            if (hasSetApplic(clazz) && !executeQuery.areApplicabilityTokensIncluded()) {
               getSetApplic(clazz).invoke(returnObj,
                  orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityToken(artifact, branch));
            }
            artifactList.add(returnObj);
         }
      }
      return artifactList;
   }

   @Override
   public Collection<T> getAllByQuery(BranchId branch, MimAttributeQuery query, boolean isExact, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      return this.getAllByQuery(branch, query, new LinkedList<RelationTypeSide>(), isExact, clazz);
   }

}
