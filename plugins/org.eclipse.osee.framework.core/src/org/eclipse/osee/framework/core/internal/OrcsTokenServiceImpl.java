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

package org.eclipse.osee.framework.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.CoreTypeTokenProvider;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public final class OrcsTokenServiceImpl implements OrcsTokenService {
   private final Map<Long, ArtifactTypeToken> artifactTypes = new ConcurrentHashMap<>();
   private final Map<Long, AttributeTypeGeneric<?>> attributeTypes = new ConcurrentHashMap<>();
   private final Map<Long, RelationTypeToken> relationTypes = new ConcurrentHashMap<>();

   /**
    * Register core types first to prevent their ids from being registered by mistaken or malicious code
    */
   public OrcsTokenServiceImpl() {
      new CoreTypeTokenProvider().registerTypes(this);
   }

   public void addTypeTokenProvider(OrcsTypeTokenProvider typeProvider) {
      typeProvider.registerTypes(this);
   }

   @Override
   public ArtifactTypeToken getArtifactType(Long id) {
      ArtifactTypeToken artifactType = artifactTypes.get(id);
      if (artifactType == null) {
         throw new OseeTypeDoesNotExist("Artifact type [%s] is not available.", id);
      }
      return artifactType;
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeType(Long id) {
      AttributeTypeGeneric<?> attributeType = attributeTypes.get(id);
      if (attributeType == null) {
         throw new OseeTypeDoesNotExist("Attribute type [%s] is not available.", id);
      }
      return attributeType;
   }

   @Override
   public RelationTypeToken getRelationType(Long id) {
      RelationTypeToken relationType = relationTypes.get(id);
      if (relationType == null) {
         throw new OseeTypeDoesNotExist("Relation type [%s] is not available.", id);
      }
      return relationType;
   }

   @Override
   public RelationTypeToken getRelationType(String name) {
      for (RelationTypeToken relationType : relationTypes.values()) {
         if (relationType.getName().equals(name)) {
            return relationType;
         }
      }
      throw new OseeTypeDoesNotExist("Relation type [%s] is not available.", name);
   }

   @Override
   public ArtifactTypeToken getArtifactTypeOrSentinel(Long id) {
      ArtifactTypeToken artifactType = artifactTypes.get(id);
      if (artifactType == null) {
         return ArtifactTypeToken.SENTINEL;
      }
      return artifactType;
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeTypeOrSentinel(Long id) {
      AttributeTypeGeneric<?> attributeType = attributeTypes.get(id);
      if (attributeType == null) {
         return AttributeTypeGeneric.SENTINEL;
      }
      return attributeType;
   }

   @Override
   public RelationTypeToken getRelationTypeOrSentinel(Long id) {
      RelationTypeToken relationType = relationTypes.get(id);
      if (relationType == null) {
         return RelationTypeToken.SENTINEL;
      }
      return relationType;
   }

   @Override
   public ArtifactTypeToken getArtifactTypeOrCreate(Long id) {
      ArtifactTypeToken artifactType = getArtifactTypeOrSentinel(id);
      if (artifactType.isInvalid()) {
         artifactType = ArtifactTypeToken.valueOf(id, "Mising Artifact Type " + id);
         registerArtifactType(artifactType);
      }
      return artifactType;
   }

   @Override
   public AttributeTypeGeneric<?> getAttributeTypeOrCreate(Long id) {
      AttributeTypeGeneric<?> attributeType = getAttributeTypeOrSentinel(id);
      if (attributeType.isInvalid()) {
         String missing = "Mising Attribute Type " + id;
         attributeType = AttributeTypeToken.valueOf(id, missing, missing);
         registerAttributeType(attributeType);
      }
      return attributeType;
   }

   @Override
   public RelationTypeToken getRelationTypeOrCreate(Long id) {
      RelationTypeToken relationType = getRelationTypeOrSentinel(id);
      if (relationType.isInvalid()) {
         relationType = RelationTypeToken.create(id, "Mising Artifact Type " + id);
         registerRelationType(relationType);
      }
      return relationType;
   }

   @Override
   public void registerArtifactType(ArtifactTypeToken artifactType) {
      ArtifactTypeToken existingType = artifactTypes.putIfAbsent(artifactType.getId(), artifactType);
      if (existingType != null) {
         throw new OseeArgumentException("An artifact type %s with the same id as %s has already been registered.",
            existingType, artifactType);
      }
   }

   @Override
   public void registerAttributeType(AttributeTypeGeneric<?> attributeType) {
      AttributeTypeGeneric<?> existingType = attributeTypes.putIfAbsent(attributeType.getId(), attributeType);
      if (existingType != null) {
         throw new OseeArgumentException("The attribute type %s with the same id as %s has already been registered.",
            existingType, attributeType);
      }
   }

   @Override
   public void registerRelationType(RelationTypeToken relationType) {
      RelationTypeToken existingType = relationTypes.putIfAbsent(relationType.getId(), relationType);
      if (existingType != null) {
         throw new OseeArgumentException("The relation type %s with the same id as %s has already been registered.",
            existingType, relationType);
      }
   }

   @Override
   public Iterable<AttributeTypeGeneric<?>> getTaggedAttrs() {
      Set<AttributeTypeGeneric<?>> attrTypeIds = new HashSet<>();
      for (Map.Entry<Long, AttributeTypeGeneric<?>> entry : attributeTypes.entrySet()) {
         AttributeTypeGeneric<?> attrType = entry.getValue();
         if (attrType.isTaggable()) {
            attrTypeIds.add(attrType);
         }
      }
      return attrTypeIds;
   }

   @Override
   public Collection<ArtifactTypeToken> getArtifactTypes() {
      return Collections.unmodifiableCollection(artifactTypes.values());
   }

   @Override
   public Collection<RelationTypeToken> getRelationTypes() {
      return Collections.unmodifiableCollection(relationTypes.values());
   }

   @Override
   public Collection<AttributeTypeGeneric<?>> getAttributeTypes() {
      return Collections.unmodifiableCollection(attributeTypes.values());
   }

   @Override
   public List<RelationTypeToken> getValidRelationTypes(ArtifactTypeToken artifactType) {
      Collection<RelationTypeToken> relationTypes = getRelationTypes();
      List<RelationTypeToken> validRelationTypes = new ArrayList<>();
      for (RelationTypeToken relationType : relationTypes) {
         boolean onSideA = relationType.getRelationSideMax(artifactType, RelationSide.SIDE_A) > 0;
         boolean onSideB = relationType.getRelationSideMax(artifactType, RelationSide.SIDE_B) > 0;
         if (onSideA || onSideB) {
            validRelationTypes.add(relationType);
         }
      }
      return validRelationTypes;
   }
}