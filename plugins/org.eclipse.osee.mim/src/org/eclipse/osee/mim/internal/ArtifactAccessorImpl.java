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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.mim.ArtifactAccessor;
import org.eclipse.osee.mim.annotations.OseeArtifactAttribute;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Luciano T. Vaglienti
 * @param <T> Class for storing/presenting artifact
 */
public class ArtifactAccessorImpl<T> implements ArtifactAccessor<T> {
   private ArtifactTypeToken artifactType = ArtifactTypeToken.SENTINEL;
   private final OrcsApi orcsApi;

   public ArtifactAccessorImpl(ArtifactTypeToken artifactType, OrcsApi orcsApi) {
      this.setArtifactType(artifactType);
      this.orcsApi = orcsApi;
   }

   @Override
   public T get(BranchId branch, ArtifactId artId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactReadable artifact =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).andId(artId).asArtifactOrSentinel();
      if (artifact.isValid()) {
         return clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
      }
      return clazz.newInstance();
   }

   @Override
   public Collection<T> getAll(BranchId branch, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         artifactType).asArtifacts()) {
         if (artifact.isValid()) {
            artifactList.add(clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact));
         }
      }
      return artifactList;
   }

   @Override
   public T getByRelation(BranchId branch, ArtifactId artId, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      ArtifactReadable artifact = orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(relation, relatedId).andId(
         artId).asArtifactOrSentinel();
      if (artifact.isValid()) {
         return clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
      }
      return clazz.newInstance();
   }

   @Override
   public Collection<T> getAllByRelation(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         artifactType).andRelatedTo(relation, relatedId).asArtifacts()) {
         if (artifact.isValid()) {
            artifactList.add(clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact));
         }
      }
      return artifactList;
   }

   @Override
   public Collection<T> getAllByFilter(BranchId branch, String filter, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      for (Field field : clazz.getDeclaredFields()) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactAttribute.class)) {
            if (field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId() != -1) {
               attributes.add(
                  AttributeTypeId.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId()));
            }
         }
      }

      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).and(
         attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
         QueryOption.TOKEN_MATCH_ORDER__ANY).getResults().getList()) { //asArtifacts() doesn't work for and() currently
         if (artifact.isValid()) {
            artifactList.add(clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact));
         }
      }
      return artifactList;
   }

   @Override
   public Collection<T> getAllByRelationAndFilter(BranchId branch, RelationTypeSide relation, ArtifactId relatedId, String filter, Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
      List<T> artifactList = new LinkedList<T>();
      List<AttributeTypeId> attributes = new LinkedList<AttributeTypeId>();
      for (Field field : clazz.getDeclaredFields()) {
         field.setAccessible(true);
         if (field.isAnnotationPresent(OseeArtifactAttribute.class)) {
            if (field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId() != -1) {
               attributes.add(
                  AttributeTypeId.valueOf(field.getDeclaredAnnotation(OseeArtifactAttribute.class).attributeId()));
            }
         }
      }
      for (ArtifactReadable artifact : orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(relation,
         relatedId).and(attributes, filter, QueryOption.TOKEN_DELIMITER__ANY, QueryOption.CASE__IGNORE,
            QueryOption.TOKEN_MATCH_ORDER__ANY).getResults().getList()) { //asArtifacts() doesn't work for and() currently
         if (artifact.isValid()) {
            artifactList.add(clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact));
         }
      }
      return artifactList;
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
      ArtifactReadable artifact =
         orcsApi.getQueryFactory().fromBranch(branch).andRelatedTo(relation, relatedId).asArtifactOrSentinel();
      if (artifact.isValid()) {
         return clazz.getDeclaredConstructor(ArtifactReadable.class).newInstance(artifact);
      }
      return clazz.newInstance();
   }

}
