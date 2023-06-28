/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.framework.core;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ArtifactTypeJoin;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeJoin;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Ryan D. Brooks
 */
public interface OrcsTokenService {

   BranchToken getBranch(BranchId branch);

   /**
    * @return singleton full artifact type token with the given id or throw OseeTypeDoesNotExist if not found
    */
   ArtifactTypeToken getArtifactType(Long id);

   /**
    * @return singleton full artifact type token with the given name or throw OseeTypeDoesNotExist if not found
    */
   ArtifactTypeToken getArtifactType(String name);

   /**
    * @return singleton full attribute type token with the given id or throw OseeTypeDoesNotExist if not found
    */
   AttributeTypeGeneric<?> getAttributeType(Long id);

   /**
    * @return singleton full attribute type token with the given id or throw OseeTypeDoesNotExist if not found
    */
   AttributeTypeGeneric<?> getAttributeType(AttributeTypeId id);

   /**
    * @return singleton full attribute type token with the given name or throw OseeTypeDoesNotExist if not found
    */
   AttributeTypeGeneric<?> getAttributeType(String name);

   /**
    * @return singleton full relation type token with the given id or throw OseeTypeDoesNotExist if not found
    */
   RelationTypeToken getRelationType(Long id);

   /**
    * @return singleton full relation type token with the given name or throw OseeTypeDoesNotExist if not found
    */
   RelationTypeToken getRelationType(String name);

   /**
    * @return singleton full artifact type token with the given id or sentinel if not found
    */
   ArtifactTypeToken getArtifactTypeOrSentinel(Long id);

   /**
    * @return singleton full attribute type token with the given id or sentinel if not found
    */
   AttributeTypeGeneric<?> getAttributeTypeOrSentinel(Long id);

   /**
    * @return singleton full relation type token with the given id or sentinel if not found
    */
   RelationTypeToken getRelationTypeOrSentinel(Long id);

   /**
    * @return singleton full artifact type token with the given id or create it with default values and register with
    * this service
    */
   ArtifactTypeToken getArtifactTypeOrCreate(Long id);

   /**
    * @return singleton full attribute type token with the given id or create it with default values and register with
    * this service
    */
   AttributeTypeGeneric<?> getAttributeTypeOrCreate(Long id);

   /**
    * @return singleton full relation type token with the given id or create it with default values and register with
    * this service
    */
   RelationTypeToken getRelationTypeOrCreate(Long id);

   void registerTokenClasses(Class<?>[] tokenClasses);

   /**
    * Register the given artifact type token by its id. Throws OseeArgumentException if the types id is already
    * registered.
    */
   void registerArtifactType(ArtifactTypeToken artifactType);

   /**
    * Register the given attribute type token by its id. Throws OseeArgumentException if the types id is already
    * registered.
    */
   void registerAttributeType(AttributeTypeGeneric<?> attributeType);

   /**
    * Register the given relation type token by its id. Throws OseeArgumentException if that id is already registered.
    */
   void registerRelationType(RelationTypeToken relationType);

   /**
    * Get Iterable List of ids of Attributes that are taggable
    */
   Iterable<AttributeTypeGeneric<?>> getTaggedAttrs();

   /**
    * Get unmodifiable list of ArtifactTypes.
    */
   Collection<ArtifactTypeToken> getArtifactTypes();

   /**
    * Get unmodifiable list of RelationTypes.
    */
   Collection<RelationTypeToken> getRelationTypes();

   /**
    * Get unmodifiable list of AttributeTypes.
    */
   Collection<AttributeTypeGeneric<?>> getAttributeTypes();

   /**
    * Get List of non-abstract Artifact Types
    */
   List<ArtifactTypeToken> getConcreteArtifactTypes();

   /**
    * Get List of valid artifact types that contain a given attribute type
    */
   Set<ArtifactTypeToken> getValidArtifactTypes(AttributeTypeToken attributeType);

   /**
    * Get List of valid relation types that relate a given artifact type
    */
   List<RelationTypeToken> getValidRelationTypes(ArtifactTypeToken artifactType);

   /**
    * Register the given type join by its id. Throws OseeArgumentException if that id is already registered.
    */
   void registerArtifactTypeJoin(ArtifactTypeJoin typeJoin);

   /**
    * Register the given type join by its id. Throws OseeArgumentException if that id is already registered.
    */
   void registerAttributeTypeJoin(AttributeTypeJoin typeJoin);

   /**
    * Register the given type join by its id. Throws OseeArgumentException if that id is already registered.
    */
   void registerRelationTypeJoin(RelationTypeJoin typeJoin);

   ArtifactTypeJoin getArtifactTypeJoin(Long id);

   AttributeTypeJoin getAttributeTypeJoin(Long id);

   RelationTypeJoin getRelationTypeJoin(Long id);

   Collection<ArtifactTypeJoin> getArtifactTypeJoins();

   Collection<AttributeTypeJoin> getAttributeTypeJoins();

   Collection<RelationTypeJoin> getRelationTypeJoins();

   boolean attributeTypeExists(String name);

   Set<AttributeTypeToken> getSingletonAttributeTypes();

   /**
    * @return the Java type, usually an interface, that contains the static fields that define ORCS type tokens that
    * were registered by its OrcsTypeTokenProvider
    */
   Class<?> getTokenClass(String canonicalClassName);
}